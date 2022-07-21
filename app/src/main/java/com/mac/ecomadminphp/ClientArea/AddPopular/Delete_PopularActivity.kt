package com.mac.ecomadminphp.ClientArea.AddPopular

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.Adapters.DeletePopularAdapter
import com.mac.ecomadminphp.Adapters.PopularModel
import com.mac.ecomadminphp.Adapters.PopularVerticalAdapter
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.ActivityDeletePopularBinding
import org.json.JSONArray
import org.json.JSONObject

class Delete_PopularActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDeletePopularBinding
    private var popularList = mutableListOf<PopularModel>()
    private var verticalPopularList = mutableListOf<PopularModel>()
    private val getHorizontalPopularUrl = Constants.baseUrl + "/Popular/getHorizontalPopular.php";
    private val getVerticalPopularUrl = Constants.baseUrl + "/Popular/getVerticalPopular.php";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDeletePopularBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GetHorizontalPoplularList()



    }

    private fun GetHorizontalPoplularList() {

        val request: StringRequest =
            StringRequest(Request.Method.POST, getHorizontalPopularUrl, { response ->

                popularList.clear()
                val jsonObject = JSONObject(response)
                val success: String = jsonObject.getString("success")
                val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                if (success.equals("1")) {

                    for (item in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(item)
                        val id: String = jsonObject.getString("id")
                        val category: String = jsonObject.getString("category")
                        val tablename: String = jsonObject.getString("tablename")
                        val image: String = jsonObject.getString("image")
                        val popularModel = PopularModel(id, category, tablename, image)
                        popularList.add(popularModel)

                    }
                    GetVerticalPoplularList()



                } else {

                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

                }

            }, { error ->

                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

            })

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }

    private fun GetVerticalPoplularList() {

        val request: StringRequest = StringRequest(Request.Method.POST, getVerticalPopularUrl, { response ->

            verticalPopularList.clear()
            val jsonObject = JSONObject(response)
            val success: String = jsonObject.getString("success")
            val jsonArray: JSONArray = jsonObject.getJSONArray("data")
            if (success.equals("1")) {

                for (item in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(item)
                    val id: String = jsonObject.getString("id")
                    val category: String = jsonObject.getString("category")
                    val tablename: String = jsonObject.getString("tablename")
                    val image: String = jsonObject.getString("image")
                    val popularModel = PopularModel(id, category,tablename,image)
                    verticalPopularList.add(popularModel)

                }

                popularList.addAll(verticalPopularList)

                binding.deletePopularRecycler.setHasFixedSize(true)
                binding.deletePopularRecycler.layoutManager=LinearLayoutManager(this)
                val adapter = DeletePopularAdapter(this,popularList,this)
                adapter.notifyDataSetChanged()
                binding.deletePopularRecycler.adapter=adapter





            } else {

                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

            }

        }, { error ->

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

        })

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }


}