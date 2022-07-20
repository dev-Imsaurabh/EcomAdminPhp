package com.mac.ecommerceuserapp.kotlin.Api

import com.google.gson.GsonBuilder
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.Constants.Companion.razorPayBaseUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClientInstance {

    private var retrofit: Retrofit?=null
    private const val BaseUrl = razorPayBaseUrl
    val retrofitInstance :Retrofit?
    get(){
        if(retrofit==null){
            val gson = GsonBuilder().setLenient()
            retrofit=Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson.create()))
                    .build()
        }
        return retrofit;
    }
}