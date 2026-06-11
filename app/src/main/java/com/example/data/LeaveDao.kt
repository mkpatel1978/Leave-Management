package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveDao {
    @Query("SELECT * FROM leave_requests ORDER BY startDate DESC")
    fun getAllRequests(): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE id = :id")
    suspend fun getRequestById(id: Long): LeaveRequest?

    @Query("SELECT * FROM leave_balances WHERE employeeEmail = :email")
    fun getBalanceForEmployee(email: String): Flow<LeaveBalance?>

    @Query("SELECT * FROM leave_balances WHERE employeeEmail = :email")
    suspend fun getBalanceForEmployeeSync(email: String): LeaveBalance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: LeaveRequest): Long

    @Update
    suspend fun updateRequest(request: LeaveRequest)

    @Delete
    suspend fun deleteRequest(request: LeaveRequest)

    @Query("DELETE FROM leave_requests WHERE id = :id")
    suspend fun deleteRequestById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: LeaveBalance)

    @Update
    suspend fun updateBalance(balance: LeaveBalance)
}
