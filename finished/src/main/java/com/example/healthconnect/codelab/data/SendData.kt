package com.example.healthconnect.codelab.data

//import org.eclipse.paho.client.mqttv3.MqttClient
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions
//import org.eclipse.paho.client.mqttv3.MqttMessage
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import android.util.Log
import com.example.healthconnect.codelab.presentation.component.CaloriesBurnedSerializable
import com.example.healthconnect.codelab.presentation.component.ExerciseSessionSerializable
import com.example.healthconnect.codelab.presentation.component.OxygenSaturationSerializable
import com.example.healthconnect.codelab.presentation.component.SampleSerializable
import com.example.healthconnect.codelab.presentation.component.StepsSerializable
import com.example.healthconnect.codelab.presentation.component.WeightSerializable

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement


class SendData {
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

//    fun sendDataToMqttBroker() {
//        try {
////            client.connect(connOpts)
//            val topic = "MQTT Examples"
//            val content = "Message from MqttPublishSample"
////            val msg = MqttMessage(content.toByteArray())
////            msg.qos = 2
////            client.publish(topic, msg)
////            client.disconnect()
//            Log.i("LogCOllectData", "${content.toByteArray()}")
//        } catch (me: Exception) {
//            // Log the exception
//        }
//    }
    fun sendSamplesToMqttBroker(samples: List<SampleSerializable>) {
        samples.forEach { sample ->
            val content = Json.encodeToJsonElement(sample)
            Log.i("LogCollectData", "Sample: ${content.toString()}")
        }
    }

    fun sendExerciseSessionToMqttBroker(exerciseSessions: List<ExerciseSessionSerializable>) {
        exerciseSessions.forEach { exerciseSession ->
            val content = Json.encodeToJsonElement(exerciseSession)
            Log.i("LogCollectData", "ExerciseSession: ${content.toString()}")
        }
    }

    fun sendStepsToMqttBroker(steps: List<StepsSerializable>) {
        steps.forEach { step ->
            val content = Json.encodeToJsonElement(step)
            Log.i("LogCollectData", "Step: ${content.toString()}")
        }
    }

    fun sendCaloriesToMqttBroker(steps: List<CaloriesBurnedSerializable>) {
        steps.forEach { step ->
            val content = Json.encodeToJsonElement(step)
            Log.i("LogCollectData", "Step: ${content.toString()}")
        }
    }
    fun sendWeightToMqttBroker(steps: List<WeightSerializable>) {
        steps.forEach { step ->
            val content = Json.encodeToJsonElement(step)
            Log.i("LogCollectData", "Step: ${content.toString()}")
        }
    }
    fun sendOxygenSaturationToMqttBroker(steps: List<OxygenSaturationSerializable>) {
        steps.forEach { step ->
            val content = Json.encodeToJsonElement(step)
            Log.i("LogCollectData", "${content.toString()}")
        }
    }

}