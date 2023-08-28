package com.desrielkiki.vaultdonation.ui

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.desrielkiki.vaultdonation.R
import com.desrielkiki.vaultdonation.data.entity.DonationType
import com.desrielkiki.vaultdonation.data.entity.relation.MemberWithDonationData
import com.desrielkiki.vaultdonation.ui.util.formatDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long,
        ) {
            when (position) {
                0 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application,
                            R.color.mint
                        )
                    )
                }

                1 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application,
                            R.color.gold
                        )
                    )
                }

                2 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application,
                            R.color.dark
                        )
                    )
                }
            }
        }
    }

    fun parseDonationType(donationType: String): DonationType {
        return when (donationType) {
            "Resource" -> {
                DonationType.Resource
            }

            "GoldBar" -> {
                DonationType.GoldBar
            }

            "Resource and GoldBar" -> {
                DonationType.Both
            }

            else -> DonationType.Both
        }
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val outputDateFormat = SimpleDateFormat("dd, MM, yyyy", Locale.ENGLISH)
        return outputDateFormat.format(calendar.time)
    }

    /**
     * calendar function
     * */

    private var calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    private val _currentWeekAndMonth = MutableLiveData<String>()
    val currentWeekAndMonth: LiveData<String>
        get() = _currentWeekAndMonth
    private val _currentMonth = MutableLiveData<Int>()
    val currentMonth: LiveData<Int>
        get() = _currentMonth

    init {
        updateCurrentWeekAndMonth()
    }

    private val _refreshData = MutableLiveData<Unit>()

    private var selectedCalendar = Calendar.getInstance()

    fun filterDonationsByMonth(
        donations: List<MemberWithDonationData>,
        month: Int,
    ): List<MemberWithDonationData> {
        return donations.filter { donation ->
            val donationDate = parseDonationDate(donation.donationData.donationDate)
            donationDate?.get(Calendar.MONTH) == month
        }
    }


    private fun parseDonationDate(dateString: String): Calendar? {
        val dateFormat = SimpleDateFormat("dd, MM, yyyy", Locale.getDefault())
        return try {
            val date = dateFormat.parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar
        } catch (e: Exception) {
            null
        }
    }


    fun getStartOfWeekFormatted(): String {
        // Memastikan startOfWeek dan endOfWeek sudah disesuaikan
        adjustWeeksIfDifferentMonths()

        val startOfWeek =
            calendar.clone() as Calendar // Menggunakan startOfWeek dari sharedViewModel
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        return formatDate(startOfWeek.time, "dd, MM, yyyy")
    }

    fun getEndOfWeekFormatted(): String {
        val lastWeekOfMonth = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val currentWeek = calendar.get(Calendar.WEEK_OF_MONTH)
        val endOfWeek = calendar.clone() as Calendar

        if (currentWeek == lastWeekOfMonth) {
            endOfWeek.set(Calendar.DAY_OF_MONTH, lastDayOfMonth)
            Log.d("SharedViewModel", "Ini adalah minggu terakhir dalam bulan.")
            Log.d("SharedViewModel", "endofmonth = $lastDayOfMonth")
        } else {
            endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            Log.d("SharedViewModel", "Ini bukan minggu terakhir dalam bulan.")
        }
        val dateFormat = SimpleDateFormat("dd, MM, yyyy", Locale.getDefault())
        return dateFormat.format(endOfWeek.time)
    }

    fun adjustWeeksIfDifferentMonths() {
        val startOfWeek = calendar.clone() as Calendar
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val endOfWeek = calendar.clone() as Calendar
        endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)

        val startMonth = startOfWeek.get(Calendar.MONTH)
        val endMonth = endOfWeek.get(Calendar.MONTH)
        val currentWeek = calendar.get(Calendar.WEEK_OF_MONTH)

        if (startMonth != endMonth || currentWeek == calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)) {
            // Jika bulan dari startOfWeek berbeda dengan bulan dari endOfWeek
            // atau berada di minggu terakhir dalam bulan
            // Set startOfWeek ke tanggal pertama dari bulan endMonth
            startOfWeek.set(Calendar.DAY_OF_MONTH, 1)

            // Set endOfWeek ke akhir minggu pertama dari bulan endMonth
            endOfWeek.set(Calendar.MONTH, endMonth)
            endOfWeek.set(Calendar.WEEK_OF_MONTH, 1)
            endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        }

        Log.d("SharedViewModel", "Adjusted Start of week: ${startOfWeek.time}")
        Log.d("SharedViewModel", "Adjusted End of week: ${endOfWeek.time}")
    }


    private fun updateCurrentWeekAndMonth() {
        val month = calendar.get(Calendar.MONTH)
        _currentWeekAndMonth.value = getFormattedWeekAndMonth(calendar.time)
        _currentMonth.value = month

    }

    fun moveToPreviousWeek() {
        val currentWeek = calendar.get(Calendar.WEEK_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)

        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val newMonth = calendar.get(Calendar.MONTH)

        if (currentWeek == 1 && currentMonth != newMonth) {
            // Jika berada di minggu pertama dalam bulan dan berpindah ke bulan sebelumnya
            calendar.set(
                Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            ) // Atur tanggal ke akhir bulan sebelumnya
        }

        adjustWeeksIfDifferentMonths() // Memanggil adjustWeeksIfDifferentMonths() setelah perubahan minggu
        updateCurrentWeekAndMonth()
        _refreshData.value = Unit
    }

    fun moveToNextWeek() {
        val lastWeekOfMonth = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
        val currentWeek = calendar.get(Calendar.WEEK_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)

        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val newMonth = calendar.get(Calendar.MONTH)

        if (currentWeek == lastWeekOfMonth && currentMonth != newMonth) {
            // Jika berada di minggu terakhir dalam bulan dan berpindah ke bulan berikutnya
            calendar.set(Calendar.DAY_OF_MONTH, 1) // Atur tanggal ke awal bulan berikutnya
        }

        updateCurrentWeekAndMonth()
        _refreshData.value = Unit
    }


    private fun getFormattedWeekAndMonth(date: Date): String {
        val weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        val monthYear = dateFormat.format(date)
        return "$monthYear, Week $weekOfMonth"
    }
}


