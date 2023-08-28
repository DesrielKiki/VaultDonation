package com.desrielkiki.vaultdonation.ui.member

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.desrielkiki.vaultdonation.data.DonationRepository
import com.desrielkiki.vaultdonation.data.entity.DonationData
import com.desrielkiki.vaultdonation.data.entity.MemberData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemberViewModel (application: Application): AndroidViewModel(application) {


    private val repository: DonationRepository = DonationRepository(application)

    val getAllMember: LiveData<List<MemberData>> = repository.getAllMember()
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


    private val _selectedIds = MutableLiveData<Set<Long>>(emptySet())
    val selectedIds: LiveData<Set<Long>> = _selectedIds

    fun getSelectedItemsLiveData(): LiveData<Set<Long>> {
        return _selectedIds
    }
    private val _isSelectionModeActive = MutableLiveData<Boolean>(false)
    val isSelectionModeActive: LiveData<Boolean>
        get() = _isSelectionModeActive

    fun setSelectionModeActive(active: Boolean) {
        _isSelectionModeActive.value = active
    }

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

    // Fungsi untuk menghapus seleksi
    fun clearSelection() {
        _selectedIds.value = emptySet()
        setSelectionModeActive(false)
    }

    // Fungsi untuk mengatur id item yang terpilih

    fun loadPageData(page: Int, pageSize: Int): LiveData<List<MemberData>> {
        return repository.loadDataByPage(page, pageSize)
    }
}