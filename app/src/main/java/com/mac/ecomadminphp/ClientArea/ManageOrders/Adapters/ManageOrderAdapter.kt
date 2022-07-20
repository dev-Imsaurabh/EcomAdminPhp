package com.mac.ecomadminphp.ClientArea.ManageOrders.Adapters

import android.R
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.ClientArea.ManageOrders.Models.ManageOrderModel
import com.mac.ecomadminphp.UserArea.Activities.Model.OrderModel
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.ManageOrderItemLayoutBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ManageOrderAdapter(
    val context: Context,
    val orderList: MutableList<ManageOrderModel>,
    val deliverBoyList: MutableList<String>,
    val dc: String,
    val page: String
): RecyclerView.Adapter<ManageOrderAdapter.myViewHolder>() {
    lateinit var deluid:String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding =ManageOrderItemLayoutBinding.inflate(LayoutInflater.from(context))
        return  myViewHolder(binding)

    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val orderListPos = orderList[position]
        holder.setData(orderListPos)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }


    inner class myViewHolder(val binding: ManageOrderItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(orderListPos: ManageOrderModel) {
            if(!deliverBoyList.isEmpty()){
                setSpinner(deliverBoyList,binding,orderListPos)

            }

            if(orderListPos.productTrackingStatus.equals("ordered")||orderListPos.productTrackingStatus.equals("return")){
                binding.topay.visibility= VISIBLE
                binding.assignSpinner.visibility= VISIBLE
            }

            if(orderListPos.productPaymentMode.equals("cod")){
                binding.orderId.setText("Order ID: "+orderListPos.orderId)

            }else{
                binding.orderId.setText("Order ID: "+orderListPos.orderId.split(",")[0])

            }
            if(orderListPos.productPaymentMode.equals("cod")){
                binding.topay.setText("You have to pay ₹"+(orderListPos.productToPay.toInt()*orderListPos.productQuantity.toInt()))
            }else{
                binding.topay.setText("Paid ₹"+(orderListPos.productToPay.toInt()*orderListPos.productQuantity.toInt()))

            }
            binding.productName.setText(orderListPos.productName+" (x${orderListPos.productQuantity})")
            binding.orderDate.setText("Order date:\n"+ getOrderDate((orderListPos.productOrderDate.toLong()*1000).toString()))
            binding.producAddress.setText(orderListPos.productAddress)
            binding.productDeliveryDate.setText("Expected delivery:\n"+ getExpectedDate((orderListPos.productDeliveryDate.toLong()).toString()))
            binding.productDescription.setText(orderListPos.productDescription)
            Picasso.get().load(Constants.baseUrl+"/Products/ProductImages/"+orderListPos.productImage).into(binding.orderImage)

            if(orderListPos.productTrackingStatus.equals("ordered")){
                binding.trackDot1.visibility=VISIBLE
            }else if(orderListPos.productTrackingStatus.equals("assigned")){
                binding.trackDot1.visibility=VISIBLE

            } else if(orderListPos.productTrackingStatus.equals("shipped")){
                binding.trackDot2.visibility=VISIBLE
                binding.orderTracker.progress=50

            }else if(orderListPos.productTrackingStatus.equals("delivered")){
                binding.trackDot3.visibility=VISIBLE
                binding.orderTracker.progress=100
            }

            if(orderListPos.productTrackingStatus.equals("ordered")){
                binding.confirmBtn.visibility= VISIBLE
            }else if(orderListPos.productTrackingStatus.equals("cancel")){
                binding.cancelBtn.visibility= VISIBLE
            }else if(orderListPos.productTrackingStatus.equals("delivered")){

                binding.assignSpinner.visibility= GONE
                binding.productDeliveryDate.visibility= GONE

            }else if(orderListPos.productTrackingStatus.equals("return")){
                binding.returnBtn.visibility= VISIBLE
            }else if(orderListPos.productTrackingStatus.equals("refund")){
                binding.confirmRefund.visibility= VISIBLE
            }else if(orderListPos.productTrackingStatus.equals("shipped")){
                binding.assignSpinner.visibility= GONE
            }else if(orderListPos.productTrackingStatus.equals("assigned")){
                binding.assignSpinner.visibility= GONE

            }


            if(orderListPos.productDescription.contains("OTP")){
                binding.productDescription.setText("OTP Generated")
            }

            if(page.equals("5")&&orderListPos.productPaymentStatus.equals("1")){
                binding.cashTrackerLv.visibility= VISIBLE

                val list = mutableListOf<String>()
                list.add(orderListPos.productDescription)

                val  adapter = ArrayAdapter(context, R.layout.simple_list_item_1, list)
                adapter.notifyDataSetChanged()
                binding.cashTrackerLv.adapter = adapter

            }





        }




    }

    fun getOrderDate(orderDateTimeStamp: String): String? {
        val format: SimpleDateFormat
        val timeStamp = orderDateTimeStamp.toLong()
        val netDate = Date(timeStamp)
        format = SimpleDateFormat("dd-MM-yy h:mm a", Locale.getDefault())
        return format.format(netDate)
    }

    fun getExpectedDate(orderDateTimeStamp: String): String? {
        val format: SimpleDateFormat
        val timeStamp = orderDateTimeStamp.toLong()
        val netDate = Date(timeStamp)
        format = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        return format.format(netDate)
    }



    private fun setSpinner(
        finalList: MutableList<String>,
        binding: ManageOrderItemLayoutBinding,
        orderListPos: ManageOrderModel
    ) {
        if(!finalList.get(0).equals("Assign delivery boy")){
            finalList.add(0, "Assign delivery boy")
        }

        val adapter = ArrayAdapter(context,R.layout.simple_spinner_dropdown_item,finalList)
        adapter.notifyDataSetChanged()
        binding.assignSpinner.adapter = adapter

        binding.assignSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                if(!finalList[position].equals("Assign delivery boy")){
                    readyTocheckConfirmBtn(finalList[position].split(",")[1],orderListPos,binding,finalList[position])
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }

    }

    private fun readyTocheckConfirmBtn(
        uid: String,
        orderListPos: ManageOrderModel,
        binding: ManageOrderItemLayoutBinding,
        fulldeluid: String
    ) {

        val deluid = "del"+uid
        binding.confirmBtn.setOnClickListener {
            val dialog = ProgressDialog.progressDialog(context,"Confirming order...")
            dialog.show()
            GetUserOrder(deluid,orderListPos,binding,dialog,fulldeluid)
        }


    }


    private fun GetUserOrder(
        deluid: String,
        orderListPos: ManageOrderModel,
        binding: ManageOrderItemLayoutBinding,
        dialog: Dialog,
        fulldeluid: String
    ) {
        val distinctList = mutableListOf<OrderModel>()
        var toPay:Int

        val fetchUrl = Constants.baseUrl + "/Orders/getOrdersForUser.php"
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

                           val orderModel = OrderModel(id,orderId,productId,productQuantity,productAddress,productTotalPay,productPaymentMode,productPaymentStatus,productTrackingStatus,productUid,productDeliveryDate,productName,productImage,productOrderDate,productDescription,productRefundStatus)
                            if(orderModel.orderId.equals(orderListPos.orderId)&&orderModel.productTrackingStatus.equals("ordered")){
                                distinctList.add(orderModel)
                            }


                        }

                        if(distinctList.size>1){

                            toPay=orderListPos.productToPay.toInt()*orderListPos.productQuantity.toInt()

                        }else{
                            toPay =orderListPos.productToPay.toInt()*orderListPos.productQuantity.toInt()+dc.toInt()
                        }


                        AssignDeliveryBoy(dialog,deluid,orderListPos,toPay.toString(),binding,fulldeluid)




                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["orderId"] = orderListPos.productUid
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

    private fun AssignDeliveryBoy(
        dialog: Dialog,
        deluid: String,
        orderListPos: ManageOrderModel,
        topay: String,
        binding: ManageOrderItemLayoutBinding,
        fulldeluid: String
    ) {

        val assignUrl = Constants.baseUrl + "/dauth/assign.php"
        val request: StringRequest = object : StringRequest(
            Method.POST, assignUrl,
            Response.Listener { response ->
                if(response.equals("assigned")){
                    binding.confirmBtn.setText("Assigned")
                    binding.confirmBtn.isEnabled=false
                    dialog.dismiss()
                }else{
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }



            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                try {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["orderId"] = orderListPos.orderId
                params["productId"] = orderListPos.productId
                params["orderManagerId"] = orderListPos.productUid
                params["total"] = topay
                params["status"] = "0"
                params["deluid"] = deluid
                params["fulldeluid"] = fulldeluid
                params["uid"] = orderListPos.productUid.replace("ODM","").trim()
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