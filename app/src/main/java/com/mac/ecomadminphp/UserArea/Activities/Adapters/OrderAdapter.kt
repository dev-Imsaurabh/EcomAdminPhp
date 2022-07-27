package com.mac.ecomadminphp.UserArea.Activities.Adapters

import android.R
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.UserArea.Activities.Model.OrderModel
import com.mac.ecomadminphp.UserArea.Activities.Orders.My_Orders_Activity
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.CancelReasonLayoutBinding
import com.mac.ecomadminphp.databinding.OrderItemLayoutBinding
import com.mac.ecomadminphp.databinding.ReturnDetailesLayoutBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(val context:Context,val orderList:MutableList<OrderModel>,val activity:My_Orders_Activity): RecyclerView.Adapter<OrderAdapter.myViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding =OrderItemLayoutBinding.inflate(LayoutInflater.from(context))
        return  myViewHolder(binding)

    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val orderListPos = orderList[position]
        holder.setData(orderListPos)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }


    inner class myViewHolder(val binding: OrderItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(orderListPos: OrderModel) {
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
            binding.productDescription.setText(orderListPos.productDescription.split(",")[0])
            Picasso.get().load(Constants.baseUrl+"/Products/ProductImages/"+orderListPos.productImage).into(binding.orderImage)

            if(orderListPos.productTrackingStatus.equals("ordered")){
                binding.trackDot1.visibility=VISIBLE
                binding.topay.visibility= VISIBLE
                binding.cancelBtn.visibility= VISIBLE
                binding.showStatus.setText("Ordered")
                binding.showStatus.setTextColor(context.resources.getColor(R.color.holo_green_dark))


            }else if(orderListPos.productTrackingStatus.equals("assigned")){
                binding.cancelBtn.visibility= GONE
                binding.trackDot1.visibility= VISIBLE
                binding.topay.visibility= VISIBLE

            }
            else if(orderListPos.productTrackingStatus.equals("shipped")){
                binding.trackDot2.visibility=VISIBLE
                binding.orderTracker.progress=50
                binding.cancelBtn.visibility= GONE
                binding.topay.visibility= VISIBLE


            }else if(orderListPos.productTrackingStatus.equals("delivered")){
                binding.trackDot3.visibility=VISIBLE
                binding.orderTracker.progress=100
                binding.productDeliveryDate.visibility== GONE
                binding.cancelBtn.visibility= GONE
                var currentDate: Date = Date(orderListPos.productRefundStatus.split(",")[2].toLong())
                var milis = getReturnFutureDate(currentDate,3)?.toLong()
                if(milis?.let { System.currentTimeMillis().compareTo(it) }!! <0){
                    binding.returnBtn.visibility= GONE
                }else{
                    binding.returnBtn.visibility= VISIBLE
                }

            }else if(orderListPos.productTrackingStatus.equals("cancel")){

                binding.cancelBtn.visibility= GONE
                binding.showStatus.setText("Canceled")
                binding.productDescription.setText(orderListPos.productDescription)
                binding.productDescription.setTextColor(context.resources.getColor(R.color.holo_red_dark))
                binding.productDeliveryDate.visibility= GONE
                binding.showStatus.setTextColor(context.resources.getColor(R.color.holo_red_dark))
            }else if(orderListPos.productTrackingStatus.equals("return")){
                binding.returnBtn.visibility = GONE
                binding.cancelBtn.visibility= GONE
                binding.productDeliveryDate.visibility= GONE
                binding.trackLayout.visibility= GONE

            }else if(orderListPos.productTrackingStatus.equals("refund")){
                binding.trackLayout.visibility= GONE
                binding.cancelBtn.visibility= GONE
                binding.showStatus.setText("Canceled")
                binding.productDescription.setText(orderListPos.productDescription)
                binding.productDescription.setTextColor(context.resources.getColor(R.color.holo_red_dark))
                binding.productDeliveryDate.visibility= GONE
                binding.showStatus.setTextColor(context.resources.getColor(R.color.holo_red_dark))


            }else if(orderListPos.productTrackingStatus.equals("refunded")){
                binding.showStatus.setText("Refunded")
                binding.productDescription.setText(orderListPos.productDescription)
                binding.productDescription.setTextColor(context.resources.getColor(R.color.holo_red_dark))
                binding.productDeliveryDate.visibility= GONE
                binding.showStatus.setTextColor(context.resources.getColor(R.color.holo_red_dark))
                binding.cancelBtn.visibility= GONE
                binding.trackLayout.visibility= GONE


            }else if(orderListPos.productTrackingStatus.equals("returned")){

                binding.showStatus3.setText("Returned")
                binding.productDescription.setText(orderListPos.productDescription)
                binding.productDescription.setTextColor(context.resources.getColor(R.color.holo_green_dark))
                binding.productDeliveryDate.visibility= GONE
                binding.showStatus.setTextColor(context.resources.getColor(R.color.holo_red_dark))
                binding.cancelBtn.visibility= GONE
                binding.trackLayout.visibility= GONE
            }




            binding.cancelBtn.setOnClickListener {

                openCancelReasonDialog(binding,orderListPos)
            }

         //retutn btn pe click listener lagana hai aur account add ka option bhi denahai
            binding.returnBtn.setOnClickListener {

                val dialog = Dialog(context, R.style.ThemeOverlay_Material_Light)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val dialogView = ReturnDetailesLayoutBinding.inflate(LayoutInflater.from(context))
                dialog.setContentView(dialogView.root)
                dialog.setCancelable(true)
                dialog.show()


                val status = "return"
                val et_a = dialogView.accountNumber
                val et_i = dialogView.ifscCode
                val bt_return = dialogView.returnRequestBtn
                if(orderListPos.productPaymentMode.equals("cod")){
                    et_a.visibility= VISIBLE
                    et_i.visibility= VISIBLE
                }else{
                    et_a.visibility= GONE
                    et_i.visibility= GONE
                }
                bt_return.setOnClickListener {
                    if(orderListPos.productPaymentMode.equals("cod")){
                        if(!et_a.text.toString().trim().isEmpty()&&!et_i.text.toString().trim().isEmpty()){
                            val pd = ProgressDialog.progressDialog(context,"Returning order...")
                            pd.show()
                            val reason = "Return request: Acc.Num: ${et_a.text.toString().trim()}\nIFSC: ${et_i.text.toString().trim()}\nRefund amount : ₹${orderListPos.productToPay}"
                            updateStatus("null",orderListPos,binding,dialog,pd,status,reason)
                        }else{
                            Toast.makeText(context, "Please enter correct details", Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        val pd = ProgressDialog.progressDialog(context,"Returning order...")
                        pd.show()
                        val reason = "Return request: Please return and refund to primary account ₹${orderListPos.productToPay}"
                        updateStatus("null",orderListPos,binding,dialog,pd,status,reason)

                    }

                }
            }

        }

    }

    private fun openCancelReasonDialog(binding: OrderItemLayoutBinding, orderListPos: OrderModel) {

        var reason=""
        val dialog = Dialog(context, R.style.ThemeOverlay_Material_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogView = CancelReasonLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogView.root)
        dialog.setCancelable(true)
        dialog.show()

        dialogView.cancelRadioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = dialog.findViewById(checkedId)
                if(orderListPos.productPaymentMode.equals("cod")){
                    reason= "Customer canceled the order on "+getOrderDate(System.currentTimeMillis().toString())+" due to "+radio.text.toString()
                }else{
                    reason= "Customer canceled the order on "+getOrderDate(System.currentTimeMillis().toString())+" due to "+radio.text.toString() +"\n issuing refund of ₹ "

                }
            })
        dialogView.cancelNowbtn.setOnClickListener {
            if(!reason.equals("")){
                startCancellation(binding,dialog,dialogView,orderListPos,reason)

            }else{
                Toast.makeText(context, "please select at least one reason", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun startCancellation(
        binding: OrderItemLayoutBinding,
        dialog: Dialog,
        dialogView: CancelReasonLayoutBinding,
        orderListPos: OrderModel,
        reason: String
    ) {
        val pd = ProgressDialog.progressDialog(context,"Cancelling order...")
        pd.show()
        val status = "cancel"
        GetUserOrder(status,binding,pd,dialog,dialogView,orderListPos,reason)

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

    fun getReturnFutureDate(currentDate: Date?, days: Int): String? {
        val cal = Calendar.getInstance()
        cal.time = currentDate
        cal.add(Calendar.DATE, days)
        val format: DateFormat = SimpleDateFormat("EEE, d MMM")
        var date: Date? = null
        try {
            date = format.parse(format.format(cal.time).toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val millies = cal.timeInMillis
        return millies.toString()
//        return format.format(cal.time)
    }


    private fun GetUserOrder(
        status: String,
        binding: OrderItemLayoutBinding,
        pd: Dialog,
        dialog: Dialog,
        dialogView: CancelReasonLayoutBinding,
        orderListPos: OrderModel,
        reason: String
    ) {
        val distinctList = mutableListOf<OrderModel>()
        var value = ""

        val fetchUrl = Constants.baseUrl+ "/Orders/getOrdersForUser.php"
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchUrl,
            Response.Listener { response ->

                try {

                    val jsonObject = JSONObject(response)
                    val success: String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if (success.equals("1")) {
                        dialog.dismiss()


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

                            if(orderModel.orderId.equals(orderListPos.orderId)&&productTrackingStatus.equals("ordered")){
                                distinctList.add(orderModel)
                            }




                        }

                        if(distinctList.size>1){
                            value="0";
                        }else{
                            value="1"
                        }

                        updateStatus(value,orderListPos,binding,dialog,pd,status,reason)





                    }else{
                        dialog.dismiss()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->
                dialog.dismiss()
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

    private fun updateStatus(
        value: String,
        orderListPos: OrderModel,
        binding: OrderItemLayoutBinding,
        dialog: Dialog,
        pd: Dialog,
        status: String,
        reason: String
    ) {

        val fetchUrl = Constants.baseUrl1+ "/updateStatus.php"
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchUrl,
            Response.Listener { response ->
                if(response.equals("canceled")){
                    pd.dismiss()
                    binding.cancelBtn.visibility= GONE
                    binding.showStatus.setText("Canceled")
                    binding.productDescription.setText("Order canceled")
                    binding.productDescription.setTextColor(context.resources.getColor(R.color.holo_red_dark))
                    binding.productDeliveryDate.visibility= GONE
                    binding.showStatus.setTextColor(context.resources.getColor(R.color.holo_red_dark))
                    Toast.makeText(context,response, Toast.LENGTH_SHORT).show()
                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                    activity.startActivity(activity.intent);
                    activity.overridePendingTransition(0, 0);
                }else if(response.equals("return")){
                    pd.dismiss()
                    binding.returnBtn.visibility= GONE
                    binding.showStatus3.setText("Returned")
                    binding.productDescription.setText("Return request sent")
                    binding.productDescription.setTextColor(context.resources.getColor(R.color.holo_red_dark))
                    binding.productDeliveryDate.visibility= GONE
                    binding.showStatus3.setTextColor(context.resources.getColor(R.color.holo_red_dark))
                    Toast.makeText(context,response, Toast.LENGTH_SHORT).show()
                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                    activity.startActivity(activity.intent);
                    activity.overridePendingTransition(0, 0);
                }else{
                    Toast.makeText(context,response, Toast.LENGTH_SHORT).show()
                }



            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["orderId"] = orderListPos.orderId
                params["orderManagerId"] = orderListPos.productUid
                params["value"] = value
                params["topay"] = orderListPos.productToPay
                params["status"] = status
                params["productId"] = orderListPos.productId
                params["paymentMode"] = orderListPos.productPaymentMode
                params["reason"] = reason
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