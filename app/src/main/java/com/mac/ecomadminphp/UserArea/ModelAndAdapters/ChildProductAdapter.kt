package com.mac.ecomadminphp.UserArea.ModelAndAdapters


import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomadminphp.ClientArea.EditProducts.UpdateProduct_Activity
import com.mac.ecomadminphp.UserArea.Activities.ViewProduct.SeeProduct_Activity
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.ProductCardLayoutBinding
import com.squareup.picasso.Picasso

class ChildProductAdapter(val context:Context, val productList:List<ChildeProductData>) :RecyclerView.Adapter<ChildProductAdapter.myViewHolder>(){
    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding = ProductCardLayoutBinding.inflate(LayoutInflater.from(context))
       return myViewHolder(binding)


    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val productPos = productList[position]
        holder.setData(productPos)
    }

    override fun getItemCount(): Int {

        var size = productList.size;
        if(productList.size<size){
            size=productList.size
        }

        return size
    }


    inner class myViewHolder(val binding: ProductCardLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setData(productList: ChildeProductData){

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

            binding.
            card.setOnClickListener {
                val intent = Intent(context, SeeProduct_Activity::class.java)
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