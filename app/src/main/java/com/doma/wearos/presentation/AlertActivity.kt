package com.doma.wearos.presentation

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.doma.wearos.R
import java.util.Locale

/** AlertActivity — Alertas críticos de segurança com TTS + vibração. */
class AlertActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tvAlertStatus: TextView
    private lateinit var btnEvacuacao: Button
    private lateinit var btnIncendio: Button
    private lateinit var btnMedico: Button
    private lateinit var btnSeguranca: Button

    private lateinit var tts: TextToSpeech
    private lateinit var vibrator: Vibrator
    private lateinit var audioHelper: AudioHelper
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)

        audioHelper = AudioHelper(this)
        vibrator    = getSystemService(VIBRATOR_SERVICE) as Vibrator
        tts         = TextToSpeech(this, this)

        tvAlertStatus = findViewById(R.id.tv_alert_status)
        btnEvacuacao  = findViewById(R.id.btn_evacuacao)
        btnIncendio   = findViewById(R.id.btn_incendio)
        btnMedico     = findViewById(R.id.btn_medico)
        btnSeguranca  = findViewById(R.id.btn_seguranca)

        btnEvacuacao.setOnClickListener {
            emitirAlerta("🚨 EVACUAÇÃO",
                "Alerta de evacuação! Dirija-se à saída de emergência mais próxima!",
                longArrayOf(0, 500, 200, 500, 200, 500))
        }
        btnIncendio.setOnClickListener {
            emitirAlerta("🔥 INCÊNDIO",
                "Alerta de incêndio! Saia do prédio imediatamente!",
                longArrayOf(0, 300, 100, 300, 100, 300))
        }
        btnMedico.setOnClickListener {
            emitirAlerta("🏥 EMERGÊNCIA MÉDICA",
                "Emergência médica! Um funcionário precisa de atendimento urgente!",
                longArrayOf(0, 200, 200, 200))
        }
        btnSeguranca.setOnClickListener {
            emitirAlerta("🛡️ SEGURANÇA",
                "Aviso de segurança da empresa Doma. Verifique suas instruções.",
                longArrayOf(0, 100, 100))
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("pt", "BR"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                       result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) { tts.stop(); tts.shutdown() }
    }

    private fun emitirAlerta(titulo: String, mensagem: String, vibrPattern: LongArray) {
        tvAlertStatus.text = "⚡ $titulo ATIVADO"
        val audioOk = audioHelper.isSpeakerAvailable() ||
                      audioHelper.isBluetoothHeadsetConnected()
        if (audioOk && ttsReady) {
            tts.speak(mensagem, TextToSpeech.QUEUE_FLUSH, null, "doma_alert")
        } else {
            Toast.makeText(this, "Sem áudio — usando vibração!", Toast.LENGTH_LONG).show()
        }
        vibrator.vibrate(VibrationEffect.createWaveform(vibrPattern, -1))
    }
}
