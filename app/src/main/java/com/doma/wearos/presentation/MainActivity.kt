package com.doma.wearos.presentation

import android.content.Intent
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.doma.wearos.R

/**
 * MainActivity — Tela principal do app Doma Wear OS.
 *
 * Exibe ListView de funcionalidades assistivas e botões de acesso
 * rápido. Registra AudioDeviceCallback para monitorar conexão de
 * fones em tempo real.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tvAudioStatus: TextView
    private lateinit var lvFuncionalidades: ListView
    private lateinit var btnAudioAssist: Button
    private lateinit var btnAlertas: Button
    private lateinit var btnBluetooth: Button

    private lateinit var audioHelper: AudioHelper
    private lateinit var audioManager: AudioManager

    /**
     * Callback dinâmico — detecta fones conectados/desconectados em tempo real.
     */
    private val audioDeviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
            if (audioHelper.isBluetoothHeadsetConnected())
                updateAudioStatus("🎧 Fone Bluetooth conectado!")
        }
        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
            if (!audioHelper.isBluetoothHeadsetConnected())
                updateAudioStatus("📢 Bluetooth desconectado. Usando alto-falante.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioHelper  = AudioHelper(this)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        tvAudioStatus     = findViewById(R.id.tv_audio_status)
        lvFuncionalidades = findViewById(R.id.lv_funcionalidades)
        btnAudioAssist    = findViewById(R.id.btn_audio_assist)
        btnAlertas        = findViewById(R.id.btn_alertas)
        btnBluetooth      = findViewById(R.id.btn_bluetooth)

        // Popula lista de funcionalidades
        val funcionalidades = listOf(
            "🔊 Leitura de mensagens de texto",
            "🔔 Notificações em áudio",
            "⏰ Lembretes por voz",
            "🎙️ Respostas a comandos de voz",
            "📚 Feedback auditivo em treinamentos",
            "🚨 Alertas críticos de segurança",
            "📡 Detecção de saídas de áudio",
            "🎧 Suporte a Bluetooth A2DP",
            "📢 Alto-falante integrado"
        )
        lvFuncionalidades.adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, funcionalidades
        )

        // Botões de navegação
        btnAudioAssist.setOnClickListener {
            startActivity(Intent(this, AudioAssistActivity::class.java))
        }
        btnAlertas.setOnClickListener {
            startActivity(Intent(this, AlertActivity::class.java))
        }
        btnBluetooth.setOnClickListener { openBluetoothSettings() }

        // Registra callback de áudio
        audioManager.registerAudioDeviceCallback(audioDeviceCallback, null)

        // Status inicial
        updateAudioStatus(buildInitialAudioStatus())
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.unregisterAudioDeviceCallback(audioDeviceCallback)
    }

    /**
     * Abre configurações Bluetooth do sistema para parear fones assistivos.
     */
    private fun openBluetoothSettings() {
        val intent = with(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("EXTRA_CONNECTION_ONLY", true)
            putExtra("EXTRA_CLOSE_ON_CONNECT", true)
            putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 1)
            this
        }
        startActivity(intent)
    }

    private fun buildInitialAudioStatus(): String = when {
        audioHelper.isBluetoothHeadsetConnected() -> "🎧 Fone Bluetooth | Áudio pronto"
        audioHelper.isWiredHeadsetConnected()     -> "🎧 Fone com fio | Áudio pronto"
        audioHelper.isSpeakerAvailable()          -> "📢 Alto-falante | Áudio pronto"
        else                                       -> "⚠️ Nenhuma saída de áudio detectada"
    }

    private fun updateAudioStatus(status: String) {
        runOnUiThread { tvAudioStatus.text = status }
    }
}
