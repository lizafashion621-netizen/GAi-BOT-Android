package com.gai.scanner

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private val captureRequest = 701
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(28, 36, 28, 36); setBackgroundColor(0xFF12141C.toInt()) }
        root.addView(TextView(this).apply { text = "GAi BOT"; textSize = 28f; setTextColor(0xFF8DFF16.toInt()) })
        root.addView(TextView(this).apply { text = "Floating visual candle scanner"; textSize = 16f; setTextColor(-1); setPadding(0, 12, 0, 20) })
        root.addView(TextView(this).apply { text = "Allow display-over-other-apps, then tap Start floating popup. Tap SCAN on the popup and approve Android screen capture. The visual result is an estimate, not a guaranteed trading signal."; textSize = 14f; setTextColor(0xFFB8C0D4.toInt()); setPadding(0, 0, 0, 22) })
        root.addView(Button(this).apply { text = "1 · Allow floating popup"; setOnClickListener { if (!Settings.canDrawOverlays(this@MainActivity)) startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) else startFloating() } })
        root.addView(Button(this).apply { text = "2 · Start screen scan"; setOnClickListener { requestCapture() } })
        root.addView(Button(this).apply { text = "Stop floating popup / scan"; setOnClickListener { stopService(Intent(this@MainActivity, FloatingService::class.java)); stopService(Intent(this@MainActivity, ScreenCaptureService::class.java)) } })
        setContentView(root)
        if (Settings.canDrawOverlays(this)) startFloating()
        if (intent.getBooleanExtra("startCapture", false)) root.post { requestCapture() }
    }
    override fun onResume() { super.onResume(); if (Settings.canDrawOverlays(this)) startFloating() }
    private fun startFloating() { startService(Intent(this, FloatingService::class.java)) }
    private fun requestCapture() { val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager; startActivityForResult(mgr.createScreenCaptureIntent(), captureRequest) }
    @Deprecated("Deprecated by platform but retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { super.onActivityResult(requestCode, resultCode, data); if (requestCode == captureRequest && resultCode == RESULT_OK && data != null) { val i = Intent(this, ScreenCaptureService::class.java).putExtra("resultCode", resultCode).putExtra("resultData", data); if (Build.VERSION.SDK_INT >= 26) startForegroundService(i) else startService(i); finish() } }
}
