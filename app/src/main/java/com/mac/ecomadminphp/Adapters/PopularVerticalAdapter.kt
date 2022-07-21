package com.mac.ecomadminphp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomadminphp.UserArea.Activities.ViewProduct.SeeMore_Activity
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.ActivityAddPopularBinding
import com.mac.ecomadminphp.databinding.PopularVerticalLaypoutBinding
import com.squareup.picasso.Picasso

class PopularVerticalAdapter(val context:Context,val popularVerticalList:MutableList<PopularModel>):RecyclerView.Adapter<PopularVerticalAdapter.myViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding = PopularVerticalLaypoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        val popularListPos = popularVerticalList[position]
        holder.setData(popularListPos)

    }

    override fun getItemCount(): Int {

        return popularVerticalList.size

    }



    inner class myViewHolder(val binding: PopularVerticalLaypoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(popularListPos: PopularModel) {

            Picasso.get().load(Constants.baseUrl+"/Popular/PosterImage/"+popularListPos.image).into(binding.image)
            binding.card.setOnClickListener {
                val intent = Intent(context, SeeMore_Activity::class.java)
                intent.putExtra("category",popularListPos.category)
                context.startActivity(intent)

            }
        }

    }

}