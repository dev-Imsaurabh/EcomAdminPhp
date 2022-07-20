package com.mac.ecomadminphp.ClientArea.ProductAvailibility

import android.R
import android.app.AlertDialog
import android.app.Dialog
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
import com.mac.ecomadminphp.databinding.ActivityAddPinCodeBinding
import org.json.JSONArray
import org.json.JSONObject

class Add_PinCode : AppCompatActivity() {
    private lateinit var binding: ActivityAddPinCodeBinding
    private lateinit var  adapter:ArrayAdapter<String>
    private  var finalList= mutableListOf<String>()


    private val setUrl = Constants.baseUrl+"/PinCode/addPincode.php"
    private val getUrl = Constants.baseUrl+"/PinCode/getPincode.php"
    private val deleteUrl = Constants.baseUrl+"/PinCode/deletePincode.php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddPinCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Add Pin Code"


        ClickOnAddBtn()
        GetPinCode()
        DeletePinCode()



    }

    private fun DeletePinCode() {
        binding.pinCodeListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, postion, id->
                val split= finalList[postion].split("-")
                var PinCodeName = split[0].trim()

                val adialog:AlertDialog.Builder = AlertDialog.Builder(this)
                adialog.setTitle("Warning")
                adialog.setMessage("Do you want to delete this Pin Code")

                adialog.setPositiveButton("yes"){dialogInterface, which ->
                    SendRequestToDelete(PinCodeName);

                }
                adialog.setNegativeButton("No"){dialogInterface,which->
                }
                adialog.show()

                true
            }
    }

    private fun SendRequestToDelete(pincode: String) {

        val request:StringRequest =object :StringRequest(Request.Method.POST,deleteUrl, { response ->

            if(response.equals("Pincode deleted")){
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                GetPinCode()
            }else{
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()

            }


        }, { error ->
            Toast.makeText(this,error.message,Toast.LENGTH_SHORT).show()


        }){
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["pincode"] = pincode
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

            if(!binding.etPincode.text.toString().isEmpty()&&!binding.etDays.text.toString().isEmpty()&&!binding.etDeliveryCharge.text.isEmpty()){
                val dialog = ProgressDialog.progressDialog(this,"Adding Pin Code...")
                dialog.show()
                AddPinCode(dialog);

            }else{
                Toast.makeText(this,"Please enter all details",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun AddPinCode(dialog: Dialog) {
        val request:StringRequest =object :StringRequest(Request.Method.POST,setUrl, { response ->

            if(response.equals("Pincode added")){
                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                GetPinCode()
                binding.etPincode.setText("")
                binding.etDays.setText("")
                binding.etDeliveryCharge.setText("")
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
                params["pincode"] = binding.etPincode.text.toString().trim()
                params["days"] = binding.etDays.text.toString().trim()
                params["delivery"] = binding.etDeliveryCharge.text.toString().trim()
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

    private fun GetPinCode() {

        val request:StringRequest = StringRequest(Request.Method.POST,getUrl ,{ response->


            val pinCodeList = mutableListOf<Pincode_Model>()
            val jsonObject =JSONObject(response)
            val success:String = jsonObject.getString("success")
            val jsonArray:JSONArray = jsonObject.getJSONArray("data")
            pinCodeList.clear()
            finalList.clear()
            if(success.equals("1")){

                for (item in 0 until jsonArray.length()){
                    val jsonObject:JSONObject = jsonArray.getJSONObject(item)
                    val id:String = jsonObject.getString("id")
                    val pincode:String = jsonObject.getString("pincode")
                    val days:String = jsonObject.getString("days")
                    val deliveryCharge:String = jsonObject.getString("delivery")
                    val pincodeModel = Pincode_Model(id,pincode,days,deliveryCharge)

                    pinCodeList.add(0,pincodeModel)

                }


                for(item in 0 until pinCodeList.size){

                    finalList.add(pinCodeList.get(item).pincode+"-"+pinCodeList.get(item).days+" days"+"-"+pinCodeList.get(item).deliveryCharge+" rs delivery charge")

                }


                adapter = ArrayAdapter(this, R.layout.simple_list_item_1, finalList)
                adapter.notifyDataSetChanged()
                binding.pinCodeListView.adapter = adapter
            }else{

                Toast.makeText(this,response,Toast.LENGTH_SHORT).show()

            }

        },{ error->

            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()

        })

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }
}