package com.mac.ecomadminphp.UserArea.Activities.ViewProduct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.UserArea.ModelAndAdapters.ChildProductAdapter
import com.mac.ecomadminphp.UserArea.ModelAndAdapters.ChildeProductData
import com.mac.ecomadminphp.UserArea.ModelAndAdapters.Product_Model
import com.mac.ecomadminphp.UserArea.ModelAndAdapters.SeeMoreAdapter
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.ActivitySeeMoreBinding
import com.mac.ecomadminphp.databinding.MainProductLayoutBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class SeeMore_Activity : AppCompatActivity() {
    private lateinit var binding:ActivitySeeMoreBinding
    private lateinit var category:String
    val getProductUrl = Constants.baseUrl + "/Products/getProducts.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySeeMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        category=intent.getStringExtra("category").toString()
        title=category
        setRecyclerView()


    }




    private fun setRecyclerView() {
        getProductData(category)

    }


    private fun getProductData(category: String) {


        val request: StringRequest = object:
            StringRequest(Request.Method.POST, getProductUrl, { response ->


                val productList = mutableListOf<ChildeProductData>()
                val jsonObject = JSONObject(response)
                val success: String = jsonObject.getString("success")
                val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                productList.clear()
                if (success.equals("1")) {

                    for (item in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(item)
                        val id: String = jsonObject.getString("id")
                        val pName: String = jsonObject.getString("pName")
                        val pCat: String = jsonObject.getString("pCat")
                        val pStock: String = jsonObject.getString("pStock")
                        val pPrice: String = jsonObject.getString("pPrice")
                        val pDisPrice: String = jsonObject.getString("pDisPrice")
                        val pRatings: String = jsonObject.getString("pRatings")
                        val pDesc: String = jsonObject.getString("pDesc")
                        val pImage: String = jsonObject.getString("pImage")
                        val pTags: String = jsonObject.getString("pTags")
                        val pDelivery: String = jsonObject.getString("pDelivery")
                        val prodcutModel = ChildeProductData(id, pName,pCat,pStock,pPrice,pDisPrice,pRatings,pDesc,pImage,pTags,pDelivery)

                        productList.add(0, prodcutModel)

                    }
                    Collections.shuffle(productList)

                    binding.seeMoreRecycler.setHasFixedSize(true)
                    binding.seeMoreRecycler.layoutManager = GridLayoutManager(this,2,
                        GridLayoutManager.VERTICAL,false)
                    val adapter = SeeMoreAdapter(this,productList)
                    adapter.notifyDataSetChanged()
                    binding.seeMoreRecycler.adapter =adapter



                } else {

                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()


                }

            }, { error ->

                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

            }){

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["category"] = category
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
}