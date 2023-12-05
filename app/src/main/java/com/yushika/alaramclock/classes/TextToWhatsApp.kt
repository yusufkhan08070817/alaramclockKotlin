package com.yushika.alaramclock.classes


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

class TextToWhatsApp(val phoneNumber: String, val context: Context) {

    fun sentMsg() {
        val sendIntent = Intent("android.intent.action.MAIN")
        sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
        sendIntent.putExtra("jid", "$phoneNumber@s.whatsapp.net")

        startActivity(context, sendIntent, null) // Pass null as the third parameter for options
    }
}