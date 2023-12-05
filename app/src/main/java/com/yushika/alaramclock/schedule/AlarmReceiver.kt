package com.yushika.alaramclock.schedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import com.yushika.alaramclock.MainActivity
import com.yushika.alaramclock.MainActivity.Companion.ws
import com.yushika.alaramclock.R
import com.yushika.alaramclock.classes.FloatingWindowService
import com.yushika.alaramclock.classes.TextToWhatsApp

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val massage=intent?.getStringExtra("extramsg")
        val option= intent?.getStringExtra("option")!!.toInt()
        println(massage)
        Log.e("message","massage $massage")
        val Intent=intent
       when(option)
       {
           0->{
               Log.e("hii","hii")
               context!!.startService(Intent(context,FloatingWindowService::class.java))
               var mediaPlayer = MediaPlayer.create(context, R.raw.music)
               mediaPlayer.start() // no need to call prepare(); create() does that for you
               MainActivity.textToSpeech.speak(massage, TextToSpeech.QUEUE_FLUSH, null, null)
           }
           1->{
try {
    ws.sentMsg()
}catch (e:Exception)
{
    Log.e("whatsapperror","$e")
}
           }
       }

    }
}
