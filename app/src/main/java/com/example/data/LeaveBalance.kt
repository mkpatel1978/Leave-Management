package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leave_balances")
data class LeaveBalance(
    @PrimaryKey val employeeEmail: String,
    val annualMax: Int = 15,
    val annualUsed: Int = 0,
    val sickMax: Int = 10,
    val sickUsed: Int = 0,
    val casualMax: Int = 7,
    val casualUsed: Int = 0,
    val unpaidUsed: Int = 0
)
