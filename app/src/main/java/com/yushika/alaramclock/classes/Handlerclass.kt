package com.yushika.alaramclock.classes

import android.os.Handler
import android.os.HandlerThread
import android.util.Log

class Handlerclass(runnable: () -> Unit, seconds: Long) {
    init {
      try {
          val handlerThread = HandlerThread("MyHandlerThread")
          handlerThread.start()
          val handler = Handler(handlerThread.looper)
          handler.post (object :Runnable{
              override fun run() {
                  runnable()
                  handler.postDelayed(this,seconds*1000)
              }

          })
      }catch (e:Exception)
      {
          Log.e("handler error",e.toString())
      }
    }

}