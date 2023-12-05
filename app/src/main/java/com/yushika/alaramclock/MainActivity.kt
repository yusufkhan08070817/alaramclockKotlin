package com.yushika.alaramclock

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.yushika.alaramclock.classes.Handlerclass
import com.yushika.alaramclock.classes.MyAdapter

import com.yushika.alaramclock.classes.permision
import com.yushika.alaramclock.databinding.ActivityMainBinding
import com.yushika.alaramclock.schedule.AleramSchudler
import com.yushika.alaramclock.schedule.alermitem
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    lateinit var bind: ActivityMainBinding
    private lateinit var timeformat: SimpleDateFormat
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: permision
    lateinit var arrayhr: ArrayList<String>
    lateinit var arraymin: ArrayList<String>
    var arrayampm = ArrayList<String>()
    var chr: Int = 0
    var cmin: Int = 0
    var campm: Int = 0

    companion object {
        lateinit var textToSpeech: TextToSpeech
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        textToSpeech = TextToSpeech(this, this)
        textToSpeech = TextToSpeech(this, this)
        var hr: Int = 0
        var min: Int = 0
        var sec: Int = 0
        window()
        val list = listOf<String>(
            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        hr()
        min()
        arrayampm.add("Alarm")
        arrayampm.add("Whatsapp")
        arrayampm.add("Weather")
        arrayampm.add("News")

        managePermissions = permision(this, list, PermissionsRequestCode)
        managePermissions.checkPermissions()
        retrieveWallpaper()
        timeformat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        Handlerclass({

            val currenttime = System.currentTimeMillis()
            val formattedtime = timeformat.format(Date(currenttime))

            hr = formattedtime.substring(0, 2).toInt()
            min = formattedtime.substring(3, 5).toInt()
            sec = formattedtime.substring(6).toInt()
            when (campm) {
                0 -> {
                    bind.customtext.hint = "Alarm"

                }

                1 -> {
                    bind.customtext.hint = "Whatsapp"
                }

                2 -> {
                    bind.customtext.hint = "Weather"
                }

                3 -> {
                    bind.customtext.hint = "News"
                }
            }


            bind.digiclock.text = "$hr : $min : $sec"

        }, 1)

        val hrlaymanager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.listhr.layoutManager = hrlaymanager
        bind.listhr.adapter = MyAdapter(this, arrayhr)
        LinearSnapHelper().attachToRecyclerView(bind.listhr)
        repo(bind.listhr, hrlaymanager, 1)


        val minlaymanager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.listmin.layoutManager = minlaymanager
        bind.listmin.adapter = MyAdapter(this, arraymin)
        LinearSnapHelper().attachToRecyclerView(bind.listmin)
        repo(bind.listmin, minlaymanager, 2)


        val ampmlaymanager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.listampm.layoutManager = ampmlaymanager
        bind.listampm.adapter = MyAdapter(this, arrayampm, 20f)
        LinearSnapHelper().attachToRecyclerView(bind.listampm)
        repo(bind.listampm, ampmlaymanager, 3)
        val alerm = AleramSchudler(this)
        bind.setalarm.setOnClickListener {
var senthr=hr-chr
            if (senthr<0)
            {
                senthr *= -1
            }
            var sentmin=min-cmin
            if (sentmin<0)
                sentmin *= -1
            var timetoset:Long=((senthr*60)+sentmin*60).toLong()
            Log.e("settime",timetoset.toString())
            val alarmitem=  alermitem(time = LocalDateTime.now().plusSeconds(timetoset),bind.customtext.text.toString())
            alarmitem.let (alerm::schedule)

        }
    }

    fun retrieveWallpaper() {

        val wallpaperManager = WallpaperManager.getInstance(this)


        val wallpaperDrawable = wallpaperManager.drawable

        bind.backgroundimg.setImageDrawable(wallpaperDrawable)
    }

    fun window() {
        View.SYSTEM_UI_FLAG_IMMERSIVE
        // Set the content to appear under the system bars so that the
        // content doesn't resize when the system bars hide and show.
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        // Hide the nav bar and status bar
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    fun hr() {
        arrayhr = ArrayList<String>()
        for (i in 0..24) {
            if (i < 10) {
                arrayhr.add("0$i")
            } else {
                arrayhr.add("$i")
            }
        }
    }

    fun min() {
        arraymin = ArrayList<String>()
        for (i in 0..60) {
            if (i < 10) {
                arraymin.add("0$i")
            } else {
                arraymin.add("$i")
            }
        }
    }

    fun repo(re: RecyclerView, laymanager: LinearLayoutManager, option: Int) {
        re.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val centerPosition =
                    laymanager.findFirstVisibleItemPosition() + laymanager.childCount / 2
                // Use the centerPosition as needed
                // For example, you can log it
                when (option) {
                    1 -> {
                        chr = centerPosition

                    }

                    2 -> {
                        cmin = centerPosition
                    }

                    3 -> {
                        campm = centerPosition
                    }

                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speakOut(text: String = "abhi kuch ni h boolnay ko") {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/*

   textToSpeech = TextToSpeech(this, this)
        var hr: Int = 0
        var min: Int = 0
        var sec: Int = 0
        window()
        val list = listOf<String>(
            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        hr()
        min()
        arrayampm.add("Alarm")
        arrayampm.add("Whatsapp")
        arrayampm.add("Weather")
        arrayampm.add("News")

        managePermissions = permision(this, list, PermissionsRequestCode)
        managePermissions.checkPermissions()
        retrieveWallpaper()
        timeformat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val t = ToastE(this, 1)
        Handlerclass({

            val currenttime = System.currentTimeMillis()
            val formattedtime = timeformat.format(Date(currenttime))

            hr = formattedtime.substring(0, 2).toInt()
            min = formattedtime.substring(3, 5).toInt()
            sec = formattedtime.substring(6).toInt()
            when (campm) {
                0 -> {
                    bind.customtext.hint = "Alarm"

                }

                1 -> {
                    bind.customtext.hint = "Whatsapp"
                }

                2 -> {
                    bind.customtext.hint = "Weather"
                }

                3 -> {
                    bind.customtext.hint = "News"
                }
            }


            bind.digiclock.text = "$hr : $min : $sec"
            t.toast("$chr $cmin $campm")
        }, 1)

        val hrlaymanager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.listhr.layoutManager = hrlaymanager
        bind.listhr.adapter = MyAdapter(this, arrayhr)
        LinearSnapHelper().attachToRecyclerView(bind.listhr)
        repo(bind.listhr, hrlaymanager, 1)


        val minlaymanager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.listmin.layoutManager = minlaymanager
        bind.listmin.adapter = MyAdapter(this, arraymin)
        LinearSnapHelper().attachToRecyclerView(bind.listmin)
        repo(bind.listmin, minlaymanager, 2)


        val ampmlaymanager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.listampm.layoutManager = ampmlaymanager
        bind.listampm.adapter = MyAdapter(this, arrayampm, 20f)
        LinearSnapHelper().attachToRecyclerView(bind.listampm)
        repo(bind.listampm, ampmlaymanager, 3)
        val alerm = AleramSchudler(this)
        bind.setalarm.setOnClickListener {



        }
    }

    fun retrieveWallpaper() {

        val wallpaperManager = WallpaperManager.getInstance(this)


        val wallpaperDrawable = wallpaperManager.drawable

        bind.backgroundimg.setImageDrawable(wallpaperDrawable)
    }

    fun window() {
        View.SYSTEM_UI_FLAG_IMMERSIVE
        // Set the content to appear under the system bars so that the
        // content doesn't resize when the system bars hide and show.
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        // Hide the nav bar and status bar
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    fun hr() {
        arrayhr = ArrayList<String>()
        for (i in 0..24) {
            if (i < 10) {
                arrayhr.add("0$i")
            } else {
                arrayhr.add("$i")
            }
        }
    }

    fun min() {
        arraymin = ArrayList<String>()
        for (i in 0..60) {
            if (i < 10) {
                arraymin.add("0$i")
            } else {
                arraymin.add("$i")
            }
        }
    }

    fun repo(re: RecyclerView, laymanager: LinearLayoutManager, option: Int) {
        re.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val centerPosition =
                    laymanager.findFirstVisibleItemPosition() + laymanager.childCount / 2
                // Use the centerPosition as needed
                // For example, you can log it
                when (option) {
                    1 -> {
                        chr = centerPosition

                    }

                    2 -> {
                        cmin = centerPosition
                    }

                    3 -> {
                        campm = centerPosition
                    }

                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speakOut(text: String = "abhi kuch ni h boolnay ko") {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

 */