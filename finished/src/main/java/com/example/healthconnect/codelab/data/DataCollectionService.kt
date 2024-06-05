package com.example.healthconnect.codelab.data

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.healthconnect.codelab.presentation.component.FormattedChange
import kotlinx.coroutines.*
import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.changes.UpsertionChange
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.format.DateTimeFormatter
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.presentation.TAG
import com.example.healthconnect.codelab.presentation.component.CaloriesBurnedSerializable
import com.example.healthconnect.codelab.presentation.component.FormattedChangeRow
import com.example.healthconnect.codelab.presentation.component.OxygenSaturationSerializable
import com.example.healthconnect.codelab.presentation.component.SampleSerializable
import com.example.healthconnect.codelab.presentation.component.StepsSerializable
import com.example.healthconnect.codelab.presentation.component.WeightSerializable

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.ContentValues
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthconnect.codelab.presentation.MainActivity
//import DifferentialChangesViewModel
import com.example.healthconnect.codelab.presentation.screen.changes.DifferentialChangesViewModel
import android.content.Context
import android.os.BatteryManager
import org.json.JSONObject


@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {
    private val formatter = DateTimeFormatter.ISO_INSTANT
//
//    override val descriptor: SerialDescriptor =
//        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(formatter.format(value))
    }
    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}

@Serializable
data class SampleSerializable(
    val beatsPerMinute: Float,
    @Serializable(with = InstantSerializer::class) val time: Instant
)
@Serializable
data class ExerciseSessionSerializable(
    val name: String,
    @Serializable(with = InstantSerializer::class) val startTime: Instant,
    @Serializable(with = InstantSerializer::class) val endTime: Instant
)

@Serializable
data class StepsSerializable(
    val steps: Long,
    @Serializable(with = InstantSerializer::class) val startTime: Instant,
    @Serializable(with = InstantSerializer::class) val endTime: Instant
)

@Serializable
data class CaloriesBurnedSerializable(
    val calories: Float,
    @Serializable(with = InstantSerializer::class) val startTime: Instant,
    @Serializable(with = InstantSerializer::class) val endTime: Instant
)

@Serializable
data class WeightSerializable(
    val weight: Float,
    @Serializable(with = InstantSerializer::class) val time: Instant
)

@Serializable
data class OxygenSaturationSerializable(
    val oxygenSaturation: Float,
    @Serializable(with = InstantSerializer::class) val time: Instant
)

class DataCollectionService : Service() {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var healthConnectManager : HealthConnectManager
    val TAG = "DataCollectionService"
    private val differentialChangesViewModel: DifferentialChangesViewModel by lazy {
        DifferentialChangesViewModel(healthConnectManager)
    }
    var changes = mutableListOf<Change>()


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        healthConnectManager = HealthConnectManager(this)
        changes = differentialChangesViewModel.changes
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        differentialChangesViewModel.enableOrDisableChanges(true)
        var changesToken: MutableState<String?> = mutableStateOf(null)

        val notification = NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)


        serviceScope.launch {
            Log.i(TAG, "Starting data collection service")
            while (true) {
                //Log.i(TAG, "Checking for changes")
                serviceScope.launch {
                    differentialChangesViewModel.getChanges()
                    changes.forEach { change ->
                        //Log.i(TAG, "Processing change: $change")
                        if(change is UpsertionChange){
                            processChange(change)
                        }
                    }
                }

                //Log.i(ContentValues.TAG, "Getting changes")

                delay(5000)
                val batteryPercentage = getBatteryPercentage(this@DataCollectionService)
                val sendData = SendData()
                sendData.sendBatteryDataToMqttBroker(batteryPercentage)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun processChange(change: UpsertionChange) {
        Log.i(TAG, "Processing change: $change")
        when(change.record){
            is StepsRecord -> {
                val steps = change.record as StepsRecord
                val stepsData = StepsSerializable(
                    steps = steps.count,
                    startTime = steps.startTime,
                    endTime = steps.endTime
                )
                val sendData = SendData()
                sendData.sendStepsToMqttBroker(listOf(stepsData))
            }
            is SpeedRecord -> {
                val speed = change.record as SpeedRecord
            }
            is HeartRateRecord -> {
                val hr = change.record as HeartRateRecord
                val sampleList = hr.samples.map { sample ->
                    SampleSerializable(
                        beatsPerMinute = sample.beatsPerMinute.toFloat(),
                        time = sample.time
                    )
                }
//            sampleList.forEach { sample ->
//                Log.i("Log Vars", "Beats per minute: ${sample.beatsPerMinute}, Time: ${sample.time}")
//            }
//            val jsonSamples = Json.encodeToJsonElement(sampleList)
//            Log.i("Log Vars", jsonSamples.toString())
                val sendData = SendData()
                sendData.sendSamplesToMqttBroker(sampleList)
            }
            is TotalCaloriesBurnedRecord -> {
                val calories = change.record as TotalCaloriesBurnedRecord
                val caloriesData = CaloriesBurnedSerializable(
                    calories = calories.energy.inKilocalories.toFloat(),
                    startTime = calories.startTime,
                    endTime = calories.endTime
                )
                val sendData = SendData()
                sendData.sendCaloriesToMqttBroker(listOf(caloriesData))
            }
            is SleepSessionRecord -> {
                val sleep = change.record as SleepSessionRecord
            }
            is WeightRecord -> {
                val weight = change.record as WeightRecord
                val weightData = WeightSerializable(
                    weight = weight.weight.inKilograms.toFloat(),
                    time = weight.time
                )
                val sendData = SendData()
                sendData.sendWeightToMqttBroker(listOf(weightData))
            }
            is DistanceRecord -> {
                val distance = change.record as DistanceRecord
            }
            is OxygenSaturationRecord -> {
                val oxygen = change.record as OxygenSaturationRecord
                val oxygenData = OxygenSaturationSerializable(
                    oxygenSaturation = oxygen.percentage.value.toFloat(),
                    time = oxygen.time
                )
                val sendData = SendData()
                sendData.sendOxygenSaturationToMqttBroker(listOf(oxygenData))
            }
            else -> {
                Log.w(TAG, "Unknown record type: ${change.record}")
            }
        }

    }

}

fun getBatteryPercentage(context: Context): Int {
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val batteryPercentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    return batteryPercentage
}