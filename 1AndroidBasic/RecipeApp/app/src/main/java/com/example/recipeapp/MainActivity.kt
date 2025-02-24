package com.example.recipeapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val isEditing = result.data?.getBooleanExtra("isEditing", false) ?: false
            val message = if (isEditing) "레시피가 수정되었습니다." else "레시피가 등록되었습니다."
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            loadRecipeList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            // 레시피 등록화면으로 이동
            val intent = Intent(this, RecipeActivity::class.java)
            resultLauncher.launch(intent)
        }

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        recipeRecyclerView = findViewById(R.id.recyclerView)
        recipeRecyclerView.layoutManager = LinearLayoutManager(this)

        // 레시피 목록 불러오기
        loadRecipeList()
    }

    private fun loadRecipeList() {
        val recipeList = sharedPreferencesHelper.getRecipeList()

        // RecyclerView 어댑터 설정
        recipeAdapter = RecipeAdapter(recipeList) { recipe ->
            // 아이템 클릭 시 수정 화면으로 이동
            val intent = Intent(this, RecipeActivity::class.java)
            intent.putExtra("recipe", recipe)  // 수정할 레시피 전달
            resultLauncher.launch(intent)
        }
        recipeRecyclerView.adapter = recipeAdapter
    }
}

class RecipeAdapter(
    private val recipeList: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipeList.size

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeName: TextView = itemView.findViewById(R.id.recipeName)
        private val recipeDescription: TextView = itemView.findViewById(R.id.recipeDescription)
        private val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)

        fun bind(recipe: Recipe) {
            recipeName.text = recipe.name
            recipeDescription.text = recipe.description

            // Glide로 content:// URI 이미지 로드
            val imageUri = Uri.parse(recipe.imageUri)
            Glide.with(itemView.context)
                .load(imageUri)  // content:// URI로 이미지 로드
                .into(recipeImage)  // 이미지 뷰에 로드

            // 아이템 클릭 시 수정 화면으로 이동
            itemView.setOnClickListener {
                onItemClick(recipe) // 클릭 리스너 실행
            }
        }
    }
}
