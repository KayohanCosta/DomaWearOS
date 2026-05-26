package com.doma.wearos.presentation

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.content.pm.PackageManager

/**
 * AudioHelper — Detecta e enumera saídas de áudio disponíveis no Wear OS.
 *
 * Implementa a lógica principal de verificação de saída de áudio
 * exigida pelo trabalho prático da disciplina DGT2816.
 */
class AudioHelper(private val context: Context) {

    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    /**
     * Verifica se um tipo específico de saída de áudio está disponível.
     *
     * Primeiro verifica FEATURE_AUDIO_OUTPUT. Se não existir, retorna false.
     * Caso contrário, enumera os dispositivos de saída e busca o tipo.
     *
     * @param type Tipo de dispositivo de áudio (AudioDeviceInfo.TYPE_*)
     * @return true se o dispositivo estiver disponível e conectado
     */
    fun audioOutputAvailable(type: Int): Boolean {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
            return false
        }
        return audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any { it.type == type }
    }

    /** Alto-falante integrado disponível */
    fun isSpeakerAvailable(): Boolean =
        audioOutputAvailable(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)

    /** Fone de ouvido Bluetooth A2DP conectado */
    fun isBluetoothHeadsetConnected(): Boolean =
        audioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)

    /** Fone com fio conectado */
    fun isWiredHeadsetConnected(): Boolean =
        audioOutputAvailable(AudioDeviceInfo.TYPE_WIRED_HEADPHONES) ||
                audioOutputAvailable(AudioDeviceInfo.TYPE_WIRED_HEADSET)

    /** Resumo diagnóstico de todas as saídas disponíveis */
    fun getAudioOutputSummary(): String {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        if (devices.isEmpty()) return "Nenhuma saída de áudio detectada"
        return devices.map { device ->
            when (device.type) {
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER  -> "Alto-falante integrado"
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP   -> "Bluetooth A2DP"
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "Fone com fio"
                AudioDeviceInfo.TYPE_WIRED_HEADSET    -> "Headset com fio"
                AudioDeviceInfo.TYPE_BLE_HEADSET      -> "Fone BLE"
                else -> "Tipo ${device.type}"
            }
        }.joinToString(", ")
    }
}
