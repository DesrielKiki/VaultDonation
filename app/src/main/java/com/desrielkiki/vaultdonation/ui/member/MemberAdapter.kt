package com.desrielkiki.vaultdonation.ui.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.desrielkiki.vaultdonation.R
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.data.helper.MemberDiffCallback
import com.desrielkiki.vaultdonation.databinding.RowMemberBinding
import com.desrielkiki.vaultdonation.ui.util.MemberItemClickListener

class MemberAdapter(
    private val listener: MemberItemClickListener,
    private val viewModel: MemberViewModel,
    private val navController: NavController,
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder1>() {

    private val selectedIds = mutableSetOf<Long>()
    private var _memberList = emptyList<MemberData>()
    var memberList: List<MemberData>
        get() = _memberList
        set(value) {
            _memberList = value
            updateMemberNumbers(1)
            notifyDataSetChanged()
        }

    class MemberViewHolder1(
        private val binding: RowMemberBinding,
        private val viewModel: MemberViewModel,
        private val listener: MemberItemClickListener,
        private val navController: NavController,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(memberData: MemberData, isSelected: Boolean) {
            binding.memberData = memberData
            binding.executePendingBindings()
            val memberNumber = "${memberData.number}. "
            binding.tvNumber.text = memberNumber
            binding.tvMemberName.text = memberData.memberName

            if (viewModel.isSelectionModeActive.value == true) {
                binding.root.setOnClickListener {
                    listener.onItemLongClick(memberData)
                }
            } else {
                binding.root.setOnLongClickListener {
                    listener.onItemLongClick(memberData)
                    viewModel.setSelectionModeActive(true)
                    true
                }
                binding.root.setOnClickListener {
                    val action =
                        MemberFragmentDirections.actionMemberFragmentToMemberDetailFragment(
                            memberData
                        )
                    navController.navigate(action)

                }
            }
            binding.root.setBackgroundResource(
                if (isSelected) R.drawable.background_border_field_grey_check
                else R.drawable.background_border_field_grey
            )
        }

        companion object {
            fun from(
                parent: ViewGroup,
                viewModel: MemberViewModel,
                listener: MemberItemClickListener,
                navController: NavController,
            ): MemberAdapter.MemberViewHolder1 {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowMemberBinding.inflate(layoutInflater, parent, false)
                return MemberAdapter.MemberViewHolder1(binding, viewModel, listener, navController)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder1 {
        LayoutInflater.from(parent.context).inflate(R.layout.row_member, parent, false)
        return MemberAdapter.MemberViewHolder1.from(parent, viewModel, listener, navController)
    }

    override fun getItemCount(): Int = memberList.size

    override fun onBindViewHolder(holder: MemberViewHolder1, position: Int) {
        val currentMember = memberList[position]
        val isSelected = selectedIds.contains(currentMember.id)
        holder.bind(currentMember, isSelected)
    }

    private fun updateMemberNumbers(startNumber: Int) {
        for ((index, member) in _memberList.withIndex()) {
            member.number = startNumber + index
        }
    }

    fun setData(memberData: List<MemberData>, startNumber: Int) {
        val diffCallback = MemberDiffCallback(memberList, memberData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.memberList = memberData
        updateMemberNumbers(startNumber)

        diffResult.dispatchUpdatesTo(this)
    }

    fun setSelectedIds(ids: Set<Long>) {
        selectedIds.clear() // Hapus id-item yang dipilih sebelumnya
        selectedIds.addAll(ids) // Tambahkan id-item yang baru dipilih
        notifyDataSetChanged() // Lakukan update pada tampilan yang terpengaruh
    }
}