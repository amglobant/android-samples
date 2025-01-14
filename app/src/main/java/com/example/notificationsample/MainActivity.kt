package com.example.notificationsample

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notificationsample.ui.theme.NotificationSampleTheme

class MainActivity : ComponentActivity() {
    private var badgeNumber: Int = 0
    val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        enableEdgeToEdge()
        setContent {
            var currentNumber by remember { mutableStateOf(0) }
            NotificationSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        badgeNumber = "Badge Number = $currentNumber",
                        modifier = Modifier.padding(innerPadding),
                        onClickIncrease = {
                            currentNumber++
                            badgeNumber = currentNumber
                        },
                        onClickDecrease = {
                            currentNumber--
                            badgeNumber = currentNumber
                        },
                        onClickNotification = { showNotification() },
                    )
                }
            }
        }
    }

    private fun showNotification() {
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("Not authorized")
                return@with
            }
            notify(NOTIFICATION_ID, getNotificationBuilder().build())
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Message")
            .setContentText("You've received $badgeNumber new messages")
//            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setNumber(badgeNumber)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        val CHANNEL_ID = "BadgeTest"
        val CHANNEL_NAME = "AnyDemoChannel"
        val CHANNEL_DESCRIPTION = "Channel for testing badges"
        val NOTIFICATION_ID = 1234
    }
}

@Composable
fun Greeting(
    badgeNumber: String,
    modifier: Modifier = Modifier,
    onClickIncrease: () -> Unit,
    onClickDecrease: () -> Unit,
    onClickNotification: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = badgeNumber,
            modifier = modifier
        )
        Button(onClick = onClickIncrease) {
            Text("Increase Badge number")
        }
        Button(onClick = onClickDecrease) {
            Text("Decrease Badge number")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onClickNotification) {
            Text("Show notification")
        }
    }

}