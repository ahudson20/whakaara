package com.app.whakaara.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class Receiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action.equals("com.app.whakaara")) {
            val extras = intent.extras
            Toast.makeText(context, extras?.getString("message") ?: "message null", Toast.LENGTH_LONG).show()
            val notification = Notifications()
            notification.notify(context!!, extras!!.getString("message")!!,10)
        } else if(intent.action.equals("android.intent.action.BOOT_COMPLETED")) {

            println("do some stuff..? just finished doing some stuff..")
//            val saveData = SaveData(context!!)
//            saveData.setAlarm()
        }
    }
}