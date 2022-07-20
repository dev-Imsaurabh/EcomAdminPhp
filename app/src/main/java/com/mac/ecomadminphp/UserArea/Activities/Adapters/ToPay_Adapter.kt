package com.mac.ecomadminphp.UserArea.Activities.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomadminphp.UserArea.Activities.Model.ToPay_Model
import com.mac.ecomadminphp.databinding.ActivitySeeMoreBinding
import com.mac.ecomadminphp.databinding.TopayItemLayoutBinding

class ToPay_Adapter(val context:Context,val toPayList:MutableList<ToPay_Model>): RecyclerView.Adapter<ToPay_Adapter.myViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val binding = TopayItemLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val topayListPos = toPayList[position]
        holder.setData(topayListPos)
    }

    override fun getItemCount(): Int {

        return  toPayList.size
    }

    inner class myViewHolder(val binding: TopayItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(topayListPos: ToPay_Model) {
            binding.orderId.setText("Order ID: "+topayListPos.orderId)
            binding.amountTxt .setText("To pay: ₹"+topayListPos.total)

        }

    }
}