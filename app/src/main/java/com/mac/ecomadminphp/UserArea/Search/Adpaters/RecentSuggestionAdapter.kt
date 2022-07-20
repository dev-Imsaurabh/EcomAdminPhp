package com.mac.ecomadminphp.UserArea.Search.Adpaters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomadminphp.databinding.ActivitySearchBinding
import com.mac.ecomadminphp.databinding.RecentSearchItemLayoutBinding

class RecentSuggestionAdapter(
    val context: Context,
    var recentList: MutableList<String>,
    val mainBinding: ActivitySearchBinding
):RecyclerView.Adapter<RecentSuggestionAdapter.myViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        var binding = RecentSearchItemLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val recommendedList = recentList[position]
        holder.setData(recommendedList)
    }

    override fun getItemCount(): Int {
        var size=0
        if(recentList.size>=10){
            size=10
        }else{
            size=recentList.size
        }
        return size
    }

    fun FilteredList(filterSearch: ArrayList<String>) {

        recentList = filterSearch
        notifyDataSetChanged()


    }


    inner  class myViewHolder(val binding: RecentSearchItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(recommendedList: String) {
            binding.recentStext.text=recommendedList
            binding.recentStext .setOnClickListener {
                showDialog()
                performSearch()
                mainBinding.searchBar.setText(recommendedList)
                mainBinding.recyclerSearchSuggestion.visibility=GONE
                mainBinding.recyclerAllProducts.visibility= VISIBLE
                mainBinding.rc.visibility = VISIBLE


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