package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.LeaveRepository
import com.example.ui.LeaveApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LeaveViewModel
import com.example.viewmodel.LeaveViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: LeaveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize local database database context
        val database = AppDatabase.getDatabase(this)
        val repository = LeaveRepository(database.leaveDao(), this)
        val factory = LeaveViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LeaveViewModel::class.java]

        // Parse deep link url requests if application is launched from an email link
        handleIntent(intent)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LeaveApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    /**
     * Resolve custom Scheme Deep-linking for automated email workflow links.
     * Maps "leavemanager://approve?id=X" and "leavemanager://reject?id=X"
     */
    private fun handleIntent(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null && uri.scheme == "leavemanager") {
            val host = uri.host
            val idStr = uri.getQueryParameter("id")
            val id = idStr?.toLongOrNull()
            
            if (id != null) {
                if (host.equals("approve", ignoreCase = true)) {
                    viewModel.approveRequest(id)
                } else if (host.equals("reject", ignoreCase = true)) {
                    viewModel.rejectRequest(id)
                }
            }
        }
    }
}
