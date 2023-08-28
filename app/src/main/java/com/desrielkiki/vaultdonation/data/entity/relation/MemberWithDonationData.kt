package com.desrielkiki.vaultdonation.data.entity.relation

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.desrielkiki.vaultdonation.data.entity.DonationData
import com.desrielkiki.vaultdonation.data.entity.MemberData
import kotlinx.parcelize.Parcelize


@Parcelize
class MemberWithDonationData(
    @Embedded
    var donationData: DonationData,
    @Relation(
        parentColumn ="memberId",
        entityColumn = "id"
    )
    val memberData: MemberData?
) : Parcelable