package com.desrielkiki.vaultdonation.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.desrielkiki.vaultdonation.data.DonationRepository
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DonationRepository = DonationRepository(application)

    fun getDonationForWeek(
        startOfWeek: String,
        endOfWeek: String,
    ): LiveData<List<MemberWithDonationData>> {
        return repository.getDonationsForWeek(startOfWeek, endOfWeek)
    }
}