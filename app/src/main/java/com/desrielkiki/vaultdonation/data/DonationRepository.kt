package com.desrielkiki.vaultdonation.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.desrielkiki.vaultdonation.data.entity.DonationData
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DonationRepository(application: Application) {
    private val dao: DonationDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db=DonationDatabase.getDatabase(application)
        dao =db.donationDao()
    }

    /**
     * get Data
     **/
    fun getMemberData(nextPage: Int): LiveData<List<MemberData>> = dao.getMemberData()
    fun getAllMember(): LiveData<List<MemberData>> = dao.getMemberData()
    fun getMemberWithDonationData(): LiveData<List<MemberWithDonationData>> = dao.getMemberWithDonationData()
    fun getDonationByMember(memberId: Long): LiveData<List<MemberWithDonationData>> {
        return dao.getDonationByMemberId(memberId)
    }
    fun getDonationsForWeek(startOfWeek: String, endOfWeek: String): LiveData<List<MemberWithDonationData>> {
        return dao.getDonationsForWeek(startOfWeek, endOfWeek)
    }
    fun getDonationsForWeekByMemberId(startOfWeek: String, endOfWeek: String, memberId: Long): LiveData<List<MemberWithDonationData>> {
        return dao.getDonationsForWeekByMemberId(startOfWeek, endOfWeek, memberId)
    }
    fun getMembersWithoutDonationForWeek(startOfWeek: String, endOfWeek: String): LiveData<List<MemberData>> {
        return dao.getMembersWithoutDonationForWeek(startOfWeek, endOfWeek)
    }

    /**
     * Insert Data
     **/
    fun insertMember(memberData: MemberData){
        executorService.execute{dao.insertMember(memberData)}
    }
    fun insertDonation(donationData: DonationData){
        executorService.execute{dao.insertDonation(donationData)}
    }

    /**
     * Update Data
     */

    fun updateMember(memberData: MemberData){
        executorService.execute{dao.updateMember(memberData)}
    }

    /**
     * Delete Data
     **/
    fun deleteMember(memberData: MemberData){
        executorService.execute{dao.deleteMember(memberData)}
    }
    fun deleteAllMember(){
        dao.deleteAllMembers()
    }
    fun deleteMemberByID(selectedId: Set<Long>){
        Log.d("MemberRepository", "Deleting member with ID: $selectedId")

        executorService.execute { dao.deleteMemberById(selectedId) }
    }

    /**
     * filter data
     * */
    fun searchDatabase(searchQuery: String): LiveData<List<MemberData>>{
        return dao.searchDatabase(searchQuery)
    }
     fun getPagedList(limit: Int, offset: Int): LiveData<List<MemberData>> {
         Log.d("DonationRepository", "getPagedList called with limit=$limit, offset=$offset")
         val data = dao.getPagedList(limit, offset)
         Log.d("DonationRepository", "getPagedList returned ${data.value?.size} items")
         return data
    }
    fun loadDataByPage(offset: Int, pageSize: Int): LiveData<List<MemberData>>{
        return dao.loadDataByPage(offset, pageSize)
    }
    fun loadMemberWithoutDonationByPage(startOfWeek: String, endOfWeek: String, offset: Int, pageSize: Int): LiveData<List<MemberData>>{
        return dao.loadMemberWithoutDonationByPage(startOfWeek, endOfWeek, offset, pageSize)
    }
    fun getDonationForWeekByPage(startOfWeek: String, endOfWeek: String, offset: Int, pageSize: Int): LiveData<List<MemberWithDonationData>>{
        return dao.getDonationsForWeekByPage(startOfWeek, endOfWeek, offset, pageSize)
    }
}