package com.mac.ecomadminphp.UserArea.Activities.Cart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mac.ecomadminphp.MainActivity
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomadminphp.databinding.ActivityOrderPlacedSuccessfullyBinding

class OrderPlacedSuccessfully_Activity : AppCompatActivity() {
    private lateinit var binding:ActivityOrderPlacedSuccessfullyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderPlacedSuccessfullyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Order placed"

        SharedPref.writeInSharedPref(this,"backOrder","1")

        binding.cbtn.setOnClickListener {
            finish()

        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}