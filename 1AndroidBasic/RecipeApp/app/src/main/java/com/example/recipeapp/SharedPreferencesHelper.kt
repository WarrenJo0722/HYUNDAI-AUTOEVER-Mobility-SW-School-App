package com.example.recipeapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("recipe_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 레시피 리스트를 쉐어드프리퍼런스에 저장
    fun saveRecipeList(recipeList: List<Recipe>) {
        val json = gson.toJson(recipeList)
        sharedPreferences.edit().putString("recipe_list", json).apply()
    }

    // 쉐어드프리퍼런스에서 레시피 리스트를 불러옴
    fun getRecipeList(): List<Recipe> {
        val json = sharedPreferences.getString("recipe_list", null)
        val type = object : TypeToken<List<Recipe>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
