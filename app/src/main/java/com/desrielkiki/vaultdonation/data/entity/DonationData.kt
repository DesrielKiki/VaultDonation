package com.desrielkiki.vaultdonation.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "table_donation", foreignKeys = [
    ForeignKey(
        entity = MemberData::class,
        parentColumns = ["id"],
        childColumns = ["memberId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
])
@Parcelize
data class DonationData(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var memberId: Long,
    var donationDate: String,
    var donationType: DonationType
):Parcelable
