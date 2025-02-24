package com.example.todoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class TodoModelAdapter(private val context: Context, private val todos: List<Todo>) : BaseAdapter() {
    override fun getCount(): Int {
        return todos.size
    }

    override fun getItem(position: Int): Todo {
        return todos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false)

        // ViewHolder가 이미 있으면 재사용
        val holder = view.tag as? ViewHolder ?: ViewHolder(view).also { view.tag = it }

        val todo = getItem(position)  // 이제 getItem으로 바로 Todo 객체 가져옴
        holder.todoText.text = todo.text
        holder.checkBox.isChecked = todo.isChecked

        // 체크박스 상태 변경 시 바로 업데이트
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            todo.isChecked = isChecked
            TodoPreferences(context).saveTodos(todos)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val todoText: TextView = view.findViewById(R.id.todoText)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
    }
}