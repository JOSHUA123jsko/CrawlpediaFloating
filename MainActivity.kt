package com.crawlpedia.floating

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 24, 22, 12)
            setBackgroundColor(0xFF081326.toInt())
        }
        val title = TextView(this).apply {
            text = "🕷️ Crawlpedia Floating Pets 🦋"
            textSize = 23f; setTextColor(0xFFFFFFFF.toInt()); gravity = Gravity.CENTER
            setPadding(8, 12, 8, 14)
        }
        val info = TextView(this).apply {
            text = "Browse Crawlpedia, then turn on floating mode. Your little creature can crawl around the screen, react to taps, and follow your finger."
            textSize = 15f; setTextColor(0xFFE0E8F5.toInt()); setPadding(4, 4, 4, 12)
        }
        val start = Button(this).apply { text = "START FLOATING PET"; setOnClickListener { startPets() } }
        val stop = Button(this).apply { text = "STOP FLOATING PET"; setOnClickListener { stopService(Intent(this@MainActivity, PetOverlayService::class.java)); Toast.makeText(this@MainActivity, "Floating pet stopped", Toast.LENGTH_SHORT).show() } }
        val web = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            loadUrl("file:///android_asset/crawlpedia.html")
        }
        root.addView(title, LinearLayout.LayoutParams(-1, -2))
        root.addView(info, LinearLayout.LayoutParams(-1, -2))
        root.addView(start, LinearLayout.LayoutParams(-1, -2))
        root.addView(stop, LinearLayout.LayoutParams(-1, -2))
        root.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun startPets() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
            Toast.makeText(this, "Allow Crawlpedia to display over other apps, then tap START again.", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(this, PetOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(intent) else startService(intent)
        Toast.makeText(this, "Your Crawlpedia pet is roaming!", Toast.LENGTH_SHORT).show()
    }
}
