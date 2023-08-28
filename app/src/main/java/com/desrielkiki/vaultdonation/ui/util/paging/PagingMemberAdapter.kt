package com.desrielkiki.vaultdonation.ui.util.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.databinding.RowMemberBinding

class PagingMemberAdapter :
    PagingDataAdapter<MemberData, PagingMemberAdapter.PagingViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MemberData>() {
            override fun areItemsTheSame(oldItem: MemberData, newItem: MemberData): Boolean {
                return oldItem.memberName == newItem.memberName
            }

            override fun areContentsTheSame(oldItem: MemberData, newItem: MemberData): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class PagingViewHolder(val binding: RowMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        val member = getItem(position)
        holder.binding.apply {
            tvMemberName.text = member?.memberName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        return PagingViewHolder(
            RowMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}