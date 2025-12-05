package com.example.trekking.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.viewpager2.widget.ViewPager2
import com.example.trekking.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val channelId = "weather_alerts_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLocation = findViewById<Button>(R.id.btnGoLocationSharing)
        val btnSafety = findViewById<Button>(R.id.btnGoSafetyTips)
        val btnData = findViewById<Button>(R.id.btnGoDataViewer)
        val btnAI = findViewById<Button>(R.id.btnGoAIChat)
        val btnML = findViewById<Button>(R.id.btnGoMLInsights)
        val btnMap = findViewById<Button>(R.id.btnGoMap)

        btnLocation?.setOnClickListener {
            startActivity(Intent(this, LocationSharingActivity::class.java))
        }

        btnSafety?.setOnClickListener {
            startActivity(Intent(this, SafetyTipsActivity::class.java))
        }

        btnData?.setOnClickListener {
            startActivity(Intent(this, DataViewerActivity::class.java))
        }

        btnAI?.setOnClickListener {
            startActivity(Intent(this, ChatBotActivity::class.java))
        }

        btnML?.setOnClickListener {
            startActivity(Intent(this, MLInsightsActivity::class.java))
        }

        btnMap?.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // Setup the new image slider
        setupImageSlider()

        // Show AI-based alerts/notifications right after entering main window (post-login)
        createNotificationChannel()
        showWeatherAlertsNotification()
        showWeatherAlertsDialog()
    }

    private fun setupImageSlider() {
        val viewPager: ViewPager2 = findViewById(R.id.viewPagerImages)
        val tabLayout: TabLayout = findViewById(R.id.tabLayoutDots)

        // Add your own images here from the drawable folder
        val imageList = listOf(
            R.drawable.trek_image_1,
            R.drawable.trek_image_2,
            R.drawable.trek_image_3
        )

        val adapter = ImageSliderAdapter(imageList)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // This is required to link the tab layout to the view pager
        }.attach()

        // Add the animation listener
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                animateTabs(tabLayout, position)
            }
        })

        // Set initial state for the first dot
        tabLayout.post { animateTabs(tabLayout, 0) }
    }

    private fun animateTabs(tabLayout: TabLayout, position: Int) {
        for (i in 0 until tabLayout.tabCount) {
            val tabView = tabLayout.getTabAt(i)?.view
            if (i == position) {
                // Animate the selected tab to be larger
                tabView?.animate()?.scaleX(1.5f)?.scaleY(1.5f)?.setDuration(200)?.start()
            } else {
                // Animate the unselected tabs to be smaller
                tabView?.animate()?.scaleX(1.0f)?.scaleY(1.0f)?.setDuration(200)?.start()
            }
        }
    }

    private fun weatherAlerts(): List<String> = listOf(
        "Too cold in Kedarkantha - Uttarakhand (−5°C, light snow)",
        "Windy in Sandakphu - West Bengal (45 km/h gusts)",
        "Heavy rain near Valley of Flowers - Uttarakhand (carry rain gear)",
        "Hot conditions at Rajmachi Fort - Maharashtra (36°C)",
        "Scattered showers at Triund - Himachal Pradesh",
        "Thunderstorm chance in Dzongri - Sikkim",
        "Humid at Dudhsagar Falls - Goa",
        "Cool breeze at Har Ki Dun - Uttarakhand",
        "Light drizzle at Tadiandamol - Karnataka",
        "Pleasant weather at Chembra Peak - Kerala"
    )

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Trekking Weather Alerts"
            val descriptionText = "Weather alerts for major trekking places"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showWeatherAlertsNotification() {
        val alerts = weatherAlerts()
        val bigText = alerts.joinToString(separator = "\n")

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(1001, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(1001, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle("Weather alerts")
            .setContentText(alerts.first())
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(1002, builder.build())
        }
    }

    private fun showWeatherAlertsDialog() {
        val alerts = weatherAlerts()
        val message = alerts.joinToString(separator = "\n\n")
        MaterialAlertDialogBuilder(this)
            .setTitle("AI Weather Alerts")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}