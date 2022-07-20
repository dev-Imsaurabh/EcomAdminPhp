package com.mac.ecomadminphp.UserArea.Search.Adpaters

import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.mac.ecomadminphp.Utils.PrefConfig
import com.mac.ecomadminphp.databinding.ActivitySearchBinding
import com.mac.ecomadminphp.databinding.SearchItemLayoutBinding

class SearchSuggestion_Adapter(
    val context: Context,
    var suggestionList: MutableList<String>,
    val mainBinding: ActivitySearchBinding

):RecyclerView.Adapter<SearchSuggestion_Adapter.myViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        var binding = SearchItemLayoutBinding.inflate(LayoutInflater.from(context))
        return myViewHolder(binding)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val recommendedList = suggestionList[position]
        holder.setData(recommendedList)
    }

    override fun getItemCount(): Int {
        return suggestionList.size
    }

    fun FilteredList(filterSearch: ArrayList<String>) {

        suggestionList = filterSearch
        notifyDataSetChanged()


    }


    inner  class myViewHolder(val binding: SearchItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(recommendedList: String) {
            binding.stext.text=recommendedList
            binding.stext.setOnClickListener {
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
        if(!mainBinding.searchBar.text.isEmpty()){
            mainBinding.loading.visibility= VISIBLE
            val handler = Handler()
            handler.postDelayed({mainBinding.loading.visibility= GONE }, 700)
        }


    }

    fun performSearch() {
        mainBinding.searchBar.clearFocus();
        val inputMedthod: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMedthod.hideSoftInputFromWindow(mainBinding.searchBar.windowToken,0)
    }



}