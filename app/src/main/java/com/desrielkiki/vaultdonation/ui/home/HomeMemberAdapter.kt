package com.desrielkiki.vaultdonation.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.desrielkiki.vaultdonation.R
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.data.helper.MemberDiffCallback
import com.desrielkiki.vaultdonation.databinding.RowMemberBinding
import com.desrielkiki.vaultdonation.databinding.RowMemberHomeBinding

class HomeMemberAdapter : RecyclerView.Adapter<HomeMemberAdapter.HomeMemberViewHolder>() {

    private var _memberList = emptyList<MemberData>()

    var memberList: List<MemberData>
        get() = _memberList
        set(value) {
            _memberList = value
            updateMemberNumbers(1)
            notifyDataSetChanged()
        }

    class HomeMemberViewHolder(private val binding: RowMemberHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memberData: MemberData) {
            binding.memberData = memberData
            binding.executePendingBindings()
            val memberNumber = "${memberData.number}. "
            binding.tvNumber.text = memberNumber
            binding.tvMemberName.text = memberData.memberName
        }

        companion object {
            fun from(parent: ViewGroup): HomeMemberAdapter.HomeMemberViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowMemberHomeBinding.inflate(layoutInflater, parent, false)
                return HomeMemberViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeMemberViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.row_member_home, parent, false)
        return HomeMemberAdapter.HomeMemberViewHolder.from(parent)
    }

    override fun getItemCount(): Int = memberList.size

    override fun onBindViewHolder(holder: HomeMemberViewHolder, position: Int) {
        val currentMember = memberList[position]
        holder.bind(currentMember)
        Log.d("home adapter", "$currentMember bind to position $position")
    }

    fun setData(memberData: List<MemberData>, startNumber: Int) {
        val diffCallback = MemberDiffCallback(memberList, memberData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.memberList = memberData
        updateMemberNumbers(startNumber)

        diffResult.dispatchUpdatesTo(this)
    }
    var weekAndMonth: String = ""
        set(value) {
            field = value
            updateMemberNumbers(1)
        }

    fun updateMemberNumbers(startNumber: Int) {
        for ((index, member) in _memberList.withIndex()) {
            member.number = startNumber + index
        }
    }
}