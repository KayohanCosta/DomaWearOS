package com.doma.wearos.presentation

import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.doma.wearos.R
import java.util.Locale

/**
 * AudioAssistActivity — Reprodução de mensagens via Text-to-Speech.
 *
 * Verifica a saída de áudio disponível antes de reproduzir,
 * implementando a lógica de audioOutputAvailable() do AudioHelper.
 */
class AudioAssistActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var etMensagem: EditText
    private lateinit var btnLerMensagem: Button
    private lateinit var btnPararAudio: Button
    private lateinit var tvStatusAudio: TextView
    private lateinit var tvSaidaAtiva: TextView

    private lateinit var audioHelper: AudioHelper
    private lateinit var tts: TextToSpeech
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_assist)

        audioHelper = AudioHelper(this)
        tts = TextToSpeech(this, this)

        etMensagem     = findViewById(R.id.et_mensagem)
        btnLerMensagem = findViewById(R.id.btn_ler_mensagem)
        btnPararAudio  = findViewById(R.id.btn_parar_audio)
        tvStatusAudio  = findViewById(R.id.tv_status_audio)
        tvSaidaAtiva   = findViewById(R.id.tv_saida_ativa)

        btnLerMensagem.setOnClickListener {
            val msg = etMensagem.text.toString().trim()
            if (msg.isEmpty()) {
                Toast.makeText(this, "Digite uma mensagem", Toast.LENGTH_SHORT).show()
            } else {
                reproduzirAudio(msg)
            }
        }

        btnPararAudio.setOnClickListener {
            tts.stop()
            tvStatusAudio.text = "⏹ Áudio pausado"
        }

        tvSaidaAtiva.text = "Saídas: ${audioHelper.getAudioOutputSummary()}"
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

    private fun reproduzirAudio(texto: String) {
        when {
            audioHelper.isBluetoothHeadsetConnected() ->
                tvStatusAudio.text = "🎧 Reproduzindo via Bluetooth..."
            audioHelper.isWiredHeadsetConnected() ->
                tvStatusAudio.text = "🎧 Reproduzindo via fone com fio..."
            audioHelper.isSpeakerAvailable() ->
                tvStatusAudio.text = "📢 Reproduzindo via alto-falante..."
            else -> {
                Toast.makeText(this, "Sem saída de áudio disponível!", Toast.LENGTH_LONG).show()
                return
            }
        }
        if (ttsReady) tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "doma_tts")
    }
}
