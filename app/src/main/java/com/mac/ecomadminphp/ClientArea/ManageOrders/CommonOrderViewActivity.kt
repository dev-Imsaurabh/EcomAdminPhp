package com.mac.ecomadminphp.ClientArea.ManageOrders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.ClientArea.ManageOrders.Adapters.ManageOrderAdapter
import com.mac.ecomadminphp.ClientArea.ManageOrders.Models.ManageOrderModel
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.ActivityCommonOrderViewBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class CommonOrderViewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCommonOrderViewBinding
    private lateinit var id:String
    private lateinit var inorderId:String
    private lateinit var total:String
    private lateinit var status:String
    private lateinit var orderManagerId:String
    private lateinit var paymentMode:String
    private lateinit var paymentId:String
    private lateinit var dc:String
    private lateinit var page:String
    private var orderList = mutableListOf<ManageOrderModel>()
    private var deliverBoyList = mutableListOf<String>()
    private val fetchUrl = Constants.baseUrl + "/Orders/getOrdersForUser.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCommonOrderViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Manage"


        getIntents()
        fetchUserInfo()
        SetToPay()


    }

    private fun SetToPay() {
        if(paymentMode.equals("online")){
            var split = inorderId.split(",")
            paymentId=split[1]
            binding.paymentId.setText("Payment ID: "+ paymentId)
            binding.orderId.setText("Order ID: "+split[0])
            binding.amountTxt.setText("Paid: ₹"+total)


        }else{
            binding.amountTxt.setText("To pay : ₹"+total)
            binding.orderId.setText("Order ID: "+inorderId)
        }


    }

    private fun GetUserOrder() {
        val dialog = ProgressDialog.progressDialog(this,"Loading...")
        dialog.show()
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchUrl,
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

                            if(page.equals("0")){
                                if(productTrackingStatus.equals("ordered")||productTrackingStatus.equals("shipped")){
                                    var data = ManageOrderModel(id,orderId,productId,productQuantity,productAddress,productTotalPay,productPaymentMode,productPaymentStatus,productTrackingStatus,productUid,productDeliveryDate,productName,productImage, productOrderDate,productDescription,productRefundStatus,total,status)
                                    if(data.orderId.equals(inorderId)){
                                        orderList.add(0,data)
                                    }
                                }

                            }else if(page.equals("1")){
                                if(productTrackingStatus.equals("cancel")){
                                    var data = ManageOrderModel(id,orderId,productId,productQuantity,productAddress,productTotalPay,productPaymentMode,productPaymentStatus,productTrackingStatus,productUid,productDeliveryDate,productName,productImage, productOrderDate,productDescription,productRefundStatus,total,status)
                                    if(data.orderId.equals(inorderId)){
                                        orderList.add(0,data)
                                    }
                                }

                            }else if(page.equals("2")){
                                if(productTrackingStatus.equals("delivered")){
                                    var data = ManageOrderModel(id,orderId,productId,productQuantity,productAddress,productTotalPay,productPaymentMode,productPaymentStatus,productTrackingStatus,productUid,productDeliveryDate,productName,productImage, productOrderDate,productDescription,productRefundStatus,total,status)
                                    if(data.orderId.equals(inorderId)){
                                        orderList.add(0,data)
                                    }
                                }

                            }else if(page.equals("3")){
                                if(productTrackingStatus.equals("return")){
                                    var data = ManageOrderModel(id,orderId,productId,productQuantity,productAddress,productTotalPay,productPaymentMode,productPaymentStatus,productTrackingStatus,productUid,productDeliveryDate,productName,productImage, productOrderDate,productDescription,productRefundStatus,total,status)
                                    if(data.orderId.equals(inorderId)){
                                        orderList.add(0,data)
                                    }
                                }

                            }else if(page.equals("4")){
                                if(productTrackingStatus.equals("refund")){
                                    var data = ManageOrderModel(id,orderId,productId,productQuantity,productAddress,productTotalPay,productPaymentMode,productPaymentStatus,productTrackingStatus,productUid,productDeliveryDate,productName,productImage, productOrderDate,productDescription,productRefundStatus,total,status)
                                    if(data.orderId.equals(inorderId)){
                                        orderList.add(0,data)
                                    }
                                }

                            }else if(page.equals("5")){
                                if(!productTrackingStatus.equals("ordered")){
                                    var data = ManageOrderModel(id,orderId,productId,productQuantity,productAddress,productTotalPay,productPaymentMode,productPaymentStatus,productTrackingStatus,productUid,productDeliveryDate,productName,productImage, productOrderDate,productDescription,productRefundStatus,total,status)
                                    if(data.orderId.equals(inorderId)){
                                        orderList.add(0,data)
                                    }
                                }

                            }





                        }

                        binding.manageOrderRecycler.setHasFixedSize(true)
                        binding.manageOrderRecycler.layoutManager=
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
                        val adapter = ManageOrderAdapter(this,orderList,deliverBoyList,dc,page)
                        adapter.notifyDataSetChanged()
                        binding.manageOrderRecycler.adapter=adapter
                        dialog.dismiss()



                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["orderId"] = orderManagerId
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

    private fun getIntents() {
        id = intent.getStringExtra("id").toString()
        inorderId= intent.getStringExtra("orderId").toString()
        total = intent.getStringExtra("total").toString()
        status = intent.getStringExtra("status").toString()
        orderManagerId = intent.getStringExtra("orderManagerId").toString()
        paymentMode = intent.getStringExtra("paymentMode").toString()
        page = intent.getStringExtra("page").toString()
        dc = intent.getStringExtra("dc").toString()
    }


    private fun fetchUserInfo(
    ) {
        val getDeliveryUsers = Constants.baseUrl + "/dauth/getDeliveryUsers.php"
        val request: StringRequest = StringRequest(
            Request.Method.POST, getDeliveryUsers,
            { response ->

                try {

                    val jsonObject  = JSONObject(response)
                    val success:String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if(success.equals("1")){

                        for (item in 0 until jsonArray.length()){
                            val jsonObject:JSONObject = jsonArray.getJSONObject(item)
                            val id:String =jsonObject.getString("id")
                            val name:String =jsonObject.getString("username")
                            val email:String =jsonObject.getString("email")
                            val password:String =jsonObject.getString("password")
                            val emailv:String= jsonObject.getInt("emailv").toString()
                            val uid:String= jsonObject.getString("uid")

                            deliverBoyList.add(name+","+uid)

                        }
                        GetUserOrder()


                    }

                }catch (e: JSONException){
                    e.printStackTrace();
                }


            },
            { error ->

            })


        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }



}