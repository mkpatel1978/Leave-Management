package com.example.ui

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.LeaveBalance
import com.example.data.LeaveRequest
import com.example.viewmodel.LeaveViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveApp(
    viewModel: LeaveViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab = remember { mutableStateOf("dashboard") }

    val userRequests by viewModel.userRequests.collectAsStateWithLifecycle()
    val allRequests by viewModel.allRequests.collectAsStateWithLifecycle()
    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()
    val currentUserEmail by viewModel.currentUserEmail.collectAsStateWithLifecycle()
    val currentUserName by viewModel.currentUserName.collectAsStateWithLifecycle()

    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showUserConfigDialog by remember { mutableStateOf(false) }

    // Alert feedback displays
    if (uiMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = { Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CheckCircle, "Success", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(8.dp))
                Text("Success", fontWeight = FontWeight.Bold)
            }},
            text = { Text(uiMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearMessages() }) {
                    Text("OK")
                }
            }
        )
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = { Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Error, "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(8.dp))
                Text("Error / Limitation", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }},
            text = { Text(errorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearMessages() }) {
                    Text("OK", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Leave Flow",
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleLarge,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            "Status: Connected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    // Profile button allowing instant identity toggling
                    Surface(
                        onClick = { showUserConfigDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("profile_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = "Active account", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = currentUserName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 100.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentTab.value == "dashboard",
                    onClick = { currentTab.value = "dashboard" },
                    icon = { Icon(if (currentTab.value == "dashboard") Icons.Filled.Dashboard else Icons.Outlined.Dashboard, null) },
                    label = { Text("Dashboard") },
                    modifier = Modifier.testTag("nav_dashboard")
                )
                NavigationBarItem(
                    selected = currentTab.value == "raise",
                    onClick = { currentTab.value = "raise" },
                    icon = { Icon(if (currentTab.value == "raise") Icons.Filled.AddBox else Icons.Outlined.AddBox, null) },
                    label = { Text("Request") },
                    modifier = Modifier.testTag("nav_raise")
                )
                NavigationBarItem(
                    selected = currentTab.value == "email",
                    onClick = { currentTab.value = "email" },
                    icon = { Icon(if (currentTab.value == "email") Icons.Filled.Email else Icons.Outlined.Mail, null) },
                    label = { Text("Email Sandbox") },
                    modifier = Modifier.testTag("nav_email_sandbox")
                )
            }
        },
        floatingActionButton = {
            if (currentTab.value == "dashboard") {
                ExtendedFloatingActionButton(
                    text = { Text("Raise Leave") },
                    icon = { Icon(Icons.Filled.Add, "Raise Request") },
                    onClick = { currentTab.value = "raise" },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag("dashboard_raise_fab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab.value) {
                "dashboard" -> DashboardTab(
                    requests = userRequests,
                    balance = currentBalance,
                    onDelete = { viewModel.deleteRequest(it) },
                    onApprovePrompt = { currentTab.value = "email" }
                )
                "raise" -> RaiseRequestTab(
                    userName = currentUserName,
                    userEmail = currentUserEmail,
                    balance = currentBalance,
                    onSubmit = { type, start, end, reason, manager ->
                        viewModel.submitRequest(type, start, end, reason, manager)
                        currentTab.value = "dashboard"
                    }
                )
                "email" -> EmailInboxSandboxTab(
                    requests = allRequests,
                    onApprove = { viewModel.approveRequest(it) },
                    onReject = { viewModel.rejectRequest(it) },
                    getEmailText = { viewModel.getEmailBody(it) }
                )
            }
        }
    }

    if (showUserConfigDialog) {
        UserConfigDialog(
            currentName = currentUserName,
            currentEmail = currentUserEmail,
            onDismiss = { showUserConfigDialog = false },
            onSave = { name, email ->
                viewModel.updateUser(name, email)
                showUserConfigDialog = false
            },
            onReset = {
                viewModel.resetBalances()
                showUserConfigDialog = false
            }
        )
    }
}

@Composable
fun DashboardTab(
    requests: List<LeaveRequest>,
    balance: LeaveBalance?,
    onDelete: (LeaveRequest) -> Unit,
    onApprovePrompt: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Elegant Greeting & Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Time-Off Dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Request leave, preview manager notifications & approve requests dynamically with connected email simulations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BeachAccess,
                            contentDescription = "Leave flow active",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Your Remaining Balances",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BalanceItem(
                    title = "Annual",
                    max = balance?.annualMax ?: 15,
                    used = balance?.annualUsed ?: 0,
                    color = Color(0xFFD97706), // Safe warm gold
                    modifier = Modifier.weight(1f)
                )
                BalanceItem(
                    title = "Sick",
                    max = balance?.sickMax ?: 10,
                    used = balance?.sickUsed ?: 0,
                    color = Color(0xFFEF4444), // Crimson coral
                    modifier = Modifier.weight(1f)
                )
                BalanceItem(
                    title = "Casual",
                    max = balance?.casualMax ?: 7,
                    used = balance?.casualUsed ?: 0,
                    color = Color(0xFF0F766E), // Spruce deep sage
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (balance != null && balance.unpaidUsed > 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Unpaid Leaves Taken",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${balance.unpaidUsed} Days",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Request History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${requests.size} requests",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (requests.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "No leave requests raised yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tap 'Raise Leave' to request your time off.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                    )
                }
            }
        } else {
            items(requests, key = { it.id }) { request ->
                RequestCardItem(
                    request = request,
                    onDelete = { onDelete(request) },
                    onApprovePrompt = onApprovePrompt
                )
            }
        }
    }
}

@Composable
fun BalanceItem(
    title: String,
    max: Int,
    used: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val remaining = (max - used).coerceAtLeast(0)
    val progress = if (max > 0) remaining.toFloat() / max.toFloat() else 0f

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))

            // Beautiful interactive canvas-drawn gauge ring representing balances
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(72.dp)) {
                    val strokeWidth = 6.dp.toPx()
                    // Track circle background
                    drawArc(
                        color = color.copy(alpha = 0.12f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                    // Filled progress segment matching remaining leave
                    drawArc(
                        color = color,
                        startAngle = 135f,
                        sweepAngle = 270f * progress,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$remaining",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "left",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "$used of $max used",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RequestCardItem(
    request: LeaveRequest,
    onDelete: () -> Unit,
    onApprovePrompt: () -> Unit
) {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val startStr = formatter.format(Date(request.startDate))
    val endStr = formatter.format(Date(request.endDate))
    
    val diff = request.endDate - request.startDate
    val days = ((diff / (24 * 60 * 60 * 1000L)) + 1).coerceAtLeast(1)

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val canDelete = (request.startDate - today) >= (2 * 24 * 60 * 60 * 1000L)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("request_item_${request.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val badgeColor = when (request.leaveType.lowercase(Locale.ROOT)) {
                        "annual" -> Color(0xFF3B82F6)
                        "sick" -> Color(0xFFEF4444)
                        "casual" -> Color(0xFF10B981)
                        else -> MaterialTheme.colorScheme.outline
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(badgeColor, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${request.leaveType} Leave",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                val (statusText, containerColor, textColor) = when (request.status) {
                    "APPROVED" -> Triple("Approved", Color(0xFFD1FAE5), Color(0xFF065F46))
                    "REJECTED" -> Triple("Rejected", Color(0xFFFEE2E2), Color(0xFF991B1B))
                    else -> Triple("Pending", Color(0xFFFEF3C7), Color(0xFF92400E))
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = containerColor,
                    modifier = Modifier.testTag("status_badge_${request.id}")
                ) {
                    Text(
                        text = statusText,
                        color = textColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "$startStr - $endStr",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "$days ${if (days == 1L) "day" else "days"}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Reason: \"${request.reason}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Approver: ${request.managerEmail}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
            )

            if (request.status == "APPROVED" && request.calendarEventId != null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(6.dp)
                ) {
                    Icon(
                        Icons.Filled.EventAvailable,
                        contentDescription = "Synced",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Synced to Google Calendar (ID: ${request.calendarEventId})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1E40AF),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!canDelete) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, "Locked", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Deletion locked (<2 days notice)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LockOpen, "Available", tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Removable (2+ days notice)",
                            fontSize = 11.sp,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (request.status == "PENDING") {
                        FilledTonalButton(
                            onClick = onApprovePrompt,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("request_email_sim_${request.id}")
                        ) {
                            Text("Email View", fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canDelete) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                            contentColor = if (canDelete) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.outline
                        ),
                        enabled = canDelete,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("request_delete_${request.id}")
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Delete", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RaiseRequestTab(
    userName: String,
    userEmail: String,
    balance: LeaveBalance?,
    onSubmit: (type: String, start: Long, end: Long, reason: String, managerEmail: String) -> Unit
) {
    val context = LocalContext.current
    var leaveType by remember { mutableStateOf("Annual") }
    var reason by remember { mutableStateOf("") }
    var managerEmail by remember { mutableStateOf("manager@company.com") }

    val calendar = Calendar.getInstance()
    var startTimestamp by remember { mutableStateOf(calendar.timeInMillis) }
    calendar.add(Calendar.DAY_OF_YEAR, 2)
    var endTimestamp by remember { mutableStateOf(calendar.timeInMillis) }

    val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    val totalDays = ((endTimestamp - startTimestamp) / (24 * 60 * 60 * 1000L).toDouble()).toInt() + 1

    fun showDatePicker(currentDate: Long, onUpdated: (Long) -> Unit) {
        val cal = Calendar.getInstance().apply { timeInMillis = currentDate }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onUpdated(selected.timeInMillis)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
            Text(
                "Raise New Leave Request",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                "Submitting will generate automated email approval hyperlinks.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Leave Category", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Annual", "Sick", "Casual", "Unpaid").forEach { type ->
                        val selected = leaveType == type
                        FilterChip(
                            selected = selected,
                            onClick = { leaveType = type },
                            label = { Text(type) },
                            modifier = Modifier.testTag("chip_type_$type")
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Start Date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker(startTimestamp) { startTimestamp = it } }
                            .testTag("btn_start_date")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(formatter.format(Date(startTimestamp)), maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Filled.CalendarMonth, null, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("End Date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker(endTimestamp) { endTimestamp = it } }
                            .testTag("btn_end_date")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(formatter.format(Date(endTimestamp)), maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Filled.CalendarMonth, null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Calculated Leave Length", style = MaterialTheme.typography.bodySmall)
                        Text("$totalDays Working Days", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    val available = when (leaveType.lowercase(Locale.ROOT)) {
                        "annual" -> (balance?.annualMax ?: 15) - (balance?.annualUsed ?: 0)
                        "sick" -> (balance?.sickMax ?: 10) - (balance?.sickUsed ?: 0)
                        "casual" -> (balance?.casualMax ?: 7) - (balance?.casualUsed ?: 0)
                        else -> 999
                    }

                    if (available >= totalDays) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFD1FAE5),
                            contentColor = Color(0xFF065F46)
                        ) {
                            Text("Sufficient ($available left)", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEE2E2),
                            contentColor = Color(0xFF991B1B)
                        ) {
                            Text("Exceeded ($available left!)", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = managerEmail,
                onValueChange = { managerEmail = it },
                label = { Text("Manager Approver Email") },
                placeholder = { Text("manager@company.com") },
                leadingIcon = { Icon(Icons.Filled.SupervisorAccount, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_manager_email")
            )
        }

        item {
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason for Absence") },
                placeholder = { Text("Type key details...") },
                leadingIcon = { Icon(Icons.Filled.Description, null) },
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_reason")
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onSubmit(leaveType, startTimestamp, endTimestamp, reason, managerEmail) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_submit_request")
            ) {
                Icon(Icons.Filled.Send, null)
                Spacer(Modifier.width(8.dp))
                Text("Submit Request & Simulate Email", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmailInboxSandboxTab(
    requests: List<LeaveRequest>,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit,
    getEmailText: (LeaveRequest) -> String
) {
    var selectedRequest by remember { mutableStateOf<LeaveRequest?>(null) }

    if (selectedRequest != null && !requests.any { it.id == selectedRequest?.id }) {
        selectedRequest = null
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Inbox, "Inbox", tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Manager Inbox",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (requests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Inbox Empty.\nSubmit a leave request.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(requests) { req ->
                        val isSelected = selectedRequest?.id == req.id
                        val itemColor = when {
                            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            req.status == "PENDING" -> MaterialTheme.colorScheme.surface
                            else -> MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(itemColor)
                                .clickable { selectedRequest = req }
                                .padding(12.dp)
                                .testTag("email_item_${req.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = req.employeeName,
                                    fontWeight = if (req.status == "PENDING") FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = req.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = when (req.status) {
                                        "APPROVED" -> Color(0xFF059669)
                                        "REJECTED" -> Color(0xFFDC2626)
                                        else -> Color(0xFFD97706)
                                    }
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "[Leave Request] ${req.leaveType} Leave Application",
                                fontSize = 11.sp,
                                fontWeight = if (req.status == "PENDING") FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(6.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(2.0f)
                .fillMaxHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val req = selectedRequest
            if (req == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.MailOutline, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Select an email from the list to view approval links.",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("From: ${req.employeeEmail}", fontSize = 11.sp)
                            Text("To: ${req.managerEmail}", fontSize = 11.sp)
                            Text(
                                text = "Subject: [Leave Request] ${req.employeeName} - ${req.leaveType} Leave",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = getEmailText(req),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = Color(0xFF334155),
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text("Interactive Links Action (Simulate tap in email):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onApprove(req.id) },
                            enabled = req.status == "PENDING",
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("sandbox_approve_${req.id}")
                        ) {
                            Icon(Icons.Filled.Check, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Approve Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onReject(req.id) },
                            enabled = req.status == "PENDING",
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("sandbox_reject_${req.id}")
                        ) {
                            Icon(Icons.Filled.Close, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Reject Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserConfigDialog(
    currentName: String,
    currentEmail: String,
    onDismiss: () -> Unit,
    onSave: (name: String, email: String) -> Unit,
    onReset: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Switch User Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Change active email/name during testing to raise requests and check balances.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("config_set_name")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("config_set_email")
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = onReset,
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Text("Reset Balance", fontSize = 11.sp, maxLines = 1)
                    }

                    Button(
                        onClick = { onSave(name, email) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Profile", fontSize = 11.sp, maxLines = 1)
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
