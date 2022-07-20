package com.mac.ecomadminphp.UserArea.Activities.ViewProduct

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.UserArea.Activities.Cart.Cart_Activity
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomadminphp.databinding.ActivitySeeProductBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SeeProduct_Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySeeProductBinding
    private lateinit var pName: String
    private lateinit var pCat: String
    private lateinit var pStock: String
    private lateinit var pPrice: String
    private lateinit var pDisPrice: String
    private lateinit var pDesc: String
    private lateinit var pId: String
    private lateinit var pImage: String
    private lateinit var uid:String
    private val addToCartUrl:String = Constants.baseUrl+"/Cart/addToCart.php"
    private val fetchCart:String = Constants.baseUrl+"/Cart/fetchCart.php"
    private var cartList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySeeProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SetIntent()
        GetUser()
        CheckProductAlreadyInCart();
        ClickOnAddToCartBtn()




    }

    private fun CheckStock() {
        if(pStock.toInt()==0){

            binding.addToCartBtn.setText("Out of stock")
            binding.addToCartBtn.isEnabled=false
            binding.addToCartBtn.setTextColor(resources.getColor(R.color.red))
        }else if(pStock.toInt()<5){
            binding.addToCartBtn.isEnabled=true
            binding.proStockWarning.visibility=VISIBLE

        }else if(pStock.toInt()>0){
            binding.addToCartBtn.isEnabled=true

        }


    }

    private fun GetUser() {
        val checkUser = SharedPref.readFromSharedPref(this, Constants.userPrefName, Constants.defaultPrefValue);
        if (!checkUser.equals("0")) {
            val splitDetails = checkUser?.split(",")
            uid = splitDetails?.get(3) ?: "uid"

        }
    }

    private fun CheckProductAlreadyInCart() {
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchCart,
            Response.Listener { response ->

                try {

                    val jsonObject  = JSONObject(response)
                    val success:String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if(success.equals("1")){


                            for (item in 0 until jsonArray.length()){

                                val jsonObject: JSONObject = jsonArray.getJSONObject(item)

                                val id:String =jsonObject.getString("id")
                                val pId:String =jsonObject.getString("pId")
                                val quantity:String =jsonObject.getString("quantity")

                                cartList.add(pId)

                            }


                        var item:Int=0

                        while(item<cartList.size){
                            if(cartList.get(item).equals(pId)){
                                binding.addToCartBtn.setText("Go to cart")
                                item=cartList.size
                            }else{
                                binding.addToCartBtn.setText("Add to cart")
                                item++
                            }
                        }

                        if(cartList.isEmpty()){
                            binding.addToCartBtn.setText("Add to cart")
                        }

                        CheckStock();



                    }

                }catch (e: JSONException){
                    e.printStackTrace();
                }



            },
            Response.ErrorListener { error ->
                Toast.makeText(this,error.message, Toast.LENGTH_SHORT).show()



            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["cartId"] = "cart"+uid
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    private fun SetIntent() {
        pId = intent.getStringExtra("id").toString()
        pName = intent.getStringExtra("pName").toString()
        pCat = intent.getStringExtra("pCat").toString()
        pStock = intent.getStringExtra("pStock").toString()
        pPrice = intent.getStringExtra("pPrice").toString()
        pDisPrice = intent.getStringExtra("pDisPrice").toString()
        pDesc = intent.getStringExtra("pDesc").toString()
        pImage = intent.getStringExtra("pImage").toString()

        setProducts()


    }

    private fun setProducts() {
        try {
            binding.proName.setText(pName)
            binding.proPrice.setText("₹"+pPrice)
            binding.proDiscountPrice.setText("₹"+pDisPrice)
            val originalPrice:Double = pPrice.toDouble()
            val sellingPrice:Double = pDisPrice.toDouble()
            val difference:Double = originalPrice-sellingPrice
            val percentage = ((difference/originalPrice)*100).toInt()
            binding.proDiscount.setText(percentage.toString()+"% off")
            binding.proPrice.setPaintFlags(binding.proPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            binding.proDesc.setText(pDesc)
            Picasso.get().load(Constants.baseUrl+"/Products/ProductImages/"+pImage).into(binding.proImage)
        } catch (e: Exception) {
        }
    }


    private fun ClickOnAddToCartBtn() {
        binding.addToCartBtn.setOnClickListener {

            if(binding.addToCartBtn.text.equals("Add to cart")){
                val dialog = ProgressDialog.progressDialog(this,"Adding to cart")
                dialog.show()
                AddToCart(dialog)
            }else{
                startActivity(Intent(this,Cart_Activity::class.java))
                finish()
            }


        }


    }

    private fun AddToCart(dialog: Dialog) {

        val request: StringRequest = object : StringRequest(
            Method.POST, addToCartUrl,
            Response.Listener { response ->

                if(response.equals("Added to cart")){
                    dialog.dismiss()
                    CheckProductAlreadyInCart();
                    Toast.makeText(this,response, Toast.LENGTH_SHORT).show()
                }else{
                    dialog.dismiss()
                    Toast.makeText(this,response, Toast.LENGTH_SHORT).show()
                }



            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                Toast.makeText(this,error.message, Toast.LENGTH_SHORT).show()



            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["uid"] = uid
                params["pId"] = pId
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    override fun onResume() {
        super.onResume()
        cartList.clear()
        CheckProductAlreadyInCart()
    }

}