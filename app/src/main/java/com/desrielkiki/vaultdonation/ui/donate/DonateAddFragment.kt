package com.desrielkiki.vaultdonation.ui.donate

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.desrielkiki.vaultdonation.R
import com.desrielkiki.vaultdonation.data.entity.DonationData
import com.desrielkiki.vaultdonation.databinding.FragmentDonateAddBinding
import com.desrielkiki.vaultdonation.ui.SharedViewModel
import com.desrielkiki.vaultdonation.ui.member.MemberViewModel
import com.desrielkiki.vaultdonation.ui.util.customDateFormatToString
import com.desrielkiki.vaultdonation.ui.util.formatDateToString
import com.desrielkiki.vaultdonation.ui.util.formatSimpleDateToString
import java.util.Calendar

class DonateAddFragment : Fragment() {

    private var _binding: FragmentDonateAddBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DonateAddFragmentArgs>()

    private val sharedViewModel: SharedViewModel by viewModels()
    private val memberViewModel: MemberViewModel by viewModels()

    private var donationDate: String = ""
    private val calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDonateAddBinding.inflate(inflater, container, false)
        binding.args = args
        donationDate = sharedViewModel.getCurrentDate()
        binding.tvCurrentDate.text =
            customDateFormatToString(calendar.time)
        binding.spDonationType.onItemSelectedListener = sharedViewModel.listener
        binding.tvCurrentDate.setOnClickListener {
            showDatePicker()
        }
        binding.btnAddDonate.setOnClickListener {
            insertDonationData()
        }
        return binding.root
    }

    private fun insertDonationData() {
        val memberId = args.memberData.id
        val donationDate = donationDate
        val donationType = binding.spDonationType.selectedItem.toString()

        val newDonate = DonationData(
            0, memberId, donationDate, sharedViewModel.parseDonationType(donationType)
        )
        memberViewModel.insertDonate(newDonate)
        Toast.makeText(requireContext(), "Successfully added donation", Toast.LENGTH_SHORT)
            .show()
        findNavController().navigate(R.id.action_memberDonateFragment_to_memberFragment)
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(), { _, selectedYear, selectedMonth, dayOfMonth ->
                donationDate =
                    formatSimpleDateToString(selectedYear, selectedMonth, dayOfMonth)
                val formattedDate = formatDateToString(donationDate)
                binding.tvCurrentDate.text = formattedDate
            }, year, month, day
        ).show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}