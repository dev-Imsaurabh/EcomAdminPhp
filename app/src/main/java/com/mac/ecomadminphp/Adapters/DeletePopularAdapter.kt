package com.mac.ecomadminphp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.ClientArea.AddPopular.Delete_PopularActivity
import com.mac.ecomadminphp.UserArea.Activities.ViewProduct.SeeMore_Activity
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.ActivityAddPopularBinding
import com.mac.ecomadminphp.databinding.DeletePopularItemLayoutBinding
import com.mac.ecomadminphp.databinding.PopularVerticalLaypoutBinding
import com.squareup.picasso.Picasso

class DeletePopularAdapter(val context:Context, val popularVerticalList:MutableList<PopularModel>,val activity:Delete_PopularActivity):RecyclerView.Adapter<DeletePopularAdapter.myViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val binding = DeletePopularItemLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        val popularListPos = popularVerticalList[position]
        holder.setData(popularListPos)

    }

    override fun getItemCount(): Int {

        return popularVerticalList.size

    }



    inner class myViewHolder(val binding: DeletePopularItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(popularListPos: PopularModel) {

            binding.text.setText("Long press to delete "+"(${popularListPos.tablename.substring(0,3)})")

            Picasso.get().load(Constants.baseUrl+"/Popular/PosterImage/"+popularListPos.image).into(binding.image)
           binding.deleteCard.setOnLongClickListener {
               sendData(popularListPos)

               return@setOnLongClickListener true
           }
        }

    }

    private fun sendData(popularListPos: PopularModel) {

         val deletePopularUrl = Constants.baseUrl + "/Popular/deletePopular.php";


        val request: StringRequest =
            object : StringRequest(Request.Method.POST, deletePopularUrl, { response ->
                Toast.makeText(context,response, Toast.LENGTH_SHORT).show()
                activity.finish();
                activity.overridePendingTransition(0, 0);
                activity.startActivity(activity.intent);
                activity.overridePendingTransition(0, 0);

            }, { error ->


            }) {

                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["id"] = popularListPos.id
                    params["tablename"] = popularListPos.tablename
                    params["image"] = popularListPos.image

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