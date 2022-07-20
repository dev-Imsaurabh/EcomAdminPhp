package com.mac.ecomadminphp.ClientArea.EditProducts

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.databinding.ActivityUpdateProductBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class UpdateProduct_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProductBinding
    private val updateProductUrl = Constants.baseUrl + "/Products/updateProduct.php"
    private val deleteProductUrl = Constants.baseUrl + "/Products/deleteProduct.php"
    private lateinit var pName: String
    private lateinit var pCat: String
    private lateinit var pStock: String
    private lateinit var pPrice: String
    private lateinit var pDisPrice: String
    private lateinit var pDesc: String
    private lateinit var pId: String
    private lateinit var pDelivery: String
    private lateinit var pTags: String
    private lateinit var bitmap: Bitmap
    private lateinit var pImage: String
    private lateinit var previousImageId: String
    private var encodedImage: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Update product"


        setAllIntent();
        ClickOnSelctImgBtn()
        ClickOnUpdateProductBtn()
        ClickOnDeleteFabBtn()


    }

    private fun ClickOnDeleteFabBtn() {

        binding.deleteProductFab.setOnClickListener {

            val adialog: AlertDialog.Builder = AlertDialog.Builder(this)
            adialog.setTitle("Delete")
            adialog.setMessage("Do you want to delete this product ?")

            adialog.setPositiveButton("yes"){dialogInterface, which ->

                val dialog = ProgressDialog.progressDialog(this,"Deleting product...")
                dialog.show()

                DeleteProduct(dialog);


            }
            adialog.setNegativeButton("No"){dialogInterface,which->

            }
            adialog.show()



        }




    }

    private fun DeleteProduct(dialog: Dialog) {

        val request: StringRequest =
            object : StringRequest(Request.Method.POST, deleteProductUrl, { response ->
                if(response.equals("Product deleted")){

                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    finish()

                }else{
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    finish()

                }



            }, { error ->
                dialog.dismiss()
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                finish()



            }) {

                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["id"] = pId
                    params["imageId"] = previousImageId
                    params["category"] = pCat
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

    private fun setAllIntent() {
        pId = intent.getStringExtra("id").toString()
        pName = intent.getStringExtra("pName").toString()
        pCat = intent.getStringExtra("pCat").toString()
        pStock = intent.getStringExtra("pStock").toString()
        pPrice = intent.getStringExtra("pPrice").toString()
        pDisPrice = intent.getStringExtra("pDisPrice").toString()
        pDesc = intent.getStringExtra("pDesc").toString()
        pImage = intent.getStringExtra("pImage").toString()
        pTags = intent.getStringExtra("pTags").toString()
        pDelivery = intent.getStringExtra("pDelivery").toString()
        previousImageId=pImage

        binding.productName.setText(pName)
        binding.productStock.setText(pStock)
        binding.productPrice.setText(pPrice)
        binding.productDisPrice.setText(pDisPrice)
        binding.productDescription.setText(pDesc)
        binding.productTags.setText(pTags)
        binding.productDelivery.setText(pDelivery)
        Picasso.get().load(Constants.baseUrl + "/Products/ProductImages/" + pImage)
            .into(binding.productImage)


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


    private fun ClickOnUpdateProductBtn() {
        binding.updateProductBtn.setOnClickListener {
            checkValidation();
        }
    }

    private fun checkValidation() {
        pName = binding.productName.text.toString().trim()
        pStock = binding.productStock.text.toString().trim()
        pPrice = binding.productPrice.text.toString().trim()
        pDisPrice = binding.productDisPrice.text.toString().trim()
        pDesc = binding.productDescription.text.toString().trim()
        pDelivery = binding.productDelivery.text.toString().trim()
        pTags = binding.productTags.text.toString().trim()

        if (pName.isEmpty()) {
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
        } else if (pDesc.isEmpty()) {
            binding.productDescription.setError("Please enter product description")
            binding.productDescription.requestFocus()

        } else {
            val dialog = ProgressDialog.progressDialog(this, "Updating product...")
            dialog.show()
            UploadDataToServer(dialog)
        }

    }

    private fun UploadDataToServer(dialog: Dialog) {

        val request: StringRequest =
            object : StringRequest(Request.Method.POST, updateProductUrl, { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                encodedImage=""
                dialog.dismiss()
                finish()


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
                    params["pDesc"] = pDesc
                    params["pImage"] = encodedImage
                    params["previousImageId"] = previousImageId
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