package com.mac.ecomadminphp.ClientArea.categories

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.ActivityAddCategoriesBinding
import org.json.JSONArray
import org.json.JSONObject

class Add_Categories_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCategoriesBinding
    private lateinit var  adapter:ArrayAdapter<String>
    private lateinit var deleteAllProduct:String
    private  var finalList= mutableListOf<String>()


    private val setUrl = Constants.baseUrl+"/Categories/addCategory.php"
    private val getUrl = Constants.baseUrl+"/Categories/getCategory.php"
    private val deleteUrl = Constants.baseUrl+"/Categories/deleteCategory.php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Add Category"


        ClickOnAddBtn()
        GetCategories()
        DeleteCategories()



    }

    private fun DeleteCategories() {
        binding.categoryListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, postion, id->
                var categoryName = finalList[postion]

                val adialog:AlertDialog.Builder = AlertDialog.Builder(this)
                adialog.setTitle("Warning")
                adialog.setMessage("Do you want to also delete all product associated with it ?")

                adialog.setPositiveButton("yes"){dialogInterface, which ->
                    deleteAllProduct="1"
                    SendRequestToDelete(categoryName);

                }
                adialog.setNegativeButton("No"){dialogInterface,which->
                    deleteAllProduct="0"
                    SendRequestToDelete(categoryName);

                }
                adialog.show()

                true
            }
    }

    private fun SendRequestToDelete(categoryName: String) {

        val request:StringRequest =object :StringRequest(Request.Method.POST,deleteUrl, { response ->

            if(response.equals("Category deleted")){
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                GetCategories()
            }else{
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()

            }


        }, { error ->
            Toast.makeText(this,error.message,Toast.LENGTH_SHORT).show()


        }){
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["category"] = categoryName
                params["value"] = deleteAllProduct
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


    private fun ClickOnAddBtn() {
        binding.addBtn.setOnClickListener {

            if(!binding.etCategory.text.toString().isEmpty()){
                val dialog = ProgressDialog.progressDialog(this,"Adding category...")
                dialog.show()

                AddCategory(dialog);

            }else{
                Toast.makeText(this,"Please enter category name",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun AddCategory(dialog: Dialog) {
        val request:StringRequest =object :StringRequest(Request.Method.POST,setUrl, { response ->

            if(response.equals("Category added")){
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                GetCategories()
                binding.etCategory.setText("")
                dialog.dismiss()
            }else{
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                dialog.dismiss()


            }


        }, { error ->
            Toast.makeText(this,error.message,Toast.LENGTH_SHORT).show()
            dialog.dismiss()


        }){
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["category"] = binding.etCategory.text.toString().trim()
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

    private fun GetCategories() {

        val request:StringRequest = StringRequest(Request.Method.POST,getUrl ,{ response->


            val categoryList = mutableListOf<Category_Model>()
            val jsonObject =JSONObject(response)
            val success:String = jsonObject.getString("success")
            val jsonArray:JSONArray = jsonObject.getJSONArray("data")
            categoryList.clear()
            finalList.clear()
            if(success.equals("1")){

                for (item in 0 until jsonArray.length()){
                    val jsonObject:JSONObject = jsonArray.getJSONObject(item)
                    val id:String = jsonObject.getString("id")
                    val category:String = jsonObject.getString("category")
                    val categoryModel = Category_Model(id,category)

                    categoryList.add(0,categoryModel)

                }


                for(item in 0 until categoryList.size){

                    finalList.add(categoryList.get(item).category)

                }


                adapter = ArrayAdapter(this, R.layout.simple_list_item_1, finalList)
                adapter.notifyDataSetChanged()
                binding.categoryListView.adapter = adapter
            }else{

                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()

            }

        },{ error->

            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()

        })

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }
}