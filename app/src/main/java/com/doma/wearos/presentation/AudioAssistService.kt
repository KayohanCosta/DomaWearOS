package com.doma.wearos.presentation

import android.app.Service
import android.content.Intent
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.IBinder
import android.speech.tts.TextToSpeech
import java.util.Locale

/** AudioAssistService — Monitora dispositivos de áudio em segundo plano. */
class AudioAssistService : Service(), TextToSpeech.OnInitListener {

    private lateinit var audioManager: AudioManager
    private lateinit var audioHelper: AudioHelper
    private lateinit var tts: TextToSpeech
    private var ttsReady = false

    private val audioDeviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
            if (audioHelper.isBluetoothHeadsetConnected())
                anunciarEmVoz("Fone de ouvido Bluetooth conectado. Áudio assistivo disponível.")
        }
        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
            if (!audioHelper.isBluetoothHeadsetConnected())
                anunciarEmVoz("Fone de ouvido Bluetooth desconectado.")
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioHelper  = AudioHelper(this)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        tts          = TextToSpeech(this, this)
        audioManager.registerAudioDeviceCallback(audioDeviceCallback, null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(EXTRA_MESSAGE)?.let { anunciarEmVoz(it) }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.unregisterAudioDeviceCallback(audioDeviceCallback)
        if (::tts.isInitialized) { tts.stop(); tts.shutdown() }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("pt", "BR"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                       result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    private fun anunciarEmVoz(texto: String) {
        if (ttsReady && (audioHelper.isSpeakerAvailable() || audioHelper.isBluetoothHeadsetConnected()))
            tts.speak(texto, TextToSpeech.QUEUE_ADD, null, "doma_service")
    }

    companion object { const val EXTRA_MESSAGE = "extra_message" }
}
