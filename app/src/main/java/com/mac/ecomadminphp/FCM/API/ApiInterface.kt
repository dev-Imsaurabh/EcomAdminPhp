package com.mac.ecomadminphp.FCM.API
import com.mac.ecomadminphp.FCM.Model.Constant.CONTENT_TYPE
import com.mac.ecomadminphp.FCM.Model.Constant.SERVER_KEY
import com.mac.ecomadminphp.FCM.Model.PushNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    fun sendNotification(@Body notification: PushNotification?): Call<PushNotification?>?
}