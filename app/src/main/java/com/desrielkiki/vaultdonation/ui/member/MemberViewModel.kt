package com.desrielkiki.vaultdonation.ui.member

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.desrielkiki.vaultdonation.data.DonationRepository
import com.desrielkiki.vaultdonation.data.entity.DonationData
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData
import com.desrielkiki.vaultdonation.ui.util.paging.MemberPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemberViewModel (application: Application): AndroidViewModel(application) {


    private val repository: DonationRepository = DonationRepository(application)

    val getMemberData: LiveData<List<MemberData>> = repository.getMemberData(nextPage = 0)
    val getAllMember: LiveData<List<MemberData>> = repository.getAllMember()
    val getMemberWithDonationData: LiveData<List<MemberWithDonationData>> = repository.getMemberWithDonationData()

    fun getDonationByMemberId(memberId: Long): LiveData<List<MemberWithDonationData>>{
        return repository.getDonationByMember(memberId)
    }

    fun getMembersWithoutDonationForWeek(startOfWeek: String, endOfWeek: String): LiveData<List<MemberData>> {
        return repository.getMembersWithoutDonationForWeek(startOfWeek, endOfWeek)
    }
    fun insertMember(memberData: MemberData){
        repository.insertMember(memberData)
    }
    fun updateMember(memberData: MemberData){
        repository.updateMember(memberData)
    }
    fun deleteMember(memberData: MemberData){
        repository.deleteMember(memberData)
    }
    fun deleteMemberById(selectedId:Set<Long>){
        repository.deleteMemberByID(selectedId)
    }
    fun deleteAllMember(){
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteAllMember()
        }
    }


    fun insertDonate(donationData: DonationData){
        repository.insertDonation(donationData)
    }
    fun searchDatabase(searchQuery: String): LiveData<List<MemberData>> {
        return repository.searchDatabase(searchQuery)
    }
    val data: LiveData<PagingData<MemberData>> = Pager(
        config = PagingConfig(pageSize = 50),
        pagingSourceFactory = { MemberPagingSource(repository) }
    ).liveData

    private val _selectedIds = MutableLiveData<Set<Long>>(emptySet())
    val selectedIds: LiveData<Set<Long>> = _selectedIds

    fun getSelectedItemsLiveData(): LiveData<Set<Long>> {
        return _selectedIds
    }
    private val _memberList = MutableLiveData<List<MemberData>>(emptyList())
    val memberList: LiveData<List<MemberData>>
        get() = _memberList

    private val _isSelectionModeActive = MutableLiveData<Boolean>(false)
    val isSelectionModeActive: LiveData<Boolean>
        get() = _isSelectionModeActive

    fun setSelectionModeActive(active: Boolean) {
        _isSelectionModeActive.value = active
    }
    private val _selectedPositions = MutableLiveData<Set<Int>>()
    val selectedPositions: LiveData<Set<Int>> = _selectedPositions

    fun toggleSelection(memberData: MemberData) {
        val currentSelectedIds = _selectedIds.value ?: emptySet()
        val newSelectedIds = currentSelectedIds.toMutableSet()

        if (newSelectedIds.contains(memberData.id)) {
            newSelectedIds.remove(memberData.id)
        } else {
            newSelectedIds.add(memberData.id)
        }
        _selectedIds.value = newSelectedIds
    }


    fun isItemSelected(memberData: MemberData): Boolean {
        val selectedIds = _selectedIds.value ?: emptySet()
        return selectedIds.contains(memberData.id)
    }
    fun getSelectedMembers(): List<MemberData> {
        val selectedIds = _selectedIds.value ?: emptySet()
        return _memberList.value?.filter { selectedIds.contains(it.id) } ?: emptyList()
    }

    // Fungsi untuk menghapus seleksi
    fun clearSelection() {
        _selectedIds.value = emptySet()
        setSelectionModeActive(false)
    }

    // Fungsi untuk mengatur id item yang terpilih
    fun setSelectedIds(ids: Set<Long>) {
        _selectedIds.value = ids.toMutableSet()
    }
    fun loadPageData(page: Int, pageSize: Int): LiveData<List<MemberData>> {
        return repository.loadDataByPage(page, pageSize)
    }
}