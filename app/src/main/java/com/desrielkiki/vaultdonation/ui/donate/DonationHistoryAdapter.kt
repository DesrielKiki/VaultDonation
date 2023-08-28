package com.desrielkiki.vaultdonation.ui.donate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.desrielkiki.vaultdonation.R
import com.desrielkiki.vaultdonation.data.entity.DonationType
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData
import com.desrielkiki.vaultdonation.data.helper.DonationCallback
import com.desrielkiki.vaultdonation.databinding.RowDonationHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DonationHistoryAdapter : RecyclerView.Adapter<DonationHistoryAdapter.HistoryViewHolder>() {

    private var donationList = emptyList<MemberWithDonationData>()

    class HistoryViewHolder(private val binding: RowDonationHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memberWithDonationData: MemberWithDonationData) {
            binding.memberWithDonationData = memberWithDonationData
            val formattedDate =
                customDateFormatFromString(memberWithDonationData.donationData.donationDate)
            binding.tvDonationDate.text = formattedDate
            binding.tvDonationType.text =
                memberWithDonationData.donationData.donationType.toString()
            if (memberWithDonationData.donationData.donationType == DonationType.Both) {
                val both = "Resource And GoldBar"
                binding.tvDonationType.text = both
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HistoryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowDonationHistoryBinding.inflate(layoutInflater, parent, false)
                return HistoryViewHolder(binding)
            }

            fun customDateFormatFromString(date: String): String {
                val inputDateFormat = SimpleDateFormat("dd, MM, yyyy", Locale.getDefault())
                val parsedDate = inputDateFormat.parse(date)

                val outputDateFormat = SimpleDateFormat("EEEE, d, MMMM, yyyy", Locale.getDefault())
                return outputDateFormat.format(parsedDate)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.row_donation_history, parent, false)
        return HistoryViewHolder.from(parent)
    }

    override fun getItemCount(): Int = donationList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentDonation = donationList[position]
        holder.bind(currentDonation)
    }

    fun setData(memberWithDonationData: List<MemberWithDonationData>) {
        val diffCallback = DonationCallback(donationList, memberWithDonationData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.donationList = memberWithDonationData
        diffResult.dispatchUpdatesTo(this)
    }
}