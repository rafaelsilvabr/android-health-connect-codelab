package com.example.healthconnect.codelab.data

import android.util.Log
import com.example.healthconnect.codelab.presentation.component.CaloriesBurnedSerializable
import com.example.healthconnect.codelab.presentation.component.ExerciseSessionSerializable
import com.example.healthconnect.codelab.presentation.component.OxygenSaturationSerializable
import com.example.healthconnect.codelab.presentation.component.SampleSerializable
import com.example.healthconnect.codelab.presentation.component.StepsSerializable
import com.example.healthconnect.codelab.presentation.component.WeightSerializable

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.json.JSONObject
import java.time.Instant

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import javax.net.ssl.SSLSocketFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import android.content.Context
import com.example.healthconnect.codelab.R
import java.io.BufferedInputStream
import kotlin.random.Random

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.*

import com.example.healthconnect.codelab.data.AwsIotHandler


class SendData {

    fun sendSamplesToMqttBroker(samples: List<SampleSerializable>) {
        samples.forEach { sample ->
            val content = Json.encodeToJsonElement(sample)
            Log.i("LogCollectData", "${content.toString()}")
            GlobalScope.launch {
                makeNetworkCall(content.toString())
            }
        }
    }

    fun sendExerciseSessionToMqttBroker(exerciseSessions: List<ExerciseSessionSerializable>) {
        exerciseSessions.forEach { exerciseSession ->
            val content = Json.encodeToJsonElement(exerciseSession)
            Log.i("LogCollectData", "${content.toString()}")
            GlobalScope.launch {
                makeNetworkCall(content.toString())
            }
        }
    }

    fun sendStepsToMqttBroker(steps: List<StepsSerializable>) {
        steps.forEach { step ->
            val content = Json.encodeToJsonElement(step)
            Log.i("LogCollectData", "${content.toString()}")
            GlobalScope.launch {
                makeNetworkCall(content.toString())
            }
        }
    }

    fun sendCaloriesToMqttBroker(steps: List<CaloriesBurnedSerializable>) {
        steps.forEach { step ->
            val content = Json.encodeToJsonElement(step)
            Log.i("LogCollectData", "${content.toString()}")
            GlobalScope.launch {
                makeNetworkCall(content.toString())
            }
        }
    }
    fun sendWeightToMqttBroker(steps: List<WeightSerializable>) {
        steps.forEach { step ->
            val content = Json.encodeToJsonElement(step)
            Log.i("LogCollectData", "${content.toString()}")
            GlobalScope.launch {
                makeNetworkCall(content.toString())
            }
        }
    }
    fun sendOxygenSaturationToMqttBroker(steps: List<OxygenSaturationSerializable>) {
        steps.forEach { step ->
            val content = Json.encodeToJsonElement(step)
            Log.i("LogCollectData", "${content.toString()}")
            GlobalScope.launch {
                makeNetworkCall(content.toString())
            }
        }
    }

    fun sendBatteryDataToMqttBroker(context: Context, battery: Int) {
        val collectTime = Instant.now()


        val content = JSONObject()
        content.put("cellphoneBattery", battery)
        content.put("time", collectTime.toString())

        Log.i("LogCollectData", "${content.toString()}")

        //this.sendMqttMessage(context)
        GlobalScope.launch {
            //makeNetworkCall(content.toString())
//            val awsIotHandler = AwsIotHandler(context)
            //awsIotHandler.connect()
        }
    }

    private suspend fun makeNetworkCall(content: String){
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val time = System.currentTimeMillis()       
        val body = "$content".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("<your-endpoint-url>")
            .post(body)
            .addHeader("<your-header>", "<your-header-value>")
            .addHeader("<your-header>", "<your-header-value>")
            .addHeader("Content-Type", "application/json")
            .build()
        val response = client.newCall(request).execute()
        println(response.body)
    }

}