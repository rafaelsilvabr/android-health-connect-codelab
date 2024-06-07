package com.example.healthconnect.codelab.data

import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.UserState
import android.content.Context

class AwsIotHandler(context: Context){
    var mqttManager: AWSIotMqttManager

    init {
        mqttManager = AWSIotMqttManager(
            "<>",
            "<>"
        )
        AWSMobileClient.getInstance().initialize(context, object : Callback<UserStateDetails> {
            override fun onResult(userStateDetails: UserStateDetails) {
                if (userStateDetails.userState == UserState.SIGNED_IN) {
                    // AWSMobileClient is initialized and the user is signed in, you can now call AWSMobileClient.getInstance().getIdentityId()
                    AWSMobileClient.getInstance().getIdentityId()
                }
            }

            override fun onError(e: Exception) {
                // Handle error
            }
        })
    }

    fun connect(){
        AWSMobileClient.getInstance().getIdentityId()
    }
}