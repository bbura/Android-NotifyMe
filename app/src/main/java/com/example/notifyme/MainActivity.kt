package com.example.notifyme


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
private const val NOTIFICATION_ID = 0
private const val NOTIFICATION_ID2 = 2
private val ACTION_UPDATE_NOTIFICATION =
    "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION"

private val ACTION_RESET_NOTIFICATION =
    "com.example.android.notifyme.ACTION_RESET_NOTIFICATION"

class MainActivity : AppCompatActivity() {

    private lateinit var mNotifyManager: NotificationManager


    private fun createNotificationChannel() {
        mNotifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //create notification channel
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Bura's Channel", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from Bura"
            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotification(string:String): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle(string)
            .setContentText(string)
            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.FLAG_BUBBLE)
            .setAutoCancel(true)
            .setDeleteIntent(
                PendingIntent.getBroadcast(
                    this,
                    NOTIFICATION_ID,
                    Intent(ACTION_RESET_NOTIFICATION),
                    PendingIntent.FLAG_ONE_SHOT
                )
            )
            .setSmallIcon(R.drawable.ic_umeras)
    }


    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            updateIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        mNotifyManager.notify(
            NOTIFICATION_ID, getNotification("1").addAction(
                R.drawable.img_notificare_foreground,
                "Update Notification",
                updatePendingIntent
            ).build()
        )

        Thread.sleep(3000)
    }

    private fun sendNotification2() {

        mNotifyManager.notify(
            NOTIFICATION_ID2, getNotification("2").build()
        )
    }

    private fun updateNotification() {
        val androidImage = BitmapFactory
            .decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder: NotificationCompat.Builder = getNotification("3")
        notifyBuilder.setStyle(
            NotificationCompat.InboxStyle()
                .addLine("1")
                .addLine("2")
                .addLine("3")
                .setSummaryText("+3 more")

        )
        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = false,
            isCancelEnabled = true
        )
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
    }

    private fun cancelNotification() {
        setNotificationButtonState(true, isUpdateEnabled = false, isCancelEnabled = false)
        mNotifyManager.cancel(NOTIFICATION_ID)
    }

    private fun setNotificationButtonState(
        isNotifyEnabled: Boolean,
        isUpdateEnabled: Boolean,
        isCancelEnabled: Boolean
    ) {
        findViewById<Button>(R.id.notify).isEnabled = isNotifyEnabled
        findViewById<Button>(R.id.update).isEnabled = isUpdateEnabled
        findViewById<Button>(R.id.cancel).isEnabled = isCancelEnabled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
        findViewById<Button>(R.id.notify).setOnClickListener {
            sendNotification()
            sendNotification2()
            val inflater = layoutInflater
            val layout: View =
                layoutInflater.inflate(
                    R.layout.custom_toast,
                    findViewById(R.id.custom_toast_container)
                )

            val text: TextView = layout.findViewById(R.id.text)
            text.text = "new toast here"

            val toast = Toast(applicationContext)
            toast.setGravity(Gravity.BOTTOM, 0, 40)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layoutInflater.inflate(R.layout.custom_toast, null)
            toast.show()
        }

        findViewById<Button>(R.id.update).setOnClickListener {
            updateNotification()
        }

        findViewById<Button>(R.id.cancel).setOnClickListener {
            cancelNotification()
        }
        val mReceiver = NotificationReceiver()
        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))

        val mReceiverr = NotificationReceiverReset()
        registerReceiver(mReceiverr, IntentFilter(ACTION_RESET_NOTIFICATION))

    }

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateNotification()
        }
    }

    inner class NotificationReceiverReset : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setNotificationButtonState(
                isNotifyEnabled = true,
                isUpdateEnabled = false,
                isCancelEnabled = false
            )
        }
    }

}

