package com.desrielkiki.vaultdonation.ui

import android.widget.RelativeLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.ui.home.HomeFragmentDirections
import com.desrielkiki.vaultdonation.ui.member.MemberFragmentDirections

class BindingAdapter {

    companion object {
        @BindingAdapter("android:navigateToDetailMemberFromMember")
        @JvmStatic
        fun navigateToDetailMemberFromMember(view: RelativeLayout, memberData: MemberData) {
            view.setOnClickListener {
                val action =
                    MemberFragmentDirections.actionMemberFragmentToMemberDetailFragment(memberData)
                view.findNavController().navigate(action)
            }

        }

        @BindingAdapter("android:navigateToDetailMemberFromHome")
        @JvmStatic
        fun navigateToDetailMemberFromHome(view: RelativeLayout, memberData: MemberData) {
            view.setOnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToMemberDetailFragment(memberData)
                view.findNavController().navigate(action)
            }
        }
    }
}