package com.mac.ecomadminphp.ClientArea.AddProduct

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.ClientArea.categories.Category_Model
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.ActivityAddProductBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class AddProduct_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private var finalList = mutableListOf<String>()
    private val getUrl = Constants.baseUrl + "/Categories/getCategory.php"
    private val addProductUrl = Constants.baseUrl + "/Products/addProduct.php"
    private lateinit var pName: String
    private lateinit var pCat: String
    private lateinit var pStock: String
    private lateinit var pPrice: String
    private lateinit var pDisPrice: String
    private lateinit var pDisc: String
    private lateinit var pId: String
    private lateinit var pDelivery: String
    private lateinit var pTags: String
    private lateinit var bitmap: Bitmap
    private var encodedImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Add Products"
        GetCategories()
        ClickOnSelctImgBtn()
        ClickOnAddProductBtn()


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
                pCat = finalList[position]
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


    private fun ClickOnAddProductBtn() {
        binding.addProductBtn.setOnClickListener {
            checkValidation();
        }
    }

    private fun checkValidation() {
        pName = binding.productName.text.toString().trim()
        pStock = binding.productStock.text.toString().trim()
        pPrice = binding.productPrice.text.toString().trim()
        pDisPrice = binding.productDisPrice.text.toString().trim()
        pDisc = binding.productDescription.text.toString().trim()
        pDelivery = binding.productDelivery.text.toString().trim()
        pTags = binding.productTags.text.toString().trim()

        if (pCat.equals("Select category")) {
            Toast.makeText(this, "Please select Category", Toast.LENGTH_SHORT).show()

        } else if (encodedImage.equals("")) {
            Toast.makeText(this, "Please select product image", Toast.LENGTH_SHORT).show()

        } else if (pName.isEmpty()) {
            binding.productName.setError("Please enter product name")
            binding.productName.requestFocus()

        } else if (pStock.isEmpty()) {
            binding.productStock.setError("Please enter product stock")
            binding.productStock.requestFocus()

        } else if (pPrice.isEmpty()) {
            binding.productPrice.setError("Please enter product price")
            binding.productPrice.requestFocus()

        } else if (pDisPrice.isEmpty()) {
            binding.productDisPrice.setError("Please enter product discount price")
            binding.productDisPrice.requestFocus()

        } else if(pDelivery.isEmpty()){
            binding.productDelivery.setError("Please enter product delivery charge")
            binding.productDelivery.requestFocus()

        }else if(pTags.isEmpty()){
            binding.productTags.setError("Please enter atleast 2 product tags")
            binding.productTags.requestFocus()
        } else if (pDisc.isEmpty()) {
            binding.productDescription.setError("Please enter product description")
            binding.productDescription.requestFocus()

        } else {
            pId = pName.substring(0, 3) + System.currentTimeMillis().toString().substring(7, 13)
            val dialog = ProgressDialog.progressDialog(this,"Adding product...")
            dialog.show()
            UploadDataToServer(dialog)
        }

    }

    private fun UploadDataToServer(dialog: Dialog) {

        val request: StringRequest =
            object : StringRequest(Request.Method.POST, addProductUrl, { response ->
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                dialog.dismiss()


            }, { error ->
                dialog.dismiss()


            }) {

                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["id"] = pId
                    params["pName"] = pName
                    params["pCat"] = pCat
                    params["pStock"] = pStock
                    params["pPrice"] = pPrice
                    params["pDisPrice"] = pDisPrice
                    params["pDelivery"] = pDelivery
                    params["pTags"] = pTags
                    params["pDesc"] = pDisc
                    params["pImage"] = encodedImage
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