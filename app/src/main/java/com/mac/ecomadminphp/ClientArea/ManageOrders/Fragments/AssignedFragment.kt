package com.mac.ecomadminphp.ClientArea.ManageOrders.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.ClientArea.ManageOrders.Adapters.CommonOrderAdapter
import com.mac.ecomadminphp.ClientArea.ManageOrders.Models.CommonOrderModel
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.FragmentActiveFragmentsBinding
import com.mac.ecomadminphp.databinding.FragmentAssignedBinding
import com.mac.ecomadminphp.databinding.FragmentHomeBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class AssignedFragment : Fragment() {
    private lateinit var _binding: FragmentAssignedBinding
    private val binding get() = _binding
    private val fetchUrl = Constants.baseUrl + "/Orders/getOrdersForAdmin.php"
    private var activeList = mutableListOf<CommonOrderModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssignedBinding.inflate(layoutInflater)


        getOrder()


        return binding.root
    }

    private fun getOrder() {
        val request: StringRequest =StringRequest(
            Request.Method.POST, fetchUrl,
            { response ->

                try {

                    val jsonObject  = JSONObject(response)
                    val success:String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if(success.equals("1")){
                        for(item in 0 until jsonArray.length()){
                            val jsonObject: JSONObject = jsonArray.getJSONObject(item)

                            val id:String =jsonObject.getString("id")
                            val orderId:String =jsonObject.getString("orderId")
                            val total:String =jsonObject.getString("total")
                            val status:String =jsonObject.getString("status")
                            val orderManagerId:String= jsonObject.getString("orderManagerId")
                            val paymentMode:String= jsonObject.getString("paymentMode")
                            val dc:String= jsonObject.getString("dc")

                            val data = CommonOrderModel(id,orderId,total,status,orderManagerId,paymentMode,"5",dc)
                            if(data.status.equals("0")||data.status.equals("1")){
                                activeList.add(0,data)
                            }

                        }

                        binding.AssignRecycler.setHasFixedSize(true)
                        binding.AssignRecycler.layoutManager=LinearLayoutManager(context)
                        val adapter = context?.let { CommonOrderAdapter(it,activeList,) }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged()
                        }
                        binding.AssignRecycler.adapter=adapter




                    }

                }catch (e: JSONException){
                    e.printStackTrace();
                }


            },
            { error ->

            })

        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(request)
    }

}