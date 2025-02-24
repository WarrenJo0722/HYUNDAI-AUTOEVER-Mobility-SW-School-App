package com.example.feedapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FeedFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView  // recyclerView를 전역으로 선언
    private lateinit var adapter: FeedAdapter

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    // Activity Result Launcher 생성
    private val uploadLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                Toast.makeText(requireContext(), "피드가 등록되었습니다.", Toast.LENGTH_SHORT).show()
                loadFeedList()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        // SharedPreferencesHelper 인스턴스 초기화
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())

        // 플로팅 액션 버튼
        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(requireContext(), UploadActivity::class.java)
            uploadLauncher.launch(intent)
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true  // 마지막 아이템을 위에 표시
        layoutManager.stackFromEnd = true    // 스크롤을 맨 아래에서 시작하도록 설정
        recyclerView.layoutManager = layoutManager

        // 피드 목록 불러오기
        loadFeedList()

        return view
    }

    private fun loadFeedList() {
        val feedList = sharedPreferencesHelper.getFeedList()

        // RecyclerView 어댑터 설정
        adapter = FeedAdapter(feedList)
        recyclerView.adapter = adapter
    }
}

class FeedAdapter(private val feedList: List<Feed>) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewUser = itemView.findViewById<TextView>(R.id.textViewUser)
        val imageViewUser = itemView.findViewById<ImageView>(R.id.imageViewUser)
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val imageViewLike = itemView.findViewById<ImageView>(R.id.imageViewLike)
        val textViewLike = itemView.findViewById<TextView>(R.id.textViewLike)
        val textViewContent = itemView.findViewById<TextView>(R.id.textViewContent)
    }

    // 아이템 레이아웃을 XML에서 객체로 변환하여 ViewHolder에 담아 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        return FeedViewHolder(view)
    }

    // 현재 position에 해당하는 데이터를 ViewHolder에 바인딩 (데이터를 UI에 적용)
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed = feedList[position]
        holder.textViewUser.text = feed.userName
        val imageUriUser = Uri.parse(feed.userImageUri)
        Glide.with(holder.itemView.context)
            .load(imageUriUser)  // content:// URI로 이미지 로드
            .into(holder.imageViewUser)
        val imageUri = Uri.parse(feed.imageUri)
        Glide.with(holder.itemView.context)
            .load(imageUri)  // content:// URI로 이미지 로드
            .into(holder.imageView)
        holder.imageViewLike.setOnClickListener {
            holder.imageViewLike.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_heart_on))
            holder.textViewLike.text = "1"
        }
        holder.textViewLike.text = feed.likes.toString()
        holder.textViewContent.text = feed.content
    }

    // 전체 아이템 개수를 반환 (RecyclerView가 몇 개의 아이템을 표시할지 결정)
    override fun getItemCount(): Int {
        return feedList.size
    }
}