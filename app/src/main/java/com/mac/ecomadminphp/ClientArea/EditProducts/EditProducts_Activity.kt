package com.mac.ecomadminphp.ClientArea.EditProducts

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.ClientArea.categories.Category_Model
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.ActivityEditProductsBinding
import org.json.JSONArray
import org.json.JSONObject

class EditProducts_Activity : AppCompatActivity() {
    private var finalList = mutableListOf<String>()
    private val getUrl = Constants.baseUrl + "/Categories/getCategory.php"
    private val getProductUrl = Constants.baseUrl + "/Products/getProducts.php"
    private var state:Int=0
    private lateinit var binding:ActivityEditProductsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Edit Products"
        GetCategories()


    }

    private fun GetCategories() {

        val request: StringRequest = StringRequest(Request.Method.POST, getUrl, { response ->


            val categoryList = mutableListOf<Category_Model>()
            val jsonObject = JSONObject(response)
            val success: String = jsonObject.getString("success")
            val jsonArray: JSONArray = jsonObject.getJSONArray("data")
            categoryList.clear()
            finalList.clear()
            if (success.equals("1")) {

                for (item in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(item)
                    val id: String = jsonObject.getString("id")
                    val category: String = jsonObject.getString("category")
                    val categoryModel = Category_Model(id, category)

                    categoryList.add(0, categoryModel)

                }


                for (item in 0 until categoryList.size) {

                    finalList.add(categoryList.get(item).category)

                }

                setSpinner(finalList);

            } else {

                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

            }

        }, { error ->

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

        })

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }

    private fun setSpinner(finalList: MutableList<String>) {
        finalList.add(0, "Select category")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, finalList
        )
        binding.editCatSpinner.adapter = adapter

        binding.editCatSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                if(!finalList[position].equals("Select category")){
                    val dialog = ProgressDialog.progressDialog(this@EditProducts_Activity,"Loading...")
                    dialog.show()
                    getProductData(finalList[position],dialog)

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }
    }

    private fun getProductData(category: String, dialog: Dialog) {


        val request: StringRequest = object:StringRequest(Request.Method.POST, getProductUrl, { response ->


            val productList = mutableListOf<EditProductData>()
            val jsonObject = JSONObject(response)
            val success: String = jsonObject.getString("success")
            val jsonArray: JSONArray = jsonObject.getJSONArray("data")
            productList.clear()
            if (success.equals("1")) {
                dialog.dismiss()

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
                    val prodcutModel = EditProductData(id, pName,pCat,pStock,pPrice,pDisPrice,pRatings,pDesc,pImage,pTags,pDelivery)

                    productList.add(0, prodcutModel)

                }

                binding.editProductRecycler.setHasFixedSize(true)
                binding.editProductRecycler.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
                val adapter = EditProductAdapter(this,productList)
                adapter.notifyDataSetChanged()
                binding.editProductRecycler.adapter =adapter



            } else {
                dialog.dismiss()

                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()


            }

        }, { error ->
            dialog.dismiss()

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

        }){

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["category"] = category
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }

        }

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)



    }

    override fun onResume() {
        super.onResume()
        state++
        if(state>1){

            finish();
            overridePendingTransition( 0, 0);
            startActivity(getIntent());
            overridePendingTransition( 0, 0);

        }


    }

}