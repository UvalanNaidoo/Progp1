package com.example.moneymappart1

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymappart1.databinding.ActivityViewPhotoBinding

class ViewPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUriString = intent.getStringExtra("photoUri")
        if (!photoUriString.isNullOrEmpty()) {
            val uri = Uri.parse(photoUriString)
            binding.ivFullPhoto.setImageURI(uri)
        }
    }
}
