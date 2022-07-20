package com.mac.ecomadminphp.ClientArea

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mac.ecomadminphp.ClientArea.AddProduct.AddProduct_Activity
import com.mac.ecomadminphp.ClientArea.EditProducts.EditProducts_Activity
import com.mac.ecomadminphp.ClientArea.ManageOrders.Manage_OrdersActivity
import com.mac.ecomadminphp.ClientArea.ProductAvailibility.Add_PinCode
import com.mac.ecomadminphp.ClientArea.categories.Add_Categories_Activity
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.databinding.ActivityClientAreaBinding

class ClientAreaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientAreaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityClientAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Client Area"

        binding.addCategory.setOnClickListener { startActivity(Intent(this,Add_Categories_Activity::class.java)) }
        binding.addProducts.setOnClickListener { startActivity(Intent(this,AddProduct_Activity::class.java)) }
        binding.editProducts.setOnClickListener { startActivity(Intent(this,EditProducts_Activity::class.java)) }
        binding.managePin.setOnClickListener { startActivity(Intent(this,Add_PinCode::class.java)) }
        binding.manageOrder.setOnClickListener { startActivity(Intent(this,Manage_OrdersActivity::class.java)) }
    }
}