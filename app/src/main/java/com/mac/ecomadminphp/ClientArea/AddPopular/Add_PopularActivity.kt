package com.mac.ecomadminphp.ClientArea.AddPopular

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.ClientArea.categories.Category_Model
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.ActivityAddPopularBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class Add_PopularActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddPopularBinding
    private val getUrl = Constants.baseUrl + "/Categories/getCategory.php"
    private val addPopularUrl = Constants.baseUrl + "/Popular/addPopular.php"
    private var orientationList = mutableListOf<String>()
    private var finalList = mutableListOf<String>()
    private lateinit var category:String
    private lateinit var tablename:String
    private lateinit var bitmap:Bitmap
    private  var encodedImage:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPopularBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GetCategories()
        SetOrientationSpinner()
        ClickOnSelctImgBtn()
        ClickOnAddBtn()


    }



    private fun SetOrientationSpinner() {
        orientationList.add("Select orientation")
        orientationList.add("Horizontal")
        orientationList.add("Vertical")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, orientationList
        )
        binding.alignSpinner.adapter = adapter

        binding.alignSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                tablename = orientationList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }


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
        binding.categorySpinner.adapter = adapter

        binding.categorySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                category = finalList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }
    }


    private fun ClickOnSelctImgBtn() {

        binding.selectImgbtn.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            startActivityForResult(intent, 100)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            } catch (e: Exception) {
            }

            binding.productImage.setImageBitmap(bitmap)

            EncodeImage(bitmap)

        }
    }

    private fun EncodeImage(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val byte = baos.toByteArray()
        encodedImage = Base64.encodeToString(byte, Base64.DEFAULT)

    }


    private fun ClickOnAddBtn() {
        binding.addBtn.setOnClickListener {


            CheckValidation()


        }
    }

    private fun CheckValidation() {

        if(tablename.equals("Select orientation")){
            Toast.makeText(this, "please select orientation", Toast.LENGTH_SHORT).show()

        }else if(encodedImage.equals("")){
            Toast.makeText(this, "please select image", Toast.LENGTH_SHORT).show()

        }else if(category.equals("Select category")){
            Toast.makeText(this, "please select category", Toast.LENGTH_SHORT).show()

        }else{

            val dialog = ProgressDialog.progressDialog(this,"Adding...")
            dialog.show()
            UploadDataToServer(dialog)

        }

    }

    private fun UploadDataToServer(dialog: Dialog) {

        val request: StringRequest =
            object : StringRequest(Request.Method.POST, addPopularUrl, { response ->
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                dialog.dismiss()


            }, { error ->
                dialog.dismiss()


            }) {

                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["category"] = category
                    params["tablename"] = tablename+"Popular"
                    params["image"] = encodedImage

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


}