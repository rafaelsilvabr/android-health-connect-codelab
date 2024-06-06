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


class SendData {

    fun getSocketFactory(context: Context): SSLSocketFactory {
        val assetManager = context.assets
        val caInput = BufferedInputStream(context.resources.openRawResource(R.raw.aws_root_ca))
        val caCert = CertificateFactory.getInstance("X.509").generateCertificate(caInput)
        caInput.close()
//        val caCert = CertificateFactory.getInstance("X.509")
//        .generateCertificate(context.resources.openRawResource(R.raw.aws_root_ca))

        val caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("ca-crt", caCert)
        }

        val caTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(caKeyStore)
        }

//        val cert = CertificateFactory.getInstance("X.509")
//            .generateCertificate(context.resources.openRawResource(R.raw.client_cert))

        val certInput = BufferedInputStream(context.resources.openRawResource(R.raw.client_cert))
        val cert = CertificateFactory.getInstance("X.509").generateCertificate(certInput)
        certInput.close()

//        val key = CertificateFactory.getInstance("X.509")
//            .generateCertificate(context.resources.openRawResource(R.raw.client_key_private))

        val keyInput = BufferedInputStream(context.resources.openRawResource(R.raw.client_key_private))
        val key = CertificateFactory.getInstance("X.509").generateCertificate(keyInput)
        keyInput.close()

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("crt", cert)
            setCertificateEntry("key", key)
        }

        val keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore, null)
        }

        val sslContext = SSLContext.getInstance("SSL").apply {
            init(keyManager.keyManagers, caTrustManager.trustManagers, null)
        }

        return sslContext.socketFactory
    }

    private fun sendMqttMessage(context: Context){
        val broker = "<your-broker-url>"
        val clientId = MqttClient.generateClientId()
        val persistence = MemoryPersistence()

        try {
            val client = MqttClient(broker, clientId, persistence)
            val connOpts = MqttConnectOptions()
            connOpts.isCleanSession = true
            connOpts.socketFactory = getSocketFactory(
                context
            )
            print("Connecting to broker: $broker")
            client.connect(connOpts)
            print("Connected")

            val topic = "<your-topic>"
            val content = "<your-message>"

            print("Publishing message: $content")
            val message = MqttMessage(content.toByteArray())
            message.qos = 2
            client.publish(topic, message)
            print("Message published")

            client.disconnect()
            print("Disconnected")
        } catch (me: MqttException) {
            print("reason " + me.reasonCode)
            print("msg " + me.message)
            print("loc " + me.localizedMessage)
            print("cause " + me.cause)
            print("excep " + me)
            me.printStackTrace()
        }
    }

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
            makeNetworkCall(content.toString())
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