package com.desrielkiki.vaultdonation.data.helper

import androidx.recyclerview.widget.DiffUtil
import com.desrielkiki.vaultdonation.data.entity.MemberData

class MemberDiffCallback(
    private val oldList: List<MemberData>,
    private val newList: List<MemberData>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].memberName == newList[newItemPosition].memberName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}