package com.example.workman.View.Create_Alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.workman.R


class RingtoneService : Service(){
    companion object {
        lateinit var r:Ringtone
    }
    var id : Int = 0
    var isRunning: Boolean = false
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var state: String = intent!!.getStringExtra("extra")
        assert(state != null)
        when(state){
            "on" -> id = 1
            "off" -> id = 0
        }
        if (!this.isRunning && id == 1){
            playAlarm()
            this.isRunning = true
            this.id = 0
            fireNotification()
        }else if (this.isRunning && id == 0){
            r.stop()
            this.isRunning = false
            this.id = 0
        }else if (!this.isRunning && id == 0){
            this.isRunning = false
            this.id = 0
        }else if (this.isRunning && id == 1){
            this.isRunning = true
            this.id = 1
        }else {

        }
        return START_NOT_STICKY
    }

    private fun fireNotification() {
        var main_activity_intent : Intent = Intent(this,Create_Alarm::class.java)
        var pi : PendingIntent = PendingIntent.getActivity(this,0,main_activity_intent,0)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val CHANNEL_ID = "default"
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )
//        var notify_manager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notification : Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm is going off")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("Click Me")
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        startForeground(1,notification)
    }

    private fun playAlarm() {
        var alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if ( alarmUri == null){
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        r = RingtoneManager.getRingtone(baseContext,alarmUri)
        r.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.isRunning = false
    }
}