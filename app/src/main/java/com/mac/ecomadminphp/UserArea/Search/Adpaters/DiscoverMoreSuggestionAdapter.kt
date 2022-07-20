package com.mac.ecomadminphp.UserArea.Search.Adpaters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomadminphp.Utils.PrefConfig
import com.mac.ecomadminphp.databinding.ActivitySearchBinding
import com.mac.ecomadminphp.databinding.DiscoverMoreLayoutBinding

class DiscoverMoreSuggestionAdapter(
    val context: Context,
    var discoverList: MutableList<String>,
    val mainBinding: ActivitySearchBinding
):RecyclerView.Adapter<DiscoverMoreSuggestionAdapter.myViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        var binding = DiscoverMoreLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val recommendedList = discoverList[position]
        holder.setData(recommendedList)
    }

    override fun getItemCount(): Int {
        var size=0
        if(discoverList.size>=15){
            size=15
        }else{
            size=discoverList.size
        }
        return size
    }


    inner  class myViewHolder(val binding: DiscoverMoreLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(recommendedList: String) {
            binding.dText.text=recommendedList
            binding.dCard.setOnClickListener {
                showDialog()
                performSearch()
                mainBinding.searchBar.setText(recommendedList)
                mainBinding.recyclerSearchSuggestion.visibility=GONE
                mainBinding.recyclerAllProducts.visibility= VISIBLE
                mainBinding.rc.visibility = VISIBLE


                try {
                    var recentList  = java.util.ArrayList<String>()
                    recentList.addAll(PrefConfig.readListFromPref(context))
                    recentList.add(recommendedList)
                    PrefConfig.writeListInPref(context,recentList)
                    recentList.clear()
                } catch (e: Exception) {
                }

            }
        }

    }

    fun showDialog(){

            mainBinding.loading.visibility= VISIBLE
            val handler = Handler()
            handler.postDelayed({mainBinding.loading.visibility= GONE }, 700)



    }

    fun performSearch() {
        mainBinding.searchBar.clearFocus();
        val inputMedthod: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMedthod.hideSoftInputFromWindow(mainBinding.searchBar.windowToken,0)
    }



}