package com.example.todoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast

class TodoAdapter(
    context: Context,
    private val todos: MutableList<String>
) : ArrayAdapter<String>(context, R.layout.item_todo, todos) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 각 항목에 대한 뷰를 설정하는 과정
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false)

        val todoTextView = view.findViewById<TextView>(R.id.todoText)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)

        // 각 할 일 항목을 텍스트로 설정
        todoTextView.text = todos[position]

        // 체크박스 상태 초기화 (선택되지 않은 상태)
        checkBox.isChecked = false

        // 체크박스를 클릭하면 상태를 토글하도록 설정
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            // 체크박스 상태 변경 시 처리할 코드 (예: 상태 저장)
            if (isChecked) {
                Toast.makeText(context, "체크됨: ${todos[position]}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "체크 해제됨: ${todos[position]}", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}