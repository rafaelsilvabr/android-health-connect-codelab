package com.example.healthconnect.codelab.presentation.component

//import org.eclipse.paho.client.mqttv3.MqttClient
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions
//import org.eclipse.paho.client.mqttv3.MqttMessage
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import android.util.Log


class CollectData {
//    private val broker = "tcp://mqtt.eclipse.org:1883"
//    private val clientId = MqttClient.generateClientId()
//    private val persistence = MemoryPersistence()
//    private val client = MqttClient(broker, clientId, persistence)
//    private val connOpts = MqttConnectOptions()

//    init {
//        connOpts.isCleanSession = true
//    }

    fun collectData(sample: SampleSerializable) {
        // Aqui você pode adicionar a lógica para coletar os dados do sample
        // Por exemplo, você pode adicionar os samples a uma lista
    }

    fun sendDataToMqttBroker() {
        try {
//            client.connect(connOpts)
            val topic = "MQTT Examples"
            val content = "Message from MqttPublishSample"
//            val msg = MqttMessage(content.toByteArray())
//            msg.qos = 2
//            client.publish(topic, msg)
//            client.disconnect()
            Log.i("LogCOllectData", "${content.toByteArray()}")
        } catch (me: Exception) {
            // Log the exception
        }
    }
}