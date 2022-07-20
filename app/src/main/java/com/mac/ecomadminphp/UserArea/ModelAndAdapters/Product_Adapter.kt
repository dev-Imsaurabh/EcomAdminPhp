package com.mac.ecomadminphp.UserArea.ModelAndAdapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.UserArea.Activities.ViewProduct.SeeMore_Activity
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.MainProductLayoutBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class Product_Adapter(val context:Context, val productList:List<Product_Model>) :RecyclerView.Adapter<Product_Adapter.myViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding = MainProductLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)


    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val productPos = productList[position]
        holder.setData(productPos)
    }

    override fun getItemCount(): Int {

        return productList.size
    }


    inner class myViewHolder(val binding: MainProductLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setData(productList: Product_Model){

            binding.productCategoryTitle.text = productList.category
            binding.seeMoreBtn.setOnClickListener {
                val intent = Intent(context,SeeMore_Activity::class.java)
                intent.putExtra("category",productList.category)
                context.startActivity(intent)
            }
            setRecyclerView(productList,binding)


        }


    }

    private fun setRecyclerView(productList: Product_Model, binding: MainProductLayoutBinding) {
        getProductData(productList.category,binding)

    }


    private fun getProductData(category: String, binding: MainProductLayoutBinding) {
        val getProductUrl = Constants.baseUrl + "/Products/getProducts.php"


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

                binding.mainProductRecycler.setHasFixedSize(true)
                binding.mainProductRecycler.layoutManager = GridLayoutManager(context,2,
                    GridLayoutManager.VERTICAL,false)
                val adapter = ChildProductAdapter(context,productList)
                adapter.notifyDataSetChanged()
                binding.mainProductRecycler.adapter =adapter



            } else {

                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()


            }

        }, { error ->

            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()

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

        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(request)



    }

}