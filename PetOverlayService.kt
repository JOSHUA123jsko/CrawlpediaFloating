package com.crawlpedia.floating

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class PetOverlayService : Service() {
    private lateinit var wm: WindowManager
    private var pet: PetView? = null
    private lateinit var params: WindowManager.LayoutParams
    private val channelId = "crawlpedia_pets"

    override fun onCreate() {
        super.onCreate()
        createChannel()
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notification: Notification = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, channelId).setContentTitle("Crawlpedia pets are active").setContentText("Tap your creature to interact. Open Crawlpedia to stop it.").setSmallIcon(android.R.drawable.ic_menu_compass).setContentIntent(open).setOngoing(true).build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this).setContentTitle("Crawlpedia pets are active").setContentText("Your floating creature is roaming").setSmallIcon(android.R.drawable.ic_menu_compass).setOngoing(true).build()
        }
        startForeground(41, notification)
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams(170, 170, if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            android.graphics.PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.START; x = 40; y = 220 }
        pet = PetView().also { wm.addView(it, params) }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(channelId, "Floating pets", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        pet?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        pet = null
        super.onDestroy()
    }

    inner class PetView : View(this@PetOverlayService) {
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private var tick = 0f
        private var vx = 2.2f
        private var vy = 1.4f
        private var downX = 0f
        private var downY = 0f
        private var moved = false
        private var following = false
        private var species = Random.nextInt(0, 1111)
        private val colors = longArrayOf(0xFFFF8EAA, 0xFFFFC857, 0xFF8CE6FF, 0xFFB8A0FF, 0xFF8FE3B0)
        private val bodyColor get() = colors[species % colors.size].toInt()
        private val loop = object : Runnable {
            override fun run() {
                tick += 0.13f
                if (!following && !moved) {
                    params.x += vx.toInt(); params.y += vy.toInt()
                    val maxX = (resources.displayMetrics.widthPixels - width).coerceAtLeast(1)
                    val maxY = (resources.displayMetrics.heightPixels - height).coerceAtLeast(1)
                    if (params.x < 0 || params.x > maxX) vx = -vx
                    if (params.y < 30 || params.y > maxY) vy = -vy
                    params.x = params.x.coerceIn(0, maxX); params.y = params.y.coerceIn(30, maxY)
                    try { wm.updateViewLayout(this@PetView, params) } catch (_: Exception) {}
                }
                invalidate()
                postDelayed(this, 32)
            }
        }

        init { setLayerType(LAYER_TYPE_SOFTWARE, null); post(loop) }

        override fun onDraw(c: Canvas) {
            super.onDraw(c)
            val cx = width / 2f; val cy = height / 2f + 4f
            val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = RadialGradient(cx, cy, 66f, intArrayOf(0x5596CFFF, 0x0096CFFF), null, Shader.TileMode.CLAMP) }
            c.drawCircle(cx, cy, 66f, glow)
            val leg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF27334B.toInt(); style = Paint.Style.STROKE; strokeWidth = 3.4f; strokeCap = Paint.Cap.ROUND }
            for (i in 0..3) {
                val yy = cy - 18 + i * 12f
                val wave = sin(tick + i) * 5f
                c.drawLine(cx - 25, yy, cx - 43, yy - 12 + wave, leg)
                c.drawLine(cx - 43, yy - 12 + wave, cx - 51, yy + 1 + wave, leg)
                c.drawLine(cx + 25, yy, cx + 43, yy - 12 - wave, leg)
                c.drawLine(cx + 43, yy - 12 - wave, cx + 51, yy + 1 - wave, leg)
            }
            p.style = Paint.Style.FILL; p.color = bodyColor
            c.drawOval(cx - 29, cy - 24, cx + 29, cy + 24, p)
            p.color = 0xFFFFE9F1.toInt(); c.drawCircle(cx - 9, cy - 4, 5f, p); c.drawCircle(cx + 9, cy - 4, 5f, p)
            p.color = 0xFF20243A.toInt(); c.drawCircle(cx - 8, cy - 3, 2.2f, p); c.drawCircle(cx + 10, cy - 3, 2.2f, p)
            p.color = 0xFF20243A.toInt(); p.strokeWidth = 2f; p.style = Paint.Style.STROKE
            c.drawArc(cx - 7, cy + 1, cx + 7, cy + 13, 5f, 170f, false, p); p.style = Paint.Style.FILL
            p.color = 0xCCFFFFFF.toInt(); p.textSize = 13f; c.drawText("#${species + 1}", cx - 15, height - 12f, p)
        }

        override fun onTouchEvent(e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { downX = e.rawX; downY = e.rawY; moved = false; following = true; return true }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (e.rawX - downX).toInt(); val dy = (e.rawY - downY).toInt()
                    if (kotlin.math.abs(dx) + kotlin.math.abs(dy) > 5) moved = true
                    params.x += dx; params.y += dy; downX = e.rawX; downY = e.rawY
                    try { wm.updateViewLayout(this, params) } catch (_: Exception) {}
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    following = false
                    if (!moved) { species = (species + 1) % 1111; vx = if (Random.nextBoolean()) 2.4f else -2.4f; vy = if (Random.nextBoolean()) 1.8f else -1.8f; invalidate() }
                    return true
                }
            }
            return true
        }
    }
}
