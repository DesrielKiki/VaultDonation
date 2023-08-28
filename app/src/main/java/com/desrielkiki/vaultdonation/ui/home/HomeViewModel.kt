package com.desrielkiki.vaultdonation.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.desrielkiki.vaultdonation.data.DonationRepository
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DonationRepository = DonationRepository(application)

    fun getMembersWithoutDonationThisWeek(): LiveData<List<MemberData>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startOfWeek = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.timeInMillis

        return repository.getMembersWithoutDonationForWeek(
            startOfWeek.toString(),
            endOfWeek.toString()
        )
    }

    fun getDonationForWeek(
        startOfWeek: String,
        endOfWeek: String,
    ): LiveData<List<MemberWithDonationData>> {
        return repository.getDonationsForWeek(startOfWeek, endOfWeek)
    }

    fun getDonationForWeekByPage(
        startOfWeek: String,
        endOfWeek: String,
        offset: Int,
        pageSize: Int,
    ): LiveData<List<MemberWithDonationData>> {
        return repository.getDonationForWeekByPage(startOfWeek, endOfWeek, offset, pageSize)
    }
}