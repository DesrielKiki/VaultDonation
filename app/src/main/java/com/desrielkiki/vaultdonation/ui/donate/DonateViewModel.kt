package com.desrielkiki.vaultdonation.ui.donate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.desrielkiki.vaultdonation.data.DonationRepository
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData

class DonateViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DonationRepository = DonationRepository(application)

    fun getDonationsForWeekByMemberId(
        startOfWeek: String,
        endOfWeek: String,
        memberId: Long
    ): LiveData<List<MemberWithDonationData>> {
        return repository.getDonationsForWeekByMemberId(startOfWeek, endOfWeek, memberId)
    }
}