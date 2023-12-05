package com.yushika.alaramclock.schedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.ZoneId

class AleramSchudler(private val context: Context):Alarmschedularinterface
{
    private val alarmmanager=context.getSystemService(AlarmManager::class.java)
    override fun schedule(item: alermitem) {
        val intent=Intent(context,AlarmReceiver::class.java).apply {
            putExtra("extramsg",item.message.toString())

        }
        alarmmanager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,item.time.atZone(ZoneId.systemDefault()).toEpochSecond()*1000,PendingIntent.getBroadcast(context,item.hashCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        )
    }

    override fun cancle(item: alermitem) {
        TODO("Not yet implemented")
    }
}