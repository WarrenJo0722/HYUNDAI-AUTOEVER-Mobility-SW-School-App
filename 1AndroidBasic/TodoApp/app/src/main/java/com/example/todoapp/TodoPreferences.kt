package com.example.todoapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TodoPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("todo_preferences", Context.MODE_PRIVATE)

    private val gson = Gson()

    // 할 일 목록을 SharedPreferences에 저장
    fun saveTodos(todos: List<Todo>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(todos)  // List<Todo>를 JSON 문자열로 변환
        editor.putString("todos", json)
        editor.apply()  // 비동기 저장
    }

    // SharedPreferences에서 할 일 목록을 불러오기
    fun loadTodos(): List<Todo> {
        val json = sharedPreferences.getString("todos", "[]")
        val type = object : TypeToken<List<Todo>>() {}.type
        return gson.fromJson(json, type)  // JSON 문자열을 List<Todo>로 변환
    }
}
