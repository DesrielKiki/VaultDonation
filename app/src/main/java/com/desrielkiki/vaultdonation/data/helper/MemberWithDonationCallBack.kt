package com.desrielkiki.vaultdonation.data.helper

import androidx.recyclerview.widget.DiffUtil
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData

class MemberWithDonationCallBack(
    private val oldList: List<MemberWithDonationData>,
    private val newList: List<MemberWithDonationData>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].memberData?.memberName == newList[newItemPosition].memberData?.memberName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}