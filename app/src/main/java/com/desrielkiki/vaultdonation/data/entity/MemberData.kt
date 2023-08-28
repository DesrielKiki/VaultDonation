package com.desrielkiki.vaultdonation.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "table_member")
@Parcelize
data class MemberData(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var memberName: String,
    var number:Int = 0
    ):Parcelable