package com.example.feedapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("feed_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 레시피 리스트를 쉐어드프리퍼런스에 저장
    fun saveFeedList(feedList: List<Feed>) {
        val json = gson.toJson(feedList)
        sharedPreferences.edit().putString("feed_list", json).apply()
    }

    // 쉐어드프리퍼런스에서 레시피 리스트를 불러옴
    fun getFeedList(): List<Feed> {
        val json = sharedPreferences.getString("feed_list", null)
        val type = object : TypeToken<List<Feed>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
