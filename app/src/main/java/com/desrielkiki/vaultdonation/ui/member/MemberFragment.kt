package com.desrielkiki.vaultdonation.ui.member

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.desrielkiki.vaultdonation.R
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.databinding.BottomSheetMemberBinding
import com.desrielkiki.vaultdonation.databinding.FragmentMemberBinding
import com.desrielkiki.vaultdonation.ui.SharedViewModel
import com.desrielkiki.vaultdonation.ui.util.MemberItemClickListener
import com.desrielkiki.vaultdonation.ui.util.snackbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.abs

class MemberFragment : Fragment(), SearchView.OnQueryTextListener, MemberItemClickListener {


    private var _binding: FragmentMemberBinding? = null
    private val binding get() = _binding!!

    private var memberItemClickListener: MemberItemClickListener? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetMemberBinding: BottomSheetMemberBinding

    private val viewModel: MemberViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private var currentPage: Int = 0
    private lateinit var memberAdapter: MemberAdapter

    private var actionMode: ActionMode? = null

    private val pageSize = 24
    private var memberPage = 0
    private var startNumber = (memberPage * 24) + 1

    private var isSearchMenuInitialized = false


    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        memberAdapter =
            MemberAdapter(memberItemClickListener ?: this, viewModel, findNavController())
        viewModel.getAllMember.observe(viewLifecycleOwner, Observer { memberData ->
            binding.tvTotalMembers.text = " ${memberData.size} members"
        })
        /**
         * initiate on swipe gesture
         */

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

        // Observe selected IDs and update adapter
        getPageData()
        viewModel.selectedIds.observe(viewLifecycleOwner) { selectedIds ->
            memberAdapter.setSelectedIds(selectedIds)
        }

        binding.fabAddMember.setOnClickListener {
            openBottomSheetMember()
        }

        setupRecyclerView()
        /*
                setupNextPrevButton()
        */
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmDeleteAll()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getPageData() {
        viewModel.loadPageData(currentPage, 24)
            .observe(viewLifecycleOwner, Observer { memberByPage ->
                sharedViewModel.emptyDatabaseView(memberByPage, binding.ivNoData)
                memberAdapter.setData(memberByPage, startNumber)
            })
    }

    private fun confirmDeleteAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("yes") { _, _ ->
            viewModel.deleteAllMember()
            Toast.makeText(
                requireContext(),
                "successfully removed all member",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete all list ?")
        builder.setMessage("are you sure you want to delete all list ?")
        builder.create().show()
    }


    private fun openBottomSheetMember() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetMemberBinding = BottomSheetMemberBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetMemberBinding.root)

        bottomSheetMemberBinding.btnAddMember.setOnClickListener {
            insertMember(0)
        }
        bottomSheetDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.member_menu, menu)

        if (!isSearchMenuInitialized) {
            val search = menu.findItem(R.id.menu_search)
            val searchView = search.actionView as? SearchView
            searchView?.isSubmitButtonEnabled = true
            searchView?.setOnQueryTextListener(this)
            isSearchMenuInitialized = true
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.rvMember
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = memberAdapter
    }

    /*    private fun setupNextPrevButton() {
            var allMember = 0
            viewModel.getAllMember.observe(viewLifecycleOwner) { memberData ->
                allMember = memberData.size
            }
            val btnNextClickListener = View.OnClickListener {
                if (currentPage + 24 < allMember) {
                    currentPage += 24
                    memberPage += 1
                }
                startNumber = (memberPage * 24) + 1
                viewModel.loadPageData(currentPage, pageSize).observe(viewLifecycleOwner) { newData ->
                    memberAdapter.setData(newData, startNumber)
                    Log.d("member fragment", "new data count = ${newData.size} ")
                }
                Log.d("member fragment", "member page = $memberPage")
                Log.d("member fragment", "current page = $currentPage")
            }
            val btnPrevClickListener = View.OnClickListener {
                if (currentPage > 0) {
                    currentPage -= 24
                    memberPage -= 1
                }
                startNumber = (memberPage * 24) + 1
                viewModel.loadPageData(currentPage, 24).observe(viewLifecycleOwner) { newData ->
                    memberAdapter.setData(newData, startNumber)
                }
                Log.d("member fragment", "member page = $memberPage")
                Log.d("member fragment", "current page = $currentPage")
            }
            binding.btnNext.setOnClickListener(btnNextClickListener)
            binding.btnPrev.setOnClickListener(btnPrevClickListener)
        }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * insert and validate function
     * */

    private fun insertMember(id: Int) {
        if (validateForm()) {
            val member = MemberData(0L, bottomSheetMemberBinding.etMemberName.text.toString())

            val successMessage = if (id == 0) {
                viewModel.insertMember(member)
                getString(R.string.member_add_succes)
            } else {
                viewModel.updateMember(member)
                getString(R.string.member_update_succes)
            }
            requireActivity().findViewById<View>(android.R.id.content)
                .snackbar(successMessage, R.id.fab_addMember).also { bottomSheetDialog.dismiss() }
        }
    }

    private fun validateForm(): Boolean {
        val textCategory = bottomSheetMemberBinding.etMemberName.text.toString()
        val isFormValid: Boolean

        if (textCategory.isEmpty()) {
            isFormValid = false
            bottomSheetMemberBinding.etMemberName.error = getString(R.string.member_add_error)
        } else isFormValid = true
        return isFormValid
    }

    /**
     * search function
     * */
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
            viewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
                sharedViewModel.emptyDatabaseView(list, binding.ivNoData)
                list?.let {
                    memberAdapter.setData(it, 1)
                }
            })
        } else {
            getPageData()
        }
    }

    /**
     * hold to select item function
     */
    override fun onItemLongClick(memberData: MemberData) {
        if (actionMode == null) {
            actionMode = requireActivity().startActionMode(actionModeCallback)
        }
        toggleSelection(memberData)
        requireActivity().invalidateOptionsMenu()

    }

    private fun toggleSelection(memberData: MemberData) {
        viewModel.toggleSelection(memberData)

        val selectedIds = viewModel.getSelectedItemsLiveData().value.orEmpty()
        val selectedCount = selectedIds.size
        if (selectedCount == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = "$selectedCount selected"
            actionMode?.invalidate()
        }
        // Memanggil invalidateOptionsMenu untuk memperbarui tampilan menu
        requireActivity().invalidateOptionsMenu()
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().menuInflater.inflate(R.menu.delete_menu, menu)
            viewModel.selectedIds.observe(viewLifecycleOwner) { selectedIds ->
                if (selectedIds.size == 1) {
                    val deleteMenuItem = menu?.findItem(R.id.menu_delete)
                    deleteMenuItem?.isVisible = selectedIds.isNotEmpty()
                }
            }
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            // No need to do anything here
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            if (item?.itemId == R.id.menu_delete) {
                deleteSelectedMembers(mode)
                currentPage = 0
                return true
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            viewModel.clearSelection()
            isSearchMenuInitialized = false
            viewModel.isSelectionModeActive.observe(viewLifecycleOwner) { isActive ->
                if (!isActive) {
                }
            }
        }
    }


    private fun deleteSelectedMembers(mode: ActionMode?) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setPositiveButton("yes") { _, _ ->
            val selectedIds = viewModel.getSelectedItemsLiveData().value.orEmpty()
            viewModel.deleteMemberById(selectedIds)

            Toast.makeText(
                requireContext(),
                "Successfully Delete selected member",
                Toast.LENGTH_SHORT
            )
                .show()
            mode?.finish()
            viewModel.clearSelection()
            memberPage = 0
            startNumber = 1
            currentPage = 0
            viewModel.loadPageData(currentPage, pageSize).observe(viewLifecycleOwner) { newData ->
                memberAdapter.notifyDataSetChanged()
                memberAdapter.setData(newData, startNumber)
                Log.d("member fragment", "current page = $currentPage")
                Log.d("member fragment", "new data count = ${newData.size} ")
            }
        }
        builder.setNegativeButton("No") { _, _ ->
        }
        builder.setIcon(R.drawable.ic_warning)
        builder.setTitle("DELETE SELECTED MEMBER")
        builder.setMessage("are you sure you want to delete selected member?")
        builder.create().show()

    }

    /**
     * swipe action function
     */

    private fun swipeToNextPage() {
        var allMember = 0
        viewModel.getAllMember.observe(viewLifecycleOwner) { memberData ->
            allMember = memberData.size
        }
        if (currentPage + 24 < allMember) {
            currentPage += 24
            memberPage += 1
        }
        startNumber = (memberPage * 24) + 1
        viewModel.loadPageData(currentPage, pageSize).observe(viewLifecycleOwner) { newData ->
            memberAdapter.setData(newData, startNumber)
        }
    }

    private fun swipeToPreviousPage() {
        if (currentPage > 0) {
            currentPage -= 24
            memberPage -= 1
        }
        startNumber = (memberPage * 24) + 1
        viewModel.loadPageData(currentPage, 24).observe(viewLifecycleOwner) { newData ->
            memberAdapter.setData(newData, startNumber)
        }
    }
}