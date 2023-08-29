package com.desrielkiki.vaultdonation.ui.member

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.desrielkiki.vaultdonation.R
import com.desrielkiki.vaultdonation.databinding.FragmentMemberDetailBinding
import com.desrielkiki.vaultdonation.ui.SharedViewModel
import com.desrielkiki.vaultdonation.ui.donate.DonateViewModel
import com.desrielkiki.vaultdonation.ui.donate.DonationHistoryAdapter
import com.desrielkiki.vaultdonation.ui.util.formatDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MemberDetailFragment : Fragment() {

    private var _binding: FragmentMemberDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<MemberDetailFragmentArgs>()

    private val donateViewModel: DonateViewModel by viewModels()
    private val memberViewModel: MemberViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val historyAdapter: DonationHistoryAdapter by lazy { DonationHistoryAdapter() }

    private var isPrevButtonClickable = true
    private var isNextButtonClickable = true

    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMemberDetailBinding.inflate(inflater, container, false)
        binding.args = args

        displaySelectedWeekRange()
        setupNextAndPrevButton()
        setupHistoryRecyclerView()

        binding.fabAddDonate.setOnClickListener {
            val action =
                MemberDetailFragmentDirections.actionMemberDetailFragmentToMemberDonateFragment(args.memberData)
            findNavController().navigate(action)
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupNextAndPrevButton() {
        sharedViewModel.currentWeekAndMonth.observe(viewLifecycleOwner, Observer { weekAndMonth ->
            binding.tvWeekInfo.text = weekAndMonth

            binding.btnPrevWeek.setOnClickListener {
                isPrevButtonClickable = false
                sharedViewModel.moveToPreviousWeek()
                selectedMonth =
                    sharedViewModel.currentMonth.value ?: Calendar.getInstance().get(Calendar.MONTH)
                displaySelectedWeekRange()
            }

            binding.btnNextWeek.setOnClickListener {
                isNextButtonClickable = false
                sharedViewModel.moveToNextWeek()
                selectedMonth =
                    sharedViewModel.currentMonth.value ?: Calendar.getInstance().get(Calendar.MONTH)
                displaySelectedWeekRange()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> deleteMember()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteMember() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("yes") { _, _ ->
            memberViewModel.deleteMember(args.memberData)
            Toast.makeText(
                requireContext(),
                "Successfully Delete ${args.memberData.memberName}",
                Toast.LENGTH_SHORT
            )
                .show()
            findNavController().navigate(R.id.action_memberDetailFragment_to_memberFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setIcon(R.drawable.ic_warning)
        builder.setTitle("DELETE MEMBER")
        builder.setMessage("are you sure you want to delete this member?")
        builder.create().show()

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

        donateViewModel.getDonationsForWeekByMemberId(
            selectedStartWeekFormatted,
            selectedEndWeekFormatted,
            args.memberData.id
        ).observe(viewLifecycleOwner, Observer { donationData ->
            // Observe the donation data and apply filtering
            val filteredDonations =
                sharedViewModel.filterDonationsByMonth(donationData, selectedMonth)

            // Update RecyclerView with the filtered donation data
            sharedViewModel.emptyDatabaseView(filteredDonations,binding.ivNoData )
            historyAdapter.setData(filteredDonations)
        })
    }

    private fun setupHistoryRecyclerView() {
        val recyclerView = binding.rvDonationHistory
        recyclerView.adapter = historyAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
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
        displaySelectedWeekRange()
        setupNextAndPrevButton()
        setupHistoryRecyclerView()
        super.onResume()
    }
}