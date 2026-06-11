package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "leave_requests")
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val employeeName: String,
    val employeeEmail: String,
    val managerEmail: String,
    val leaveType: String, // e.g. "Annual", "Sick", "Casual", "Maternity", "Unpaid"
    val startDate: Long, // timestamp
    val endDate: Long, // timestamp
    val reason: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val calendarEventId: Long? = null, // Store Google Calendar event ID
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
