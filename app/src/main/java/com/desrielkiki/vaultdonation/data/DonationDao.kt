package com.desrielkiki.vaultdonation.data

import android.content.ClipData
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.desrielkiki.vaultdonation.data.entity.DonationData
import com.desrielkiki.vaultdonation.data.entity.MemberData
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData


@Dao
interface DonationDao {

    /**
     * insert function
     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMember(memberData: MemberData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDonation(donationData: DonationData)

    /**
     * Update Data
     * */
    @Update
    fun updateMember(memberData: MemberData)

    /**
     * delete function
     */
    @Delete
    fun deleteMember(memberData: MemberData)

    @Query("DELETE FROM table_member WHERE id IN (:selectedId)")
    fun deleteMemberById(selectedId: Set<Long>)
    @Query("DELETE FROM table_member")
    fun deleteAllMembers()

    /**
     * get function
     */
    @Query("SELECT * FROM table_member ORDER BY memberName ASC ")
    fun getMemberData(): LiveData<List<MemberData>>

    @Query("SELECT * FROM table_donation WHERE memberId = :memberId")
    fun getDonationByMemberId(memberId: Long): LiveData<List<MemberWithDonationData>>

    @Transaction
    @Query("SELECT * FROM table_donation")
    fun getMemberWithDonationData(): LiveData<List<MemberWithDonationData>>

    @Query("SELECT * FROM table_donation WHERE donationDate >= :startOfWeek AND donationDate <= :endOfWeek")
    fun getDonationsForWeek(
        startOfWeek: String,
        endOfWeek: String,
    ): LiveData<List<MemberWithDonationData>>

    @Query("SELECT * FROM table_donation WHERE donationDate >= :startOfWeek AND donationDate <= :endOfWeek AND memberId = :memberId")
    fun getDonationsForWeekByMemberId(
        startOfWeek: String,
        endOfWeek: String,
        memberId: Long,
    ): LiveData<List<MemberWithDonationData>>

    @Query("SELECT * FROM table_member WHERE id NOT IN (SELECT memberId FROM table_donation WHERE donationDate BETWEEN :startOfWeek AND :endOfWeek)")
    fun getMembersWithoutDonationForWeek(
        startOfWeek: String,
        endOfWeek: String,
    ): LiveData<List<MemberData>>

    /**
     * filter function
     **/
    @Query("SELECT * FROM table_member WHERE memberName LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<MemberData>>

    @Query("SELECT * FROM table_member ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getPagedList(limit: Int, offset: Int): LiveData<List<MemberData>>

    @Query("SELECT * FROM table_member ORDER BY memberName ASC LIMIT :pageSize OFFSET :offset")
    fun loadDataByPage(offset: Int, pageSize: Int): LiveData<List<MemberData>>

    @Query("SELECT * FROM table_member WHERE id NOT IN (SELECT memberId FROM table_donation WHERE donationDate BETWEEN :startOfWeek AND :endOfWeek) LIMIT :pageSize OFFSET :offset")
    fun loadMemberWithoutDonationByPage(
        startOfWeek: String,
        endOfWeek: String,
        offset: Int,
        pageSize: Int,
    ): LiveData<List<MemberData>>

    @Query("SELECT * FROM table_donation WHERE donationDate >= :startOfWeek AND donationDate <= :endOfWeek LIMIT :pageSize OFFSET :offset")
    fun getDonationsForWeekByPage(
        startOfWeek: String,
        endOfWeek: String,
        offset: Int,
        pageSize: Int,
    ): LiveData<List<MemberWithDonationData>>

}