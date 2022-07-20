package com.mac.ecomadminphp.Utils

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PrefConfig {
    private const val LIST_KEY = "list_key"
    private const val DISCOVER_LIST = "discover_list"
    fun writeListInPref(context: Context?, list: java.util.ArrayList<String>) {
        val gson = Gson()
        val jsonString = gson.toJson(list)
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString(LIST_KEY, jsonString)
        editor.apply()
    }

    fun readListFromPref(context: Context?): java.util.ArrayList<String> {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val emptyList = Gson().toJson(ArrayList<String>())
        val jsonString = pref.getString(LIST_KEY, emptyList)
        val gson = Gson()
        val type = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson(jsonString, type)
    }

    fun writeListInPrefForDiscover(context: Context?, list: java.util.ArrayList<String>) {
        val gson = Gson()
        val jsonString = gson.toJson(list)
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString(DISCOVER_LIST, jsonString)
        editor.apply()
    }

    fun readListFromPrefForDiscover(context: Context?): java.util.ArrayList<String> {
        val emptyList = Gson().toJson(ArrayList<String>())
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val jsonString = pref.getString(DISCOVER_LIST,emptyList )
        val gson = Gson()
        val type = object : TypeToken<ArrayList<String?>?>() {}.type

        return gson.fromJson(jsonString,type)
}
}
