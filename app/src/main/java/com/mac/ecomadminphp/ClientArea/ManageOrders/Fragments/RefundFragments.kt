package com.mac.ecomadminphp.ClientArea.ManageOrders.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.ClientArea.ManageOrders.Adapters.CommonOrderAdapter
import com.mac.ecomadminphp.ClientArea.ManageOrders.Models.CommonOrderModel
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.FragmentRefundFragmentsBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RefundFragments : Fragment() {

    private lateinit var _binding: FragmentRefundFragmentsBinding
    private val binding get() = _binding
    private val fetchUrl = Constants.baseUrl + "/Orders/getOrdersForAdmin.php"
    private var refundList = mutableListOf<CommonOrderModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRefundFragmentsBinding.inflate(layoutInflater)
        getOrder()

        return binding.root
    }

    private fun getOrder() {
        val request: StringRequest = StringRequest(
            Request.Method.POST, fetchUrl,
            { response ->

                try {

                    val jsonObject = JSONObject(response)
                    val success: String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if (success.equals("1")) {
                        for (item in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(item)

                            val id: String = jsonObject.getString("id")
                            val orderId: String = jsonObject.getString("orderId")
                            val total: String = jsonObject.getString("total")
                            val status: String = jsonObject.getString("status")
                            val orderManagerId: String = jsonObject.getString("orderManagerId")
                            val paymentMode: String = jsonObject.getString("paymentMode")
                            val dc: String = jsonObject.getString("dc")

                            val data = CommonOrderModel(
                                id,
                                orderId,
                                total,
                                status,
                                orderManagerId,
                                paymentMode,"4",dc)
                            if (data.status.equals("0")) {
                                refundList.add(0,data)
                            }

                        }

                        binding.refundRecycler.setHasFixedSize(true)
                        binding.refundRecycler.layoutManager = LinearLayoutManager(context)
                        val adapter = context?.let { CommonOrderAdapter(it, refundList) }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged()
                        }
                        binding.refundRecycler.adapter = adapter


                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                }


            },
            { error ->

            })

        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(request)
    }

}