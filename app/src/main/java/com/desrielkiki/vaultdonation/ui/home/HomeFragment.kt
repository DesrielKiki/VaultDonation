package com.desrielkiki.vaultdonation.ui.home

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.desrielkiki.vaultdonation.R
import com.desrielkiki.vaultdonation.data.entity.DonationType
import com.desrielkiki.vaultdonation.databinding.FragmentHomeBinding
import com.desrielkiki.vaultdonation.ui.SharedViewModel
import com.desrielkiki.vaultdonation.ui.donate.DonateViewModel
import com.desrielkiki.vaultdonation.ui.member.MemberViewModel
import com.desrielkiki.vaultdonation.ui.util.formatDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

class HomeFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val memberViewModel: MemberViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val homeMemberAdapter: HomeMemberAdapter by lazy { HomeMemberAdapter() }

    private var isPrevWeekButtonClickable = true
    private var isNextWeekButtonClickable = true

    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
    private var currentPage: Int = 0
    private var startNumber: Int = 1

    private var navToNextPage: Boolean = false
    private var navToPreviousPage: Boolean = false

    private lateinit var gestureDetector: GestureDetector

    private val memberIdsWithDonations = mutableMapOf<Long, MutableSet<DonationType>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        setupRecyclerView()
        setupNextAndPrevWeekButton()
        setHasOptionsMenu(true)
        displaySelectedWeekRange()
    }

    private fun setupNextAndPrevWeekButton() {
        sharedViewModel.currentWeekAndMonth.observe(viewLifecycleOwner, Observer { weekAndMonth ->
            binding.tvWeekInfo.text = weekAndMonth
            homeMemberAdapter.weekAndMonth = weekAndMonth

            binding.btnPrevWeek.setOnClickListener {
                handlePrevWeekButtonClick()
            }

            binding.btnNextWeek.setOnClickListener {
                handleNextWeekButtonClick()
            }
        })
    }

    private fun handlePrevWeekButtonClick() {
        if (isPrevWeekButtonClickable) {
            isPrevWeekButtonClickable = false
            sharedViewModel.moveToPreviousWeek()
            selectedMonth =
                sharedViewModel.currentMonth.value ?: Calendar.getInstance().get(Calendar.MONTH)
            displaySelectedWeekRange()
            isPrevWeekButtonClickable = true

        }
    }

    private fun handleNextWeekButtonClick() {
        if (isNextWeekButtonClickable) {
            isNextWeekButtonClickable = false
            sharedViewModel.moveToNextWeek()
            selectedMonth =
                sharedViewModel.currentMonth.value ?: Calendar.getInstance().get(Calendar.MONTH)
            displaySelectedWeekRange()
            isNextWeekButtonClickable = true
        }
    }

    private fun displaySelectedWeekRange() {
        sharedViewModel.adjustWeeksIfDifferentMonths() // Memanggil adjustWeeksIfDifferentMonths() hanya sekali
        var selectedStartWeekFormatted = sharedViewModel.getStartOfWeekFormatted()
        val selectedEndWeekFormatted = sharedViewModel.getEndOfWeekFormatted()

        val dateFormat = SimpleDateFormat("dd, MM, yyyy", Locale.getDefault())
        val selectedStartWeek = dateFormat.parse(selectedStartWeekFormatted)
        val selectedEndWeek = dateFormat.parse(selectedEndWeekFormatted)

        val calendar = Calendar.getInstance()
        calendar.time = selectedStartWeek

        val startMonth = calendar.get(Calendar.MONTH)
        calendar.time = selectedEndWeek
        val endMonth = calendar.get(Calendar.MONTH)

        if (startMonth != endMonth) {
            // Jika bulan dari selectedStartWeek tidak sama dengan bulan dari selectedEndWeek
            // Set selectedStartWeek menjadi awal dari bulan selectedEndWeek
            calendar.time = selectedEndWeek
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startOfMonth = calendar.time
            selectedStartWeekFormatted = formatDate(startOfMonth, "dd, MM, yyyy")
        }
        memberViewModel.loadPageData(currentPage, 24)
            .observe(viewLifecycleOwner, Observer { memberByPage ->
                // Mendapatkan donasi dalam rentang minggu menggunakan ViewModel dan LiveData
                homeViewModel.getDonationForWeek(
                    selectedStartWeekFormatted,
                    selectedEndWeekFormatted
                )
                    .observe(viewLifecycleOwner, Observer { donationsForWeek ->
                        val filteredDonations =
                            sharedViewModel.filterDonationsByMonth(
                                donationsForWeek,
                                selectedMonth
                            )
                        memberIdsWithDonations.clear()

                        for (donation in filteredDonations) {
                            val memberId = donation.donationData.memberId
                            val donationType = donation.donationData.donationType

                            val existingDonationTypes =
                                memberIdsWithDonations.getOrPut(memberId) { mutableSetOf() }
                            existingDonationTypes.add(donationType)
                        }

                        // Mendapatkan daftar member yang belum melakukan donasi dalam rentang minggu
                        val membersWithoutDesiredDonations = memberByPage.filter { member ->
                            val memberId = member.id
                            val donationTypes = memberIdsWithDonations[memberId]

                            val hasBothDonation = donationTypes?.contains(DonationType.Both) == true
                            val hasResourceAndGoldDonations =
                                donationTypes?.contains(DonationType.Resource) == true &&
                                        donationTypes.contains(DonationType.GoldBar)

                            !hasBothDonation && !hasResourceAndGoldDonations
                        }
                        homeMemberAdapter.setData(membersWithoutDesiredDonations, startNumber)
                    })
            })

        fun updatePageAndLoadData(newPage: Int) {
            currentPage = newPage
            memberViewModel.loadPageData(currentPage, 24)
                .observe(viewLifecycleOwner) { memberByPage ->
                    homeViewModel.getDonationForWeek(
                        selectedStartWeekFormatted,
                        selectedEndWeekFormatted
                    ).observe(viewLifecycleOwner) { donationsForWeek ->

                        val filteredDonations =
                            sharedViewModel.filterDonationsByMonth(
                                donationsForWeek,
                                selectedMonth
                            )
                        memberIdsWithDonations.clear()

                        for (donation in filteredDonations) {
                            val memberId = donation.donationData.memberId
                            val donationType = donation.donationData.donationType

                            val existingDonationTypes =
                                memberIdsWithDonations.getOrPut(memberId) { mutableSetOf() }
                            existingDonationTypes.add(donationType)
                        }

                        val membersWithoutDesiredDonations = memberByPage.filter { member ->
                            val memberId = member.id
                            val donationTypes = memberIdsWithDonations[memberId]

                            val hasBothDonation = donationTypes?.contains(DonationType.Both) == true
                            val hasResourceAndGoldDonations =
                                donationTypes?.contains(DonationType.Resource) == true &&
                                        donationTypes.contains(DonationType.GoldBar)

                            !hasBothDonation && !hasResourceAndGoldDonations
                        }

                        if (membersWithoutDesiredDonations.isNotEmpty() && currentPage >= 0) {
                            homeMemberAdapter.setData(membersWithoutDesiredDonations, startNumber)
                            if (navToNextPage) {

                                startNumber += 24
                            } else if (navToPreviousPage) {

                                startNumber -= 24
                            }
                            homeMemberAdapter.updateMemberNumbers(startNumber)
                        } else {
                            currentPage = maxOf(0, currentPage - 24)
                        }
                        navToPreviousPage = false
                        navToNextPage = false

                    }
                }
        }
  /*      val btnNextClickListener = View.OnClickListener {
            navToNextPage = true
            updatePageAndLoadData(currentPage + 24)
        }
        val btnPrevClickListener = View.OnClickListener {
            if (currentPage >= 24) {
                navToPreviousPage = true
                updatePageAndLoadData(currentPage - 24)
            }
        }
        binding.btnNext.setOnClickListener(btnNextClickListener)
        binding.btnPrev.setOnClickListener(btnPrevClickListener)
    */
        fun swipeToNextPage() {
            navToNextPage = true
            updatePageAndLoadData(currentPage + 24)
        }

        fun swipeToPreviousPage() {
            if (currentPage >= 24) {
                navToPreviousPage = true
                updatePageAndLoadData(currentPage - 24)
            }
        }
        gestureDetector =
            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                private val MIN_SWIPE_DISTANCE = 100
                private val MIN_VELOCITY = 200

                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float,
                ): Boolean {
                    val distanceX = e2.x - e1.x
                    val distanceY = e2.y - e1.y

                    if (abs(distanceX) > abs(distanceY) &&
                        abs(distanceX) > MIN_SWIPE_DISTANCE &&
                        abs(velocityX) > MIN_VELOCITY
                    ) {
                        if (distanceX > 0) {
                            // Swipe ke kiri
                            swipeToPreviousPage()
                        } else {
                            // Swipe ke kanan
                            swipeToNextPage()
                        }
                    }
                    return true
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.rvMemberList
        recyclerView.adapter = homeMemberAdapter
        recyclerView.layoutManager = GridLayoutManager(context, 3)

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        isPrevWeekButtonClickable = true
        isNextWeekButtonClickable = true
        displaySelectedWeekRange()

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"

        if (searchQuery != "%%") {
            // Lakukan pencarian dan tampilkan hasilnya
            memberViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
                list?.let {
                    val filteredMembersWithoutDonations = list.filter { member ->
                        val memberId = member.id
                        val donationTypes = memberIdsWithDonations[memberId]

                        val hasBothDonation = donationTypes?.contains(DonationType.Both) == true
                        val hasResourceAndGoldDonations =
                            donationTypes?.contains(DonationType.Resource) == true &&
                                    donationTypes.contains(DonationType.GoldBar)

                        !hasBothDonation && !hasResourceAndGoldDonations
                    }
                    homeMemberAdapter.setData(filteredMembersWithoutDonations, startNumber)
                }
            })
        } else {
            displaySelectedWeekRange()
        }

    }
}