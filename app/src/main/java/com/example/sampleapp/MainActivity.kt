package com.example.sampleapp
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.telephony.SmsManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var chatView: TextView
    private val REQ_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(30,50,30,30); setBackgroundColor(0xFFFFE4EC.toInt()) }
        val title = TextView(this).apply { text = "Myra - Your AI Girlfriend ❤️"; textSize = 22f; setTextColor(0xFFE91E63.toInt()) }
        chatView = TextView(this).apply { text = "Myra: Hi Meri Jaan! Bolo kya karna hai? ❤️\n"; textSize = 16f }
        val btnMic = Button(this).apply { text = "🎤 Bolo Jaan" }
        btnMic.setOnClickListener { startVoice() }
        layout.addView(title); layout.addView(chatView); layout.addView(btnMic)
        setContentView(layout)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE), 1)
    }
    private fun startVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        startActivityForResult(intent, REQ_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQ_CODE && resultCode == RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            chatView.append("\nYou: $result\n")
            handleCommand(result.lowercase())
        }
    }
    private fun handleCommand(cmd: String){
        var reply = ""
        when {
            cmd.contains("whatsapp") -> { openApp("com.whatsapp"); reply = "WhatsApp khol rahi hu Jaan ❤️" }
            cmd.contains("youtube") -> { openApp("com.google.android.youtube"); reply = "YouTube khol diya Jaan" }
            cmd.contains("instagram") -> { openApp("com.instagram.android"); reply = "Instagram khol diya Jaan" }
            cmd.contains("camera") -> { startActivity(Intent("android.media.action.IMAGE_CAPTURE")); reply = "Camera khol diya Jaan" }
            cmd.contains("weather") -> { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://weather.com"))); reply = "Weather dikha rahi hu Jaan" }
            cmd.contains("call") -> { makeCall("03000000000"); reply = "Call laga rahi hu Jaan" }
            else -> { reply = "Haan Jaan? Tumne bola $cmd, bolo aur kya chahiye? ❤️" }
        }
        chatView.append("Myra: $reply\n")
        tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    private fun openApp(pkg: String){
        val launch = packageManager.getLaunchIntentForPackage(pkg)
        if(launch != null) startActivity(launch)
    }
    private fun makeCall(num: String){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$num")))
        }
    }
    override fun onInit(status: Int) { if(status == TextToSpeech.SUCCESS) { tts.language = Locale("en", "US"); tts.setSpeechRate(0.9f) } }
}
