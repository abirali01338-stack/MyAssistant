package com.myassistant.app

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.service.quicksettings.TileService
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

object Prefs {
    private fun p(c: Context) = c.getSharedPreferences("assistant", Context.MODE_PRIVATE)
    fun isOn(c: Context) = p(c).getBoolean("on", false)
    fun setOn(c: Context, v: Boolean) = p(c).edit().putBoolean("on", v).apply()
}

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var toggle: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (24 * resources.displayMetrics.density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 2, pad, pad)
        }
        val title = TextView(this).apply {
            text = "আমার এআই সহকারী"
            textSize = 24f
        }
        toggle = Switch(this).apply {
            text = "এআই চালু / বন্ধ"
            textSize = 18f
            setPadding(0, pad, 0, pad)
            setOnCheckedChangeListener { _, checked ->
                Prefs.setOn(this@MainActivity, checked)
                TileService.requestListeningState(
                    this@MainActivity,
                    ComponentName(this@MainActivity, AssistantTileService::class.java)
                )
            }
        }
        status = TextView(this).apply { textSize = 16f }
        val permBtn = Button(this).apply {
            text = "নোটিফিকেশন পড়ার অনুমতি দিন"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }

        root.addView(title)
        root.addView(toggle)
        root.addView(status)
        root.addView(permBtn)
        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        toggle.isChecked = Prefs.isOn(this)
        val enabled = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val granted = enabled?.contains(packageName) == true
        status.text = if (granted) "নোটিফিকেশন অনুমতি: দেওয়া আছে" else "নোটিফিকেশন অনুমতি: দেওয়া হয়নি"
    }
}
