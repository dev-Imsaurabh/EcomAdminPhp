package com.mac.ecomadminphp.UserArea.Search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.ClientArea.EditProducts.Search_Data
import com.mac.ecomadminphp.UserArea.Search.Adpaters.DiscoverMoreSuggestionAdapter
import com.mac.ecomadminphp.UserArea.Search.Adpaters.RecentSuggestionAdapter
import com.mac.ecomadminphp.UserArea.Search.Adpaters.SearchSuggestion_Adapter
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.PrefConfig
import com.mac.ecomadminphp.databinding.ActivitySearchBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet


class Search_Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val getAllProductsUrl: String = Constants.baseUrl + "/Products/getAllProducts.php"
    private var searchTagList = mutableListOf<String>()
    private var discoverList = java.util.ArrayList<String>()
    private var recentList=java.util.ArrayList<String>()
    val searchProductList = mutableListOf<Search_Data>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Search"
        discoverList = ArrayList()
        binding.searchBar.requestFocus()


        getProductData()
        SearchBarOptimization()
        SetDiscoverList();
        SetRecentList();


    }

    private fun SetRecentList() {
        if(!PrefConfig.readListFromPref(this).isEmpty()){
            var list = PrefConfig.readListFromPref(this)
            var set: MutableSet<String> = LinkedHashSet()
            set.addAll(list)
            list.clear()
            list.addAll(set)
            Collections.reverse(list)
            val recentAdapter = RecentSuggestionAdapter(this,list,binding)
            binding.recyclerRecent.setHasFixedSize(true)
            binding.recyclerRecent.layoutManager=LinearLayoutManager(this)
            recentAdapter.notifyDataSetChanged()
            binding.recyclerRecent.adapter=recentAdapter
        }
    }

    private fun SetDiscoverList() {
        if(!PrefConfig.readListFromPrefForDiscover(this).isEmpty()){
            var list =PrefConfig.readListFromPrefForDiscover(this)
            var set: MutableSet<String> = LinkedHashSet()
            set.addAll(list)
            list.clear()
            list.addAll(set)
            val discoverAdapter = DiscoverMoreSuggestionAdapter(this,list,binding)
            binding.recyclerDiscoverMore.setHasFixedSize(true)
            binding.recyclerDiscoverMore.layoutManager=GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false)
            discoverAdapter.notifyDataSetChanged()
            binding.recyclerDiscoverMore.adapter=discoverAdapter
        }

    }

    private fun SearchBarOptimization() {
        var etSearchBar:EditText = binding.searchBar
        etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val handler = Handler()
                handler.postDelayed({ FilterResult(s.toString().trim { it <= ' ' }) }, 300)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = etSearchBar.text.toString().trim()
                if(text.isEmpty()){
                    showDialog()
                    binding.recyclerSearchSuggestion.visibility = GONE
                    binding.recyclerAllProducts.visibility= GONE
                    binding.rc.visibility= GONE
                    binding.recyclerDiscoverMore.visibility= VISIBLE
                    binding.dm.visibility= VISIBLE
                    binding.rs.visibility= VISIBLE
                    binding.recyclerRecent.visibility= VISIBLE

                }else{
                    binding.recyclerSearchSuggestion.visibility = VISIBLE
                    binding.recyclerDiscoverMore.visibility= GONE
                    binding.recyclerRecent.visibility= GONE
                    binding.dm.visibility= GONE
                    binding.rs.visibility= GONE




                }


            }
        })





        binding.searchBar.setOnEditorActionListener(OnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                recentList.clear()
                    if (!textView.text.toString().trim { it <= ' ' }.isEmpty()) {

                        binding.recyclerSearchSuggestion.visibility= GONE
                        binding.recyclerAllProducts.visibility= VISIBLE
                        performSearch()
                        showDialog()
                        showInfo()
                        var set: MutableSet<String> = LinkedHashSet()
                        set.addAll(discoverList)
                        discoverList.clear()
                        discoverList.addAll(set)
                        if(!discoverList.isEmpty()){
                            PrefConfig.writeListInPrefForDiscover(this,discoverList)
                        }

                        try {
                            recentList.addAll(PrefConfig.readListFromPref(this))
                            recentList.add(textView.text.toString())
                            PrefConfig.writeListInPref(this,recentList)
                            recentList.clear()
                        } catch (e: Exception) {
                        }


                    }else{
                        binding.recyclerAllProducts.visibility= GONE

                    }
                }else{
                    binding.recyclerAllProducts.visibility= GONE
            }
                return@OnEditorActionListener true
            false
        })


    }

    private fun FilterResult(text: String) {
        discoverList.clear()

        try {
            val filterList: java.util.ArrayList<Search_Data> =
                java.util.ArrayList<Search_Data>()
            for (item in searchProductList) {
                try {
                    if (item.pNAme.toLowerCase().contains(text.toLowerCase())) {
                        filterList.add(item)
                    } else if (item.pCat.toLowerCase().contains(text.toLowerCase())) {
                        filterList.add(item)
                    } else if (item.pDesc.toLowerCase().contains(text.toLowerCase())) {
                        filterList.add(item)
                    } else if (item.pTags.replace(",","").toLowerCase().contains(text.toLowerCase())) {
                        filterList.add(item)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }




            val search_Adapter = Search_Adapter(this,searchProductList,binding)
            binding.recyclerAllProducts.setAdapter(search_Adapter)
            search_Adapter.FilteredList(filterList)

        } catch (e: Exception) {
            e.printStackTrace()
        }


        val filterSearch = ArrayList<String>()
        for (item2 in searchTagList) {
            if (item2.toLowerCase().contains(text.toLowerCase())) {
                filterSearch.add(item2)
                discoverList.add(item2)
            }

            var set: MutableSet<String> = LinkedHashSet()
            set.addAll(filterSearch)
            filterSearch.clear()
            filterSearch.addAll(set)

            val searchSuggestionAdapter = SearchSuggestion_Adapter(this,searchTagList,binding)
            binding.recyclerSearchSuggestion.setAdapter(searchSuggestionAdapter)
            searchSuggestionAdapter.FilteredList(filterSearch)
        }

    }

    private fun getProductData() {


        val request: StringRequest =
            StringRequest(Request.Method.POST, getAllProductsUrl, { response ->


                val jsonObject = JSONObject(response)
                val success: String = jsonObject.getString("success")
                val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                searchProductList.clear()
                if (success.equals("1")) {

                    for (item in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(item)
                        val id: String = jsonObject.getString("id")
                        val pName: String = jsonObject.getString("pName")
                        val pCat: String = jsonObject.getString("pCat")
                        val pStock: String = jsonObject.getString("pStock")
                        val pPrice: String = jsonObject.getString("pPrice")
                        val pDisPrice: String = jsonObject.getString("pDisPrice")
                        val pRatings: String = jsonObject.getString("pRatings")
                        val pDesc: String = jsonObject.getString("pDesc")
                        val pImage: String = jsonObject.getString("pImage")
                        val pTags: String = jsonObject.getString("pTags")
                        val pDelivery: String = jsonObject.getString("pDelivery")
                        val prodcutModel = Search_Data(
                            id,
                            pName,
                            pCat,
                            pStock,
                            pPrice,
                            pDisPrice,
                            pRatings,
                            pDesc,
                            pImage,
                            pTags,
                            pDelivery
                        )

                        val splitTags = pTags.split(",")
                        for (tags in 0 until splitTags.size) {
                            searchTagList.add(splitTags[tags].trim())
                        }

                        searchProductList.add(0, prodcutModel)

                    }



                    binding.recyclerAllProducts.setHasFixedSize(true)
                    binding.recyclerAllProducts.layoutManager = GridLayoutManager(
                        this, 2,
                        GridLayoutManager.VERTICAL, false
                    )
                    val adapter = Search_Adapter(this, searchProductList, binding)
                    adapter.notifyDataSetChanged()
                    binding.recyclerAllProducts.adapter = adapter

                    binding.recyclerSearchSuggestion.setHasFixedSize(true)
                    binding.recyclerSearchSuggestion.layoutManager = LinearLayoutManager(this)
                    val suggestionAdapter = SearchSuggestion_Adapter(this, searchTagList, binding)
                    suggestionAdapter.notifyDataSetChanged()
                    binding.recyclerSearchSuggestion.adapter = suggestionAdapter


                } else {

                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()


                }

            }, { error ->

                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

            })
        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }

    fun showDialog(){
        if(!binding.searchBar.text.isEmpty()){
            binding.loading.visibility= VISIBLE
            val handler = Handler()
            handler.postDelayed({binding.loading.visibility= GONE }, 700)
        }

    }

    fun performSearch() {
        binding.searchBar.clearFocus();
        val inputMedthod:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMedthod.hideSoftInputFromWindow(binding.searchBar.windowToken,0)
    }

    fun showInfo(){
            binding.rc.visibility = VISIBLE

    }

}