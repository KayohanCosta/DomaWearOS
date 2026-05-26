# 🏢 DomaWearOS — Trabalho Prático DGT2816

> **Disciplina:** DGT2816 — Interação com sensores de smartphones e wearables  
> **Plataforma:** Wear OS (Android Smartwatch) | **Linguagem:** Kotlin  
> **Instituição:** Estácio

---

## 📋 Descrição

App **Wear OS** para a empresa **Doma**, fornecendo assistência sonora a funcionários com necessidades especiais. Usa o áudio como canal principal para leitura de mensagens, notificações e alertas críticos de segurança.

## 🛠️ Tecnologias

| Tech | Versão | Uso |
|------|--------|-----|
| Kotlin | 1.9.22 | Linguagem |
| Wear OS SDK | API 30+ | Plataforma |
| Text-to-Speech | Android | Síntese PT-BR |
| AudioManager API | Android | Detecção de áudio |
| AudioDeviceCallback | Android | Monitoramento em tempo real |

## 📂 Estrutura

```
app/src/main/
├── AndroidManifest.xml        # BODY_SENSORS, WAKE_LOCK, BLUETOOTH, VIBRATE
└── java/com/doma/wearos/presentation/
    ├── AudioHelper.kt          # audioOutputAvailable() - core do trabalho
    ├── MainActivity.kt         # ListView + AudioDeviceCallback dinâmico
    ├── AudioAssistActivity.kt  # Text-to-Speech PT-BR
    ├── AlertActivity.kt        # Alertas + vibração tátil
    └── AudioAssistService.kt   # Serviço background
```

## 🔑 Funcionalidades Implementadas

### `audioOutputAvailable(type)` — AudioHelper.kt
```kotlin
fun audioOutputAvailable(type: Int): Boolean {
    if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT))
        return false
    return audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                       .any { it.type == type }
}
```

### `AudioDeviceCallback` — Detecção dinâmica em tempo real
```kotlin
audioManager.registerAudioDeviceCallback(object : AudioDeviceCallback() {
    override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) { ... }
    override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) { ... }
}, null)
```

### Intent Bluetooth Settings
```kotlin
val intent = with(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) {
    putExtra("EXTRA_CONNECTION_ONLY", true)
    putExtra("EXTRA_CLOSE_ON_CONNECT", true)
    putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 1)
    this
}
startActivity(intent)
```

## 📄 Documentação

A documentação completa do projeto está em [`docs/documentacao_dgt2816.html`](docs/documentacao_dgt2816.html).  
Abra no navegador e use `Ctrl+P → Salvar como PDF` para gerar o PDF.

## ▶️ Como Rodar

1. Abra no **Android Studio**
2. `Tools > Device Manager > Create Device > Wear OS Small Round > API 30`
3. Clique **▶ Run**

## 📚 Referências

- [Audio output for Wear OS](https://developer.android.com/training/wearables/audio)
- [AudioManager API](https://developer.android.com/reference/android/media/AudioManager)
- [TextToSpeech](https://developer.android.com/reference/android/speech/tts/TextToSpeech)
- Material DGT2816: https://sway.cloud.microsoft/d3rPssMprgwvvxGP

---
*DGT2816 — Interação com sensores de smartphones e wearables | Estácio 2026*
