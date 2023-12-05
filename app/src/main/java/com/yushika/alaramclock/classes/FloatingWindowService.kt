package com.yushika.alaramclock.classes

// FloatingWindowService.kt
import android.app.Service
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.yushika.alaramclock.MainActivity.Companion.hr
import com.yushika.alaramclock.R
import com.yushika.alaramclock.schedule.AleramSchudler
import com.yushika.alaramclock.schedule.alermitem
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var btnStopService: Button
    private lateinit var background: ImageView
    private val handler = Handler()
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private lateinit var hrl:ImageView
    private lateinit var minl:ImageView
    private lateinit var secl:ImageView
    private val logDelay = 60000L // 1 minute
 lateinit var  alerm:AleramSchudler

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Inflate the floating window layout
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_window_layout, null)

        // Set up layout parameters for the floating window
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }

        // Set the initial position of the floating window
        params.x = 0
        params.y = 100
        alerm = AleramSchudler(this)
        background=floatingView.findViewById(R.id.backfl)
secl=floatingView.findViewById(R.id.sec)
        minl=floatingView.findViewById(R.id.min)
        hrl=floatingView.findViewById(R.id.hr)
        val wallpaperManager = WallpaperManager.getInstance(this)


        val wallpaperDrawable = wallpaperManager.drawable

        background.setImageDrawable(wallpaperDrawable)

        // Add the view to the window manager
        windowManager.addView(floatingView, params)

        // Initialize the button and set the click listener
        btnStopService = floatingView.findViewById(R.id.btnStopService)
        btnStopService.setOnClickListener {
            stopSelf()
        }
        handler.postDelayed(updateTimeRunnable, 1000)
        handler.postDelayed(logErrorRunnable, logDelay)

    }
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            // Update the time
            updateCurrentTime()

            // Schedule the next update after 1 second
            handler.postDelayed(this, 1000)
        }
    }

    // Update the current time in the UI
    private fun updateCurrentTime() {
        val currentTime = System.currentTimeMillis()
        val formattedTime = timeFormat.format(currentTime)
        var hr=formattedTime.substring(0,2).toInt()
        var min=formattedTime.substring(3,5).toInt()
        var sec=formattedTime.substring(6).toInt()
        rotateimagewhithshift(hrl,tomap(hr,12),0.5f,1.0f)
        rotateimagewhithshift(minl,tomap(min,60),0.5f,1.0f)
        rotateimagewhithshift(secl,tomap(sec,60),0.5f,1.0f)
    }
    private fun rotateimagewhithshift(imageview:ImageView,degrees:Float,pivotxvalue:Float,pivotyvalue:Float)
    {
        imageview.pivotX=imageview.width*pivotxvalue
        imageview.pivotY=imageview.height*pivotyvalue
        imageview.animate().rotation(degrees)

    }
    private fun tomap(value:Int,max:Int):Float{
        return (value%max).toFloat()/max*360f
    }
    private val logErrorRunnable = object : Runnable {
        override fun run() {
            val alarmitem = alermitem(
                time = LocalDateTime.now().plusSeconds(1),
               "hey wake up",0
            )
            alarmitem.let(alerm::schedule)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Remove the view when the service is destroyed
        if (floatingView.isAttachedToWindow) {
            windowManager.removeView(floatingView)
            alerm.let { alerm::cancle }
        }
    }
}
