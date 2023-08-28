package com.desrielkiki.vaultdonation.ui.util.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.desrielkiki.vaultdonation.data.DonationDao
import com.desrielkiki.vaultdonation.data.DonationRepository
import com.desrielkiki.vaultdonation.data.entity.MemberData
import kotlinx.coroutines.delay
import java.lang.Exception

class MemberPagingSource(private val repository: DonationRepository) : PagingSource<Int, MemberData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemberData> {
        return try {
            val nextPage = params.key ?: 1
            val response = repository.getMemberData(nextPage)
            val entities = response.value ?: emptyList() // Get the list from LiveData

            LoadResult.Page(
                data = entities,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MemberData>): Int? {
        return state.anchorPosition
    }

}