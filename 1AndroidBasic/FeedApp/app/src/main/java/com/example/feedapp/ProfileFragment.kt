package com.example.feedapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // SharedPreferences 객체 얻기
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // 저장된 데이터 읽기 (기본값 제공)
        val name = sharedPref.getString("name", "") // 기본값 설정
        val introduce = sharedPref.getString("introduce", "")
        val image = sharedPref.getString("image", "")

        val textViewName = view.findViewById<TextView>(R.id.textViewName)
        val textViewIntroduce = view.findViewById<TextView>(R.id.textViewIntroduce)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        textViewName.text = name
        textViewIntroduce.text = introduce
        // Glide로 content:// URI 이미지 로드
        val imageUri = Uri.parse(image)
        Glide.with(requireContext())
            .load(imageUri)  // content:// URI로 이미지 로드
            .into(imageView)  // 이미지 뷰에 로드

        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val editor = sharedPref.edit()

            // SharedPreferences 전체 데이터 삭제
            editor.clear()
            editor.apply()

            // 프래그먼트 내에서 액티비티를 시작할 때
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish() // 현재 액티비티 종료
        }

        return view
    }
}