package com.mac.ecomadminphp.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.UserArea.ModelAndAdapters.Product_Adapter
import com.mac.ecomadminphp.UserArea.ModelAndAdapters.Product_Model
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomadminphp.databinding.FragmentHomeBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class HomeFragment : Fragment() {

    private  var finalList= mutableListOf<String>()
    private val getUrl = Constants.baseUrl+"/Categories/getCategory.php"
    private val countUrl = Constants.baseUrl+"/Categories/categoryCount.php"
    private lateinit var _binding:FragmentHomeBinding
    private val binding get() = _binding
    private var done:Int=0
    val productList = mutableListOf<Product_Model>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        GetCategories()


        return binding.root
    }



    private fun GetCategories() {
        binding.loadingLayout.root.visibility= VISIBLE
        val request: StringRequest = StringRequest(Request.Method.POST,getUrl ,{ response->


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

                    getProductCount(productList.get(item).category)


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

    private fun getProductCount(category: String) {

        val request: StringRequest = object:
            StringRequest(Request.Method.POST, countUrl, { response ->
                done++

                if(!response.equals("0")){
                    finalList.add(category)
                }

                if(done==productList.size){
                    Collections.shuffle(finalList)


                    try {
                        val  adapter: Product_Adapter? = context?.let { Product_Adapter(it,finalList) }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged()
                            setRecyclerView(adapter)
                        }
                    } catch (e: Exception) {

                    }

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

    private fun setRecyclerView(finaladapter: Product_Adapter) {
        binding.mainProductRecycler.setHasFixedSize(true)
        binding.mainProductRecycler.layoutManager= WrapContentLinearLayoutManager(context)
//        binding.mainProductRecycler.layoutManager= LinearLayoutManager(context,
//            LinearLayoutManager.VERTICAL,false)
        binding.mainProductRecycler.adapter=finaladapter
        binding.loadingLayout.root.visibility=GONE
        done=0


    }



    override fun onResume() {
        super.onResume()
        val value = context?.let { SharedPref.readFromSharedPref(it,"backOrder","0") }
        if(value.equals("1")){
            GetCategories()
            context?.let { SharedPref.writeInSharedPref(it,"backOrder","0") }
        }
    }


    class WrapContentLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
        //... constructor
        override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
            }
        }
    }


}
