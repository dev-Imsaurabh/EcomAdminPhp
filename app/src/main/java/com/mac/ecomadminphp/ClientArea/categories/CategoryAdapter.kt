package com.mac.ecomadminphp.ClientArea.categories

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.UserArea.Activities.ViewProduct.SeeMore_Activity
import com.mac.ecomadminphp.databinding.CategoryItemLayoutBinding
import com.squareup.picasso.Picasso

class CategoryAdapter(val context:Context,val categoryList:List<Category_Model>) :RecyclerView.Adapter<CategoryAdapter.myViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding = CategoryItemLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)


    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val categoryPos = categoryList[position]
        holder.setData(categoryPos)
    }

    override fun getItemCount(): Int {

        return categoryList.size
    }


    inner class myViewHolder(val binding: CategoryItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setData(categoryList: Category_Model){

            binding.catName.text = categoryList.category
            Picasso.get().load("https://source.unsplash.com/random/500x500/?${categoryList.category}=1").into(binding.catImg)
            binding.card.setOnClickListener {
                val intent = Intent(context, SeeMore_Activity::class.java)
                intent.putExtra("category",categoryList.category)
                context.startActivity(intent)
            }

        }


    }
}