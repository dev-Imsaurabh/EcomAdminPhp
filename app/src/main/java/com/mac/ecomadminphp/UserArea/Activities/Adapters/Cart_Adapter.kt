package com.mac.ecomadminphp.UserArea.Activities.Adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.UserArea.Activities.Cart.Cart_Activity
import com.mac.ecomadminphp.UserArea.Activities.Model.Cart_Model
import com.mac.ecomadminphp.UserArea.Activities.ViewProduct.SeeProduct_Activity
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.CartItemLayoutBinding
import com.squareup.picasso.Picasso

class Cart_Adapter(val context:Context, val cartList:List<Cart_Model> ,val activity: Cart_Activity) :RecyclerView.Adapter<Cart_Adapter.myViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding = CartItemLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)


    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val productPos = cartList[position]
        holder.setData(productPos)
    }

    override fun getItemCount(): Int {

        return cartList.size
    }


    inner class myViewHolder(val binding: CartItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setData(productList: Cart_Model){



            if(productList.quantity.toInt()==0){
                RemoveFromCart(productList)
            }

            binding.removeFromCartBtn.setOnClickListener {
                RemoveFromCart(productList)

            }



            if(productList.quantity.toInt()>=productList.pStock.toInt()){
                binding.incBtn.isEnabled=false
                binding.proStock.setText("We have just "+productList.quantity+" item")

                binding.proStock.visibility = VISIBLE

            }else if(productList.pStock.toInt()-productList.quantity.toInt()<5){
                binding.incBtn.isEnabled = true
                binding.proStock.visibility = VISIBLE
                binding.proStock.setText("only "+(productList.pStock.toInt()-productList.quantity.toInt()).toString() + " more left !\norder now !")
            } else{
                binding.incBtn.isEnabled=true
                binding.proStock.visibility = GONE

            }

            binding.incBtn.setOnClickListener {
                val previousQuantity = productList.quantity.toInt()
                val newQuantity = previousQuantity +1

                if (productList.quantity.toInt()>=1){
                    UpdateQuantity(productList,newQuantity)

                }

            }

            binding.decBtn.setOnClickListener {
                val previousQuantity = productList.quantity.toInt()
                val newQuantity = previousQuantity -1

                if (productList.quantity.toInt()!=1){
                    UpdateQuantity(productList,newQuantity)

                }            }



            Picasso.get().load(Constants.baseUrl+"/Products/ProductImages/"+productList.pImage).into(binding.proImage)
            binding.proName.text= productList.pNAme
//            binding.price.text = "₹"+productList.pPrice
            binding.proPrice.text = "₹"+productList.pDisPrice
            binding.quantity.text = productList.quantity
//            binding.price.setPaintFlags(binding.discount.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

//
//            val originalPrice:Double = productList.pPrice.toDouble()
//            val sellingPrice:Double = productList.pDisPrice.toDouble()
//            val difference:Double = originalPrice-sellingPrice
//            val percentage = ((difference/originalPrice)*100).toInt()
//            binding.discount.text = percentage.toString()+"% off"

            binding.card.setOnClickListener {
                val intent = Intent(context,SeeProduct_Activity::class.java)
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
                activity.finish()
            }


        }


    }

    private fun UpdateQuantity(productList: Cart_Model, newQuantity: Int) {

        val url = Constants.baseUrl+"/Cart/updateQuantity.php"

        val dialog = ProgressDialog.progressDialog(context,"Loading...")
        dialog.show()
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->

                if(response.equals("Quantity updated")){
                    dialog.dismiss()
                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                    activity.startActivity(activity.intent);
                    activity.overridePendingTransition(0, 0);
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                dialog.dismiss()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["quantity"] = newQuantity.toString()
                params["pId"]=productList.id
                params["cartId"] = "cart" + productList.uid
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

    private fun RemoveFromCart(productList: Cart_Model) {

        val url = Constants.baseUrl+"/Cart/deleteCart.php"

        val dialog = ProgressDialog.progressDialog(context,"Loading...")
        dialog.show()
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->

                if(response.equals("Item removed")){
                    dialog.dismiss()
                    Toast.makeText(context,response,Toast.LENGTH_SHORT).show()
                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                    activity.startActivity(activity.intent);
                    activity.overridePendingTransition(0, 0);
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                dialog.dismiss()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["pId"]=productList.id
                params["cartId"] = "cart" + productList.uid
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