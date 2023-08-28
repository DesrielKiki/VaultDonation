package com.desrielkiki.vaultdonation.ui.util.paging

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.desrielkiki.vaultdonation.data.DonationRepository

class PagingViewModel (application: Application): AndroidViewModel(application) {
    private val repository: DonationRepository = DonationRepository(application)

    val data = Pager(
        PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            initialLoadSize = 20
        )
    ) {
        MemberPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
    init {
        Log.d("PagingViewModel", "PagingViewModel initialized")
    }
}