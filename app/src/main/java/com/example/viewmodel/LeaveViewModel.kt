package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.LeaveBalance
import com.example.data.LeaveRequest
import com.example.data.LeaveRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class LeaveViewModel(private val repository: LeaveRepository) : ViewModel() {

    // Active User Email for context-driven tracking
    private val _currentUserEmail = MutableStateFlow("mayank.master@gmail.com")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentUserName = MutableStateFlow("Mayank")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    // Combined list of all requests inside database
    val allRequests: StateFlow<List<LeaveRequest>> = repository.allRequests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // User-specific requests
    val userRequests: StateFlow<List<LeaveRequest>> = combine(allRequests, currentUserEmail) { list, email ->
        list.filter { it.employeeEmail.equals(email, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observe active user's remaining live balances
    val currentBalance: StateFlow<LeaveBalance?> = _currentUserEmail
        .flatMapLatest { email -> repository.getBalance(email) }
        .onEach { balance ->
            if (balance == null) {
                repository.checkAndInitializeBalance(_currentUserEmail.value)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Operation feedbacks / dialog states
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearMessages() {
        _uiMessage.value = null
        _errorMessage.value = null
    }

    fun updateUser(name: String, email: String) {
        _currentUserName.value = name
        _currentUserEmail.value = email
        viewModelScope.launch {
            repository.checkAndInitializeBalance(email)
        }
    }

    /**
     * Submit a new leave request.
     */
    fun submitRequest(
        leaveType: String,
        startDate: Long,
        endDate: Long,
        reason: String,
        managerEmail: String
    ) {
        viewModelScope.launch {
            if (reason.isBlank()) {
                _errorMessage.value = "Please provide a valid reason."
                return@launch
            }
            if (managerEmail.isBlank() || !managerEmail.contains("@")) {
                _errorMessage.value = "Please enter a valid manager email."
                return@launch
            }
            if (startDate > endDate) {
                _errorMessage.value = "Start Date cannot be after End Date."
                return@launch
            }

            val requestDays = repository.calculateLeaveDays(startDate, endDate)
            val currentBal = currentBalance.value ?: LeaveBalance(_currentUserEmail.value)
            
            // Proactive balance check based on category
            val available = when (leaveType.lowercase(Locale.ROOT)) {
                "annual" -> currentBal.annualMax - currentBal.annualUsed
                "sick" -> currentBal.sickMax - currentBal.sickUsed
                "casual" -> currentBal.casualMax - currentBal.casualUsed
                else -> 999 // Unpaid has of course infinite limit
            }

            if (available < requestDays) {
                _errorMessage.value = "Requested $requestDays days of $leaveType leave exceeds your remaining balance of $available days."
                return@launch
            }

            val newRequest = LeaveRequest(
                employeeName = _currentUserName.value,
                employeeEmail = _currentUserEmail.value,
                managerEmail = managerEmail,
                leaveType = leaveType,
                startDate = startDate,
                endDate = endDate,
                reason = reason,
                status = "PENDING"
            )

            val newId = repository.createLeaveRequest(newRequest)
            _uiMessage.value = "Request submitted successfully! Formulated email approval draft for ID #$newId."
        }
    }

    /**
     * Delete request, obeying the 2-day threshold before commencement.
     */
    fun deleteRequest(request: LeaveRequest) {
        viewModelScope.launch {
            repository.tryDeleteRequest(request)
                .onSuccess {
                    _uiMessage.value = "Leave request successfully deleted and cancelled."
                }
                .onFailure {
                    _errorMessage.value = it.message ?: "Could not cancel leave."
                }
        }
    }

    /**
     * Complete approval workflow (typically triggered from deep link click in mail).
     */
    fun approveRequest(id: Long) {
        viewModelScope.launch {
            repository.approveRequest(id)
                .onSuccess { req ->
                    _uiMessage.value = "Leave request for ${req.employeeName} (${req.leaveType}) APPROVED and written to Google Calendar!"
                }
                .onFailure {
                    _errorMessage.value = it.message ?: "Failed to approve request."
                }
        }
    }

    /**
     * Decline leave request workflow.
     */
    fun rejectRequest(id: Long) {
        viewModelScope.launch {
            repository.rejectRequest(id)
                .onSuccess { req ->
                    _uiMessage.value = "Leave request for ${req.employeeName} has been rejected."
                }
                .onFailure {
                    _errorMessage.value = it.message ?: "Failed to reject request."
                }
        }
    }

    /**
     * Generate the direct action email text formatted professionally.
     */
    fun getEmailBody(request: LeaveRequest): String {
        return repository.buildEmailBody(request)
    }

    /**
     * Setup default balance data or reset it for demo purposes.
     */
    fun resetBalances() {
        viewModelScope.launch {
            val email = _currentUserEmail.value
            repository.checkAndInitializeBalance(email)
            _uiMessage.value = "Leave balances and system caches normalized."
        }
    }
}

class LeaveViewModelFactory(private val repository: LeaveRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeaveViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
