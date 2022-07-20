package com.mac.ecomadminphp.Fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.UserArea.ModelAndAdapters.Product_Adapter
import com.mac.ecomadminphp.UserArea.ModelAndAdapters.Product_Model
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomadminphp.databinding.FragmentHomeBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class HomeFragment : Fragment() {

    private  var finalList= mutableListOf<String>()
    private val getUrl = Constants.baseUrl+"/Categories/getCategory.php"
    private lateinit var _binding:FragmentHomeBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        GetCategories()


        return binding.root
    }



    private fun GetCategories() {

        val request: StringRequest = StringRequest(Request.Method.POST,getUrl ,{ response->


            val productList = mutableListOf<Product_Model>()
            val jsonObject = JSONObject(response)
            val success:String = jsonObject.getString("success")
            val jsonArray: JSONArray = jsonObject.getJSONArray("data")
            productList.clear()
            finalList.clear()
            if(success.equals("1")){

                for (item in 0 until jsonArray.length()){
                    val jsonObject: JSONObject = jsonArray.getJSONObject(item)
                    val id:String = jsonObject.getString("id")
                    val category:String = jsonObject.getString("category")
                    val productModel = Product_Model(id,category)

                    productList.add(0,productModel)

                }


                for(item in 0 until productList.size){

                    finalList.add(productList.get(item).category)

                }

                Collections.shuffle(productList)


                val  adapter: Product_Adapter? = context?.let { Product_Adapter(it,productList) }
                if (adapter != null) {
                    adapter.notifyDataSetChanged()
                }
                if (adapter != null) {
                    setRecyclerView(adapter)
                }

            }else{

                Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

            }

        },{ error->

            Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

        })

        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(request)


    }

    private fun setRecyclerView(finaladapter: Product_Adapter) {
        binding.mainProductRecycler.setHasFixedSize(true)
        binding.mainProductRecycler.layoutManager= LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        binding.mainProductRecycler.adapter=finaladapter


    }



    override fun onResume() {
        super.onResume()
        val value = context?.let { SharedPref.readFromSharedPref(it,"backOrder","0") }
        if(value.equals("1")){
            GetCategories()
            context?.let { SharedPref.writeInSharedPref(it,"backOrder","0") }
        }
    }



}
