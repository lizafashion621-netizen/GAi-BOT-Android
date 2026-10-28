package com.gai.scanner

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Color
import android.graphics.Typeface
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

class FloatingService : Service() {
    private lateinit var wm: WindowManager
    private var panel: LinearLayout? = null
    private var resultView: TextView? = null
    private var scanLine: TextView? = null
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onCreate() { super.onCreate(); showPanel() }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { return START_STICKY }
    private fun showPanel() {
        if (!Settings.canDrawOverlays(this) || panel != null) return
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(12, 10, 12, 10); setBackgroundColor(Color.rgb(21, 25, 34)) }
        val handle = TextView(this).apply { text = "━━  GAi BOT  ━━"; textSize = 12f; gravity = Gravity.CENTER; setTextColor(Color.rgb(141,255,22)); typeface = Typeface.DEFAULT_BOLD; setPadding(2, 2, 2, 8) }
        scanLine = TextView(this).apply { text = "━━━━━━━━━━━━"; gravity = Gravity.CENTER; textSize = 16f; setTextColor(Color.rgb(141,255,22)); setPadding(0, 8, 0, 8) }
        resultView = TextView(this).apply { text = "READY"; gravity = Gravity.CENTER; textSize = 16f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); setPadding(0, 4, 0, 8) }
        val scan = TextView(this).apply { text = "▶ SCAN SCREEN"; gravity = Gravity.CENTER; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.BLACK); setBackgroundColor(Color.rgb(141,255,22)); setPadding(10, 10, 10, 10); setOnClickListener { resultView?.text = "ALLOW CAPTURE…"; val i = Intent(this@FloatingService, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i.apply { putExtra("startCapture", true) }) } }
        root.addView(handle); root.addView(scanLine); root.addView(resultView); root.addView(scan)
        @Suppress("DEPRECATION")
        val type = if (android.os.Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        val p = WindowManager.LayoutParams(190, WindowManager.LayoutParams.WRAP_CONTENT, type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.END; x = 12; y = 160 }
        var sx=0f; var sy=0f; var px=0; var py=0
        handle.setOnTouchListener { _, e -> when(e.action) { MotionEvent.ACTION_DOWN -> { sx=e.rawX; sy=e.rawY; px=p.x; py=p.y; true }; MotionEvent.ACTION_MOVE -> { p.x=(px+(sx-e.rawX)).toInt().coerceAtLeast(0); p.y=(py+(e.rawY-sy)).toInt().coerceAtLeast(0); wm.updateViewLayout(root,p); true }; else -> false } }
        wm.addView(root, p); panel = root
        val filter = android.content.IntentFilter("com.gai.scanner.RESULT"); if (android.os.Build.VERSION.SDK_INT >= 33) registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED) else @Suppress("DEPRECATION") registerReceiver(receiver, filter)
    }
    private val receiver = object : android.content.BroadcastReceiver() { override fun onReceive(context: android.content.Context?, intent: Intent?) { val dir=intent?.getStringExtra("signal") ?: "NO CLEAR CANDLE"; resultView?.text=dir; resultView?.setTextColor(if(dir=="UP") Color.GREEN else if(dir=="DOWN") Color.RED else Color.WHITE); scanLine?.setTextColor(if(dir=="UP") Color.GREEN else if(dir=="DOWN") Color.RED else 0xFF8DFF16.toInt()) } }
    override fun onDestroy() { try { unregisterReceiver(receiver) } catch (_: Exception) {}; panel?.let { try { wm.removeView(it) } catch (_: Exception) {} }; panel=null; super.onDestroy() }
}
