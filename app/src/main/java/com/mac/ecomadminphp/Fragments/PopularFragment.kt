package com.mac.ecomadminphp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.mac.ecomadminphp.Adapters.PopularModel
import com.mac.ecomadminphp.Adapters.PopularVerticalAdapter
import com.mac.ecomadminphp.UserArea.Activities.ViewProduct.SeeMore_Activity
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.FragmentPopularBinding
import org.json.JSONArray
import org.json.JSONObject

class PopularFragment : Fragment() {

    private lateinit var _binding: FragmentPopularBinding
    private val binding get() = _binding
    private var popularList = mutableListOf<PopularModel>()
    private var verticalPopularList = mutableListOf<PopularModel>()
    private val getHorizontalPopularUrl = Constants.baseUrl + "/Popular/getHorizontalPopular.php";
    private val getVerticalPopularUrl = Constants.baseUrl + "/Popular/getVerticalPopular.php";
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularBinding.inflate(layoutInflater)



        GetHorizontalPoplularList()
        GetVerticalPoplularList()


        return binding.root
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

                    setImages()


                } else {

                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()

                }

            }, { error ->

                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()

            })

        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(request)


    }

    private fun setImages() {
        val imageList = ArrayList<SlideModel>()
        for (item in popularList) {
            imageList.add(
                SlideModel(
                    Constants.baseUrl + "/Popular/PosterImage/" + item.image,
                    ScaleTypes.FIT
                )
            )
        }

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)

        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {

                val intent = Intent(context, SeeMore_Activity::class.java)
                intent.putExtra("category", popularList.get(position).category)
                startActivity(intent)

            }
        })
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

                binding.verticalRecycler.setHasFixedSize(true)
                binding.verticalRecycler.layoutManager=GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
                val adapter = context?.let { PopularVerticalAdapter(it,verticalPopularList) }
                if (adapter != null) {
                    adapter.notifyDataSetChanged()
                }
                binding.verticalRecycler.adapter=adapter


            } else {

                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()

            }

        }, { error ->

            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()

        })

        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(request)


    }


}