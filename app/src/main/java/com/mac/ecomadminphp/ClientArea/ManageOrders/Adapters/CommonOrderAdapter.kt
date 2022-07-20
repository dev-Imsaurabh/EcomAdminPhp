package com.mac.ecomadminphp.ClientArea.ManageOrders.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomadminphp.ClientArea.ManageOrders.CommonOrderViewActivity
import com.mac.ecomadminphp.ClientArea.ManageOrders.Models.CommonOrderModel
import com.mac.ecomadminphp.databinding.CommonOrderItemLayoutBinding

class CommonOrderAdapter(val context:Context,val list:MutableList<CommonOrderModel>):
    RecyclerView.Adapter<CommonOrderAdapter.myViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding = CommonOrderItemLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)


    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val listPos = list[position]
        holder.setData(listPos)

    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class myViewHolder(val binding: CommonOrderItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(listPos: CommonOrderModel) {

            val split = listPos.orderId.split(",")

            if(split.size>1){
                binding.orderId.setText("Order ID: "+split[0])
            }else{
                binding.orderId.setText("Order ID: "+listPos.orderId)
            }
            binding.goBtn.setOnClickListener {
                val intent =Intent(context,CommonOrderViewActivity::class.java)
                intent.putExtra("id",listPos.id)
                intent.putExtra("orderId",listPos.orderId)
                intent.putExtra("total",listPos.total)
                intent.putExtra("status",listPos.status)
                intent.putExtra("orderManagerId",listPos.orderMangerId)
                intent.putExtra("paymentMode",listPos.paymentMode)
                intent.putExtra("page",listPos.page)
                intent.putExtra("dc",listPos.dc)
                context.startActivity(intent)

            }



        }

    }
}