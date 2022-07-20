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
import com.mac.ecomadminphp.UserArea.Activities.Model.OrderModel
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.CancelReasonLayoutBinding
import com.mac.ecomadminphp.databinding.OrderItemLayoutBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(val context:Context,val orderList:MutableList<OrderModel>): RecyclerView.Adapter<OrderAdapter.myViewHolder>() {


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

            }else if(orderListPos.productTrackingStatus.equals("assigned")){
                binding.cancelBtn.visibility= GONE
                binding.trackDot1.visibility= VISIBLE

            }
            else if(orderListPos.productTrackingStatus.equals("shipped")){
                binding.trackDot2.visibility=VISIBLE
                binding.orderTracker.progress=50
                binding.cancelBtn.visibility= GONE

            }else if(orderListPos.productTrackingStatus.equals("delivered")){
                binding.trackDot3.visibility=VISIBLE
                binding.orderTracker.progress=100
                binding.productDeliveryDate.visibility== GONE
                binding.cancelBtn.visibility= GONE
            }else if(orderListPos.productTrackingStatus.equals("cancel")){

                binding.cancelBtn.visibility= GONE

            }else if(orderListPos.productTrackingStatus.equals("return")){

                binding.cancelBtn.visibility= GONE

            }else if(orderListPos.productTrackingStatus.equals("refund")){

                binding.cancelBtn.visibility= GONE

            }else if(orderListPos.productTrackingStatus.equals("refunded")){

                binding.cancelBtn.visibility= GONE

            }else if(orderListPos.productTrackingStatus.equals("returned")){

                binding.cancelBtn.visibility= GONE

            }




            binding.cancelBtn.setOnClickListener {

                openCancelReasonDialog(binding,orderListPos)
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
                reason= radio.text.toString()
            })
        dialogView.cancelNowbtn.setOnClickListener {
            if(!reason.equals("")){
                startCancellation(binding,dialog,dialogView,orderListPos)

            }else{
                Toast.makeText(context, "please select at least one reason", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun startCancellation(
        binding: OrderItemLayoutBinding,
        dialog: Dialog,
        dialogView: CancelReasonLayoutBinding,
        orderListPos: OrderModel
    ) {
        val pd = ProgressDialog.progressDialog(context,"Cancelling order...")
        pd.show()
        if(orderListPos.productPaymentMode.equals("cod")){
            

        }else{

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
}