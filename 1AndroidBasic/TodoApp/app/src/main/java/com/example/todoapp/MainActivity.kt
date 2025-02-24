package com.example.todoapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var todoPreferences: TodoPreferences
    private lateinit var todos: MutableList<Todo>
    private lateinit var adapter: TodoModelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 할 일 목록
        /*val todos = mutableListOf(
            "아침 운동하기",
            "저녁 약속",
            "치과 가기"
        )*/

        // 리스트뷰 컴포넌트 선언 및 초기화
        val listView = findViewById<ListView>(R.id.listView)

        // 리스트뷰 어댑터 셋팅
//        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todos)
//        listView.adapter = adapter

        // 커스텀 어댑터 설정
//        val adapter = TodoAdapter(this, todos)
//        listView.adapter = adapter

        // TodoPreferences 초기화
        todoPreferences = TodoPreferences(this)

        // SharedPreferences에서 할 일 목록 불러오기
        todos = todoPreferences.loadTodos().toMutableList()

        // TodoAdapter 설정
        adapter = TodoModelAdapter(this, todos)
        listView.adapter = adapter

        // 에디트텍스트 선언 및 초기화
        val editText = findViewById<EditText>(R.id.editText)

        // 버튼 선언 및 초기화
        val button = findViewById<Button>(R.id.button)
        // 버튼 동작 설정
        button.setOnClickListener {
            val todoText = editText.text.toString()

            if (todoText.isNotEmpty()) {
                // 새로운 할 일 추가
                val newTodo = Todo(
                    text = todoText,       // 입력한 텍스트
                    isChecked = false      // 새로 추가된 할 일은 기본적으로 체크되지 않음
                )

                todos.add(newTodo)  // 할 일 목록에 추가
                todoPreferences.saveTodos(todos)  // SharedPreferences에 저장

                // 리스트 갱신
                adapter.notifyDataSetChanged()

                // 에디트 텍스트 비우기
                editText.setText("")
            } else {
                Toast.makeText(this, "할 일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            /*// 빈 문자열 추가 방지
            if (editText.text.toString() == "") {
                // 토스트 메시지 띄우기
                Toast.makeText(applicationContext, "할 일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            todos.add(editText.text.toString())
            adapter.notifyDataSetChanged()
            editText.setText("")*/
        }
    }
}