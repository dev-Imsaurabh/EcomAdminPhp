package com.mac.ecomadminphp.UserArea.Activities.Additional

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomadminphp.databinding.ActivityMyAccountBinding

class My_Account_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityMyAccountBinding
    private lateinit var uid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "My Accounts"

        checkUser()
    }


    private fun checkUser() {
        val checkUser =
            SharedPref.readFromSharedPref(this, Constants.userPrefName, Constants.defaultPrefValue);
        if (!checkUser.equals("0")) {
            val splitDetails = checkUser?.split(",")
            binding.txtUsername.setText(splitDetails?.get(0) ?: "username")
            binding.txtEmail.setText(splitDetails?.get(1) ?: "email")
            uid = splitDetails?.get(3) ?: "nothing"

        }

    }
}