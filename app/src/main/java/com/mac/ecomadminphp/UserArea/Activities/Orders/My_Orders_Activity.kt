package com.mac.ecomadminphp.UserArea.Activities.Orders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.UserArea.Activities.Adapters.OrderAdapter
import com.mac.ecomadminphp.UserArea.Activities.Adapters.ToPay_Adapter
import com.mac.ecomadminphp.UserArea.Activities.Model.Cart_item_Model
import com.mac.ecomadminphp.UserArea.Activities.Model.OrderModel
import com.mac.ecomadminphp.UserArea.Activities.Model.ToPay_Model
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomadminphp.databinding.ActivityMyOrdersBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class My_Orders_Activity : AppCompatActivity() {
    private  lateinit var binding:ActivityMyOrdersBinding
    private val fetchOrders = Constants.baseUrl + "/Orders/getOrdersForUser.php"
    private val fetchToPay = Constants.baseUrl + "/Orders/getToPayInfo.php"
    private lateinit var uid:String
    private  var orderList= mutableListOf<OrderModel>()
    private  var toPayList= mutableListOf<ToPay_Model>()
    private  var orderIDList= mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="My orders"

        GetUser()
        orderList.clear()

        getOrders()



    }
    private fun GetUser() {
        val checkUser =
            SharedPref.readFromSharedPref(this, Constants.userPrefName, Constants.defaultPrefValue);
        if (!checkUser.equals("0")) {
            val splitDetails = checkUser?.split(",")
            uid = splitDetails?.get(3) ?: "uid"

        }
    }



    private fun getOrders() {
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchOrders,
            Response.Listener { response ->

                try {

                    val jsonObject = JSONObject(response)
                    val success: String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if (success.equals("1")) {


                        for (item in 0 until jsonArray.length()) {

                            val jsonObject: JSONObject = jsonArray.getJSONObject(item)

                            val id: String = jsonObject.getString("id")
                            val orderId: String = jsonObject.getString("orderId")
                            val productId: String = jsonObject.getString("productId")
                            val productQuantity: String = jsonObject.getString("productQuantity")
                            val productAddress: String = jsonObject.getString("productAddress")
                            val productTotalPay: String = jsonObject.getString("productTotalPay")
                            val productPaymentMode: String = jsonObject.getString("productPaymentMode")
                            val productPaymentStatus: String = jsonObject.getString("productPaymentStatus")
                            val productTrackingStatus: String = jsonObject.getString("productTrackingStatus")
                            val productUid: String = jsonObject.getString("productUid")
                            val productDeliveryDate: String = jsonObject.getString("productDeliveryDate")
                            val productName: String = jsonObject.getString("productName")
                            val productImage: String = jsonObject.getString("productImage")
                            val productOrderDate: String = jsonObject.getString("productOrderDate")
                            val productDescription: String = jsonObject.getString("productDescription")
                            val productRefundStatus: String = jsonObject.getString("productRefundStatus")

                            val data = OrderModel(id,orderId,productId,productQuantity,productAddress,productTotalPay,productPaymentMode,productPaymentStatus,productTrackingStatus,productUid,productDeliveryDate,productName,productImage, productOrderDate,productDescription,productRefundStatus)
                            if(!data.orderId.contains(",")){
                                orderIDList.add(orderId)
                            }
                            orderList.add(0,data)


                        }

                         binding.orderRecycler.setHasFixedSize(true)
                        binding.orderRecycler.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                        val adapter = OrderAdapter(this,orderList,this)
                        adapter.notifyDataSetChanged()
                        binding.orderRecycler.adapter=adapter

                        SetToPayRecycler()



                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["orderId"] = "ODM"+uid
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

    private fun SetToPayRecycler() {
        var set: MutableSet<String> = LinkedHashSet()
        set.addAll(orderIDList)
        orderIDList.clear()
        orderIDList.addAll(set)

        for(item in orderIDList){

            val request: StringRequest = object : StringRequest(
                Method.POST, fetchToPay,
                Response.Listener { response ->


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
                                val dc: String = jsonObject.getString("dc")

                                var data = ToPay_Model(id,orderId,total,status,dc)
                                if(!data.status.equals("1")){
                                    toPayList.add(data)

                                }



                            }

                            binding.toPayRecycler.setHasFixedSize(true)
                            binding.toPayRecycler.layoutManager=LinearLayoutManager(this)
                            val adapter = ToPay_Adapter(this,toPayList)
                            adapter.notifyDataSetChanged()
                            binding.toPayRecycler.adapter=adapter



                        }

                    } catch (e: JSONException) {
                        e.printStackTrace();
                    }


                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()


                }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["orderId"] = item
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
}