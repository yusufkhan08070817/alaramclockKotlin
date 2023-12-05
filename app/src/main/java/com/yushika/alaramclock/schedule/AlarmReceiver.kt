package com.yushika.alaramclock.schedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import com.yushika.alaramclock.MainActivity
import com.yushika.alaramclock.R

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val massage=intent?.getStringExtra("extramsg")
        println(massage)
        Log.e("message","massage $massage")
        var mediaPlayer = MediaPlayer.create(context, R.raw.music)
        mediaPlayer.start() // no need to call prepare(); create() does that for you
        MainActivity.textToSpeech.speak(massage, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
