package com.mac.ecomadminphp.FCM

import android.content.Context
import android.widget.Toast
import com.mac.ecomadminphp.FCM.API.ApiUtilities
import com.mac.ecomadminphp.FCM.Model.PushNotification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SendNotification {
    fun Send(pushNotification: PushNotification?, context: Context?) {
        ApiUtilities.client.sendNotification(pushNotification)
            ?.enqueue(object : Callback<PushNotification?> {
                override fun onResponse(
                    call: Call<PushNotification?>,
                    response: Response<PushNotification?>
                ) {
                    if (response.isSuccessful()) {
//                        Toast.makeText(context, "successful", Toast.LENGTH_SHORT).show();
                    } else {
    //                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                    }
                }

                override fun onFailure(call: Call<PushNotification?>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}