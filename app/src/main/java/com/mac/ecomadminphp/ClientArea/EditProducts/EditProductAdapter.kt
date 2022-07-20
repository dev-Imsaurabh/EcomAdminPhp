package com.mac.ecomadminphp.ClientArea.EditProducts


import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.EditProductItemLayoutBinding
import com.squareup.picasso.Picasso

class EditProductAdapter(val context:Context, val productList:List<EditProductData>) :RecyclerView.Adapter<EditProductAdapter.myViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding = EditProductItemLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)


    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val productPos = productList[position]
        holder.setData(productPos)
    }

    override fun getItemCount(): Int {

        return productList.size
    }


    inner class myViewHolder(val binding: EditProductItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setData(productList: EditProductData){

            Picasso.get().load(Constants.baseUrl+"/Products/ProductImages/"+productList.pImage).into(binding.image)
            binding.title.text= productList.pNAme
            binding.price.text = "₹"+productList.pPrice
            binding.discountPrice.text = "Just ₹"+productList.pDisPrice
            binding.price.setPaintFlags(binding.discount.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)


            val originalPrice:Double = productList.pPrice.toDouble()
            val sellingPrice:Double = productList.pDisPrice.toDouble()
            val difference:Double = originalPrice-sellingPrice
            val percentage = ((difference/originalPrice)*100).toInt()
            binding.discount.text = percentage.toString()+"% off"

            binding.card.setOnClickListener {
                val intent = Intent(context,UpdateProduct_Activity::class.java)
                intent.putExtra("id",productList.id)
                intent.putExtra("pName",productList.pNAme)
                intent.putExtra("pCat",productList.pCat)
                intent.putExtra("pStock",productList.pStock)
                intent.putExtra("pPrice",productList.pPrice)
                intent.putExtra("pDisPrice",productList.pDisPrice)
                intent.putExtra("pDesc",productList.pDesc)
                intent.putExtra("pImage",productList.pImage)
                intent.putExtra("pTags",productList.pTags)
                intent.putExtra("pDelivery",productList.pDelivery)
                context.startActivity(intent)
            }


        }


    }
}