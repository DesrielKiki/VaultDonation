package com.desrielkiki.vaultdonation.data.helper

import androidx.recyclerview.widget.DiffUtil
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData

class DonationCallback (
    private val oldList: List<MemberWithDonationData>,
    private val newList: List<MemberWithDonationData>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].donationData.id == newList[newItemPosition].donationData.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}