package com.mac.ecomadminphp.UserArea.Activities.Cart

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.mac.ecomadminphp.ClientArea.ProductAvailibility.Pincode_Model
import com.mac.ecomadminphp.FCM.Model.Constant
import com.mac.ecomadminphp.FCM.Model.NotificationData
import com.mac.ecomadminphp.FCM.Model.PushNotification
import com.mac.ecomadminphp.FCM.SendNotification
import com.mac.ecomadminphp.UserArea.Activities.Adapters.Cart_Adapter
import com.mac.ecomadminphp.UserArea.Activities.Model.AddressModel
import com.mac.ecomadminphp.UserArea.Activities.Model.Cart_Model
import com.mac.ecomadminphp.UserArea.Activities.Model.Cart_item_Model
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomadminphp.databinding.ActivityCartBinding
import com.mac.ecomadminphp.databinding.CheckoutAddressLayoutBinding
import com.mac.ecomadminphp.databinding.OnlineCodDialogBinding
import com.mac.ecommerceuserapp.kotlin.Api.Order
import com.mac.ecommerceuserapp.kotlin.Api.RetrofitInterface
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Cart_Activity : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivityCartBinding
    private val fetchCart: String = Constants.baseUrl + "/Cart/fetchCart.php"
    private val getCartItemsUrl: String = Constants.baseUrl + "/Cart/getCartItems.php"
    private val getQuanUrl: String = Constants.baseUrl + "/Cart/getQuantity.php"
    private val getPinCodeUrl = Constants.baseUrl + "/PinCode/getPincode.php"
    private val setAddressUrl = Constants.baseUrl + "/Cart/addAddress.php"
    private val getAddressUrl = Constants.baseUrl + "/Cart/getAddress.php"
    private val placeOrderUrl = Constants.baseUrl + "/Orders/order.php"
    private val emptyCartUrl = Constants.baseUrl + "/Orders/dropCart.php"
    private val updateStockUrl = Constants.baseUrl + "/Orders/updateStock.php"
    private var cartItemList = mutableListOf<Cart_item_Model>()
    private var finalList = mutableListOf<String>()
    private var wholeProductsName =""

    private lateinit var adapter: ArrayAdapter<String>

    private var finalList1 = mutableListOf<String>()

    private var cartList = mutableListOf<String>()
    private lateinit var uid: String
    private lateinit var expectedDate:String
    private var totalCartAmount: Int = 0
    private var toPay: Int = 0
    private lateinit var finaladdress: String
    private var done: Int = 0
    private lateinit var paymentMode: String
    private var available = true
    private var available1 = true
    private lateinit var days: String
    private lateinit var deliveryCharges: String
    private lateinit var deliveryDate: String
    val list = mutableListOf<Cart_Model>()
    var pinCodeList = mutableListOf<Pincode_Model>()
    var orderComplete: Int = 0
    private  var  initvalue:Int =0

    private var productName: String = ""
    private var productImage: String = ""
    private var productPrice: String = ""
    private var productCategory: String = ""
    private var productStock: String = ""
    private val TAG = "RazorPayActivity"
    lateinit var retrofit: Retrofit
    lateinit var retroInterface: RetrofitInterface


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Your Cart"

        //razorpay
        Checkout.preload(this)
        val gson = GsonBuilder().setLenient()
        retrofit=Retrofit.Builder().baseUrl(Constants.razorPayBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson.create()))
            .build()

        retroInterface =retrofit.create(RetrofitInterface::class.java)

        //

        val intent = intent.getStringExtra("paymentMode");

        GetUser()
        CheckProductAlreadyInCart()
        GetPinCode()
        ClickOnCheckOutBtn()


        if (intent != null) {
            paymentMode = intent
            ShowAddressDialog(paymentMode)
        }

    }

    private fun GetAddress(
        dialog: Dialog,
        dialogView: CheckoutAddressLayoutBinding,
        finalList1: MutableList<String>,
        paymentMode: String
    ) {
        val request: StringRequest =
            object : StringRequest(Request.Method.POST, getAddressUrl, { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                val addressList = mutableListOf<AddressModel>()
                val jsonObject = JSONObject(response)
                val success: String = jsonObject.getString("success")
                val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                addressList.clear()
                this.finalList1.clear()
                if (success.equals("1")) {

                    for (item in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(item)
                        val id: String = jsonObject.getString("id")
                        val address: String = jsonObject.getString("address")
                        val addressModel = AddressModel(id, address)

                        addressList.add(0, addressModel)

                    }


                    for (item in 0 until addressList.size) {

                        this.finalList1.add(addressList.get(item).address)

                    }

                    val loadingDialog = ProgressDialog.progressDialog(this, "Loading...")
                    loadingDialog.show()


                    SetupSpinnerAdapter(dialog, dialogView, paymentMode, loadingDialog)


                } else {

                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                }

            }, { error ->

                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

            }) {

                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["addressId"] = "add" + uid
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

    private fun GetUser() {
        val checkUser =
            SharedPref.readFromSharedPref(this, Constants.userPrefName, Constants.defaultPrefValue);
        if (!checkUser.equals("0")) {
            val splitDetails = checkUser?.split(",")
            uid = splitDetails?.get(3) ?: "uid"

        }
    }

    private fun CheckProductAlreadyInCart() {
        cartItemList.clear()
        val dialog = ProgressDialog.progressDialog(this, "Loading...")
        dialog.show()
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchCart,
            Response.Listener { response ->

                try {

                    val jsonObject = JSONObject(response)
                    val success: String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if (success.equals("1")) {


                        for (item in 0 until jsonArray.length()) {

                            val jsonObject: JSONObject = jsonArray.getJSONObject(item)

                            val id: String = jsonObject.getString("id")
                            val pId: String = jsonObject.getString("pId")
                            val quantity: String = jsonObject.getString("quantity")

                            var cartItemModel = Cart_item_Model(id, pId, quantity)
                            cartItemList.add(cartItemModel)
                            cartList.add(pId)

                        }

                        if (cartList.isEmpty()) {
                            binding.userInfo.visibility = VISIBLE
                            dialog.dismiss()
                        } else {
                            binding.userInfo.visibility = GONE

                        }

                        GetCartItems(dialog)


                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()


            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["cartId"] = "cart" + uid
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


    private fun GetCartItems(dialog: Dialog) {
        var i: Int = 0
        while (i < cartList.size) {
            getItems(cartList.get(i), dialog)
            i++
        }

    }

    private fun getItems(pId: String, dialog: Dialog) {
        val request: StringRequest = object : StringRequest(
            Method.POST, getCartItemsUrl,
            Response.Listener { response ->

                getquantity(response, pId, dialog)

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["pId"] = pId
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

    private fun getquantity(response1: String?, pId: String, dialog: Dialog) {

        val request: StringRequest = object : StringRequest(
            Method.POST, getQuanUrl,
            Response.Listener { response ->


                try {

                    val jsonObject1 = JSONObject(response)
                    val success1: String = jsonObject1.getString("success")
                    val jsonArray1: JSONArray = jsonObject1.getJSONArray("data")

                    if (success1.equals("1")) {


                        val jsonObject1: JSONObject = jsonArray1.getJSONObject(0)

                        val id: String = jsonObject1.getString("id")
                        val pId: String = jsonObject1.getString("pId")
                        val quantity: String = jsonObject1.getString("quantity")

                        val jsonObject = JSONObject(response1)
                        val success: String = jsonObject.getString("success")
                        val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                        if (success.equals("1")) {


                            val jsonObject: JSONObject = jsonArray.getJSONObject(0)

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


                            totalCartAmount += quantity.toInt() * pDisPrice.toInt()
                            binding.totalCartValue.setText("Total : ₹" + totalCartAmount.toString() + " /-")

                            var cartModel = Cart_Model(
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
                                pDelivery,
                                quantity,
                                uid
                            )
                            list.add(0, cartModel)
                            done++

                            if (done == cartList.size) {
                                dialog.dismiss()
                                binding.checkoutBtn.visibility = VISIBLE
                                binding.totalCartValue.visibility = VISIBLE
                                SetCartRecycler(dialog)
                                done = 0
                            }

                        }


                    } else {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                    }


                } catch (e: JSONException) {
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["cartId"] = "cart" + uid
                params["pId"] = pId
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

    private fun SetCartRecycler(dialog: Dialog) {

        binding.cartRecycler.setHasFixedSize(true);
        binding.cartRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = Cart_Adapter(this, list, this)
        adapter.notifyDataSetChanged()
        binding.cartRecycler.adapter = adapter


    }


    private fun GetPinCode() {

        val request: StringRequest = StringRequest(Request.Method.POST, getPinCodeUrl, { response ->


            pinCodeList = mutableListOf<Pincode_Model>()
            val jsonObject = JSONObject(response)
            val success: String = jsonObject.getString("success")
            val jsonArray: JSONArray = jsonObject.getJSONArray("data")
            pinCodeList.clear()
            finalList.clear()
            if (success.equals("1")) {

                for (item in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(item)
                    val id: String = jsonObject.getString("id")
                    val pincode: String = jsonObject.getString("pincode")
                    val days: String = jsonObject.getString("days")
                    val deliveryCharge: String = jsonObject.getString("delivery")
                    val pincodeModel = Pincode_Model(id, pincode, days, deliveryCharge)

                    pinCodeList.add(0, pincodeModel)

                }


                for (item in 0 until pinCodeList.size) {

                    finalList.add(pinCodeList.get(item).pincode)

                }

            } else {

                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

            }

        }, { error ->

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

        })

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }


    private fun ClickOnCheckOutBtn() {

        binding.checkoutBtn.setOnClickListener {
            ShowPaymentModeDialog()
        }
    }

    private fun ShowPaymentModeDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogView = OnlineCodDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogView.root)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true)
        dialog.show()


        val online = dialogView.onlineBtn.setOnClickListener {
            paymentMode = "online"
            ShowAddressDialog(paymentMode)
            dialog.dismiss()

        }


        val cod = dialogView.codBtn.setOnClickListener {
            paymentMode = "cod"
            ShowAddressDialog(paymentMode)
            dialog.dismiss()

        }


    }

    private fun ShowAddressDialog(paymentMode: String) {

        val dialog = Dialog(this, R.style.ThemeOverlay_Material_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogView = CheckoutAddressLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(dialogView.root)
        dialog.setCancelable(true)
        dialog.show()

        GetAddress(dialog, dialogView, finalList1, paymentMode)

        val orderBtn = dialogView.orderBtn
        if (paymentMode.equals("online")) {
            orderBtn.setText("Pay")
        }

        orderBtn.setOnClickListener {

            if (available1) {
                if (orderBtn.text.equals("Pay")) {
                    val payonlinedialog = ProgressDialog.progressDialog(this,"Redirecting to payment gateway..")
                    payonlinedialog.show()
                    generateOrderId(toPay.toString(),payonlinedialog)
                } else {

                    MakeOrder("", "")

                }
            } else {
                val ad = AlertDialog.Builder(this@Cart_Activity)
                ad.setTitle("Shipment not available")
                ad.setMessage("Sorry , we do not ship there")

                ad.setNegativeButton("close") { dialogInterface, which ->
                }

                ad.show()
            }


        }


        val addnewaddressBtn = dialogView.addNewAddressBtn.setOnClickListener {
            dialogView.newAddressLayout.visibility = VISIBLE
        }

        val itemTotal = dialogView.txtItemTotal.setText("₹" + totalCartAmount.toString())

        var et_pincode = dialogView.etPicode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (finalList.isEmpty()) {
                    dialogView.etPicode.setError("Shipment is unavailable")
                    dialogView.etPicode.requestFocus()
                    available = false

                }

                if (s.toString().length == 6) {
                    for (item in 0 until finalList.size) {
                        if (!s.toString().equals(finalList[item])) {
                            available = false
                        } else {
                            available = true
                            break

                        }
                    }

                    if (!available) {
                        dialogView.etPicode.setError("Shipment is unavailable at this location !")
                        dialogView.etPicode.requestFocus()
                    }
                } else {
                    available = false
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
        })

        var addAdressBtn = dialogView.setAddressBtn.setOnClickListener {
            finalList1.clear()

            val cusName = dialogView.etFirstname.text.toString().trim()
            val flatNumber = dialogView.etFlatnumber.text.toString().trim()
            val flatName = dialogView.etFlatname.text.toString().trim()
            val roadName = dialogView.etRoad.text.toString().trim()
            val pinCode = dialogView.etPicode.text.toString().trim()
            val cityName = dialogView.etCity.text.toString().trim()
            val stateName = dialogView.etState.text.toString().trim()
            val phoneNumber = dialogView.etPhone.text.toString().trim()



            if (!available) {

                dialogView.etPicode.setError("Shipment is unavailable")
                dialogView.etPicode.requestFocus()

            } else if (cusName.isEmpty()) {

                dialogView.etFirstname.setError("Please enter your first and last name")
                dialogView.etFirstname.requestFocus()


            } else if (flatNumber.isEmpty()) {
                dialogView.etFlatnumber.setError("Please enter your flat number")
                dialogView.etFlatnumber.requestFocus()

            } else if (flatName.isEmpty()) {
                dialogView.etFlatname.setError("Please enter your flat name")
                dialogView.etFlatname.requestFocus()

            } else if (roadName.isEmpty()) {
                dialogView.etRoad.setError("Please enter your road or area name")
                dialogView.etRoad.requestFocus()

            } else if (pinCode.isEmpty()) {
                dialogView.etPicode.setError("Please enter your area Pin Code")
                dialogView.etPicode.requestFocus()
            } else if (cityName.isEmpty()) {
                dialogView.etCity.setError("Please enter your city name")
                dialogView.etCity.requestFocus()

            } else if (stateName.isEmpty()) {
                dialogView.etState.setError("Please enter your state name")
                dialogView.etState.requestFocus()

            } else if (phoneNumber.isEmpty()) {
                dialogView.etPhone.setError("Please enter phone number")
                dialogView.etPhone.requestFocus()


            } else {
                val fullAdress =
                    cusName + "~" + flatNumber + "~" + flatName + "~" + roadName + "~" + cityName + "~" + stateName + "~" + pinCode + "~" + phoneNumber
                val loadingDialog = ProgressDialog.progressDialog(this, "Adding address...")
                loadingDialog.show()
                AddAddress(dialog, fullAdress, loadingDialog, dialogView)
            }

        }

    }


    private fun SetupSpinnerAdapter(
        dialog: Dialog,
        dialogView: CheckoutAddressLayoutBinding,
        paymentMode: String,
        loadingDialog: Dialog
    ) {

        if (!finalList1.isEmpty()) {
            if (!finalList1.get(0).equals("Select address")) {
                finalList1.add(0, "Select address")

            }
        } else {
            finalList1.add(0, "Select address")

        }

        adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item, finalList1
        )


        dialogView.addressSpinner.adapter = adapter
        loadingDialog.dismiss()

        dialogView.addressSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                toPay = 0
                deliveryCharges = ""
                days = ""
                available1 = false


                if (!finalList1[position].trim().equals("Select address")) {
                    dialogView.checkOutOption.visibility = VISIBLE
                    val splitPincode = finalList1[position].split("~")
                    for (item in 0 until finalList.size) {
                        if (!splitPincode[6].trim().equals(finalList[item])) {
                            available1 = false
                        } else {
                            available1 = true
                            break

                        }
                    }

                    if (!available1) {
                        val ad = AlertDialog.Builder(this@Cart_Activity)
                        ad.setTitle("Shipment not available")
                        ad.setMessage("Sorry , we do not ship there")

                        ad.setNegativeButton("close") { dialogInterface, which ->
                        }

                        ad.show()
                    } else {
                        getAdditionInfo(finalList1[position], dialogView, dialog, splitPincode[6])
                    }


                } else {
                    dialogView.checkOutOption.visibility = GONE

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }

    }


    private fun AddAddress(
        dialog: Dialog,
        fullAdress: String,
        loadingDialog: Dialog,
        dialogView: CheckoutAddressLayoutBinding
    ) {

        val request: StringRequest = object : StringRequest(
            Method.POST, setAddressUrl,
            Response.Listener { response ->
                if (response.equals("Address added")) {
                    dialogView.newAddressLayout.visibility = GONE
                    loadingDialog.dismiss()
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                    GetAddress(dialog, dialogView, finalList1, paymentMode)
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                }


            },
            Response.ErrorListener { error ->
                loadingDialog.dismiss()
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["addressId"] = uid
                params["address"] = fullAdress
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

    private fun getAdditionInfo(
        address: String,
        dialogView: CheckoutAddressLayoutBinding,
        dialog: Dialog,
        pincode: String
    ) {

        getDeliveryCharges(pincode, dialog, dialogView, address)


    }

    private fun getDeliveryCharges(
        pincode: String,
        dialog: Dialog,
        dialogView: CheckoutAddressLayoutBinding,
        address: String
    ) {

        for (item in pinCodeList) {
            if (item.pincode.equals(pincode)) {

                days = item.days
                deliveryCharges = item.deliveryCharge
            }
        }

        dialogView.txtDeliveryCharges.setText("₹" + deliveryCharges)
        toPay += totalCartAmount + deliveryCharges.toInt()
        dialogView.etTotalPay.setText("₹" + toPay.toString() + " /-")
        val netDate: Date = Date(System.currentTimeMillis())
        val futureSplit = getFutureDate(netDate, days.toInt()) ?: ""
        deliveryDate = futureSplit.split("-")[1]
        dialogView.txtDate.setText(futureSplit.split("-")[0])
        expectedDate=dialogView.txtDate.text.toString()
        finaladdress = address.replace("~", ", ")
        dialogView.address.setText(finaladdress)
    }


    fun getFutureDate(currentDate: Date?, days: Int): String? {
        val cal = Calendar.getInstance()
        cal.time = currentDate
        cal.add(Calendar.DATE, days)
        val format: DateFormat = SimpleDateFormat("EEE, d MMM")
        var date: Date? = null
        try {
            date = format.parse(format.format(cal.time).toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val millies = cal.timeInMillis
        return format.format(cal.time) + "-" + millies
//        return format.format(cal.time)
    }


    private fun MakeOrder(razorPayOrderId: String, paymentId: String) {

        val dialog = ProgressDialog.progressDialog(this, "Placing your order")
        dialog.show()

        val orderId:String
        if(!razorPayOrderId.equals("")){
             orderId = razorPayOrderId +","+paymentId

        }else{
            val random = Random(System.currentTimeMillis())
            val finalResult = 10000000 + random.nextLong()
             orderId = "ORD" + finalResult.toString().replace("-", "")
        }


        for (item in 0 until cartItemList.size) {


            for (info in 0 until list.size) {
                if (list[info].id.equals(cartItemList[item].pId)) {

                    productName = list.get(info).pNAme
                    productImage = list.get(info).pImage
                    productPrice = list.get(info).pDisPrice
                    productCategory = list.get(info).pCat
                    productStock = list.get(info).pStock

                }
            }
            val productPaymentStatus:String

            val productId = cartItemList.get(item).pId
            val productQuantity = cartItemList.get(item).quantity
            val productAddress = finaladdress
            val productDeliveryDate = deliveryDate
            val productPaymentMode = paymentMode
            if(!razorPayOrderId.equals("")){
                productPaymentStatus = "1"

            }else{
                productPaymentStatus = "0"

            }
            val productTackingStatus = "ordered"
            val productUid = "ODM" + uid

            SetOrdertoServer(
                orderId,
                productId,
                productQuantity,
                productAddress,
                productPaymentMode,
                productDeliveryDate,
                productPaymentStatus,
                productTackingStatus,
                productUid,
                productName,
                productImage,
                productPrice,
                dialog,
                productCategory,
                productStock,item
            )


        }


    }

    private fun SetOrdertoServer(
        orderId: String,
        productId: String,
        productQuantity: String,
        productAddress: String,
        productPaymentMode: String,
        productDeliveryDate: String,
        productPaymentStatus: String,
        productTackingStatus: String,
        productUid: String,
        productName: String,
        productImage: String,
        productPrice: String,
        dialog: Dialog,
        productCategory: String,
        productStock: String,
        item: Int


    ) {



        val request: StringRequest = object : StringRequest(
            Method.POST, placeOrderUrl,
            Response.Listener { response ->
                if (response.equals("Order placed")) {
                    wholeProductsName+="\n${productName}"
                    UpdateStock(productCategory, productQuantity, productStock, productId,productName,item)
                    orderComplete++
                    if (orderComplete == cartItemList.size) {
                        if (response.equals("Order placed")) {
                            EmptyCart(dialog, response)

                        }
                    }


                } else {
                    dialog.dismiss()
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                }


            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["orderId"] = orderId
                params["productId"] = productId
                params["productQuantity"] = productQuantity
                params["productAddress"] = productAddress
                params["productTotalPay"] = productPrice
                params["productPaymentMode"] = productPaymentMode
                params["productDeliveryDate"] = productDeliveryDate
                params["productPaymentStatus"] = productPaymentStatus
                params["productTackingStatus"] = productTackingStatus
                params["productUid"] = productUid
                params["productName"] = productName
                params["productImage"] = productImage
                params["total"] = toPay.toString()
                params["dc"]=deliveryCharges.toString()
                params["value"]=item.toString()
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

    private fun UpdateStock(
        productCategory: String,
        productQuantity: String,
        productStock: String,
        productId: String,
        productName: String,
        item: Int
    ) {

        val value = productStock.toInt() - productQuantity.toInt()


        val request: StringRequest = object : StringRequest(
            Method.POST, updateStockUrl,
            Response.Listener { response ->
                if (response.equals("Updated")) {

                } else {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                }


            },
            Response.ErrorListener { error ->
                try {
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }

            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["category"] = productCategory
                params["value"] = value.toString()
                params["pId"] = productId
                params["productName"] = productName
                params["count"]=item.toString()
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

    private fun EmptyCart(dialog: Dialog, OrderResponse: String?) {

        val request: StringRequest = object : StringRequest(
            Method.POST, emptyCartUrl,
            Response.Listener { response ->
                if (response.equals("Cart empty")) {
                    Notify("Order placed","Your order is placed for "+wholeProductsName+"\nExpected delivery "+expectedDate,uid)
                    Toast.makeText(this, OrderResponse, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    finish()
                    startActivity(Intent(this,OrderPlacedSuccessfully_Activity::class.java))

                } else {
                    dialog.dismiss()
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                }


            },
            Response.ErrorListener { error ->
                try {
                    dialog.dismiss()
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }

            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["cartId"] = "cart$uid"
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

    private fun generateOrderId(amount: String, paymentDialog: Dialog) {

        var map = HashMap<String,String>()
        map["amount"]=amount


        retroInterface
            .getOrderId(map).enqueue(object : Callback<Order> {
                override fun onResponse(call: Call<Order>, response: retrofit2.Response<Order>) {
                    if(response.body()!=null){
                        paymentDialog.dismiss()
                        intiatePayment(amount, response.body()!!)
                    }
                }

                override fun onFailure(call: Call<Order>, t: Throwable) {
                }

            })


    }



    private fun intiatePayment(amount: String, order: Order) {

        val checkout =Checkout()
        checkout.setKeyID(order.getKeyId())
        checkout.setImage(R.drawable.ic_menu_report_image)

        val paymentOptions = JSONObject()
        paymentOptions.put("name","Grocery app")
        paymentOptions.put("amount",amount)
        paymentOptions.put("order_id",order.getOrderId())
        paymentOptions.put("currency","INR")
        paymentOptions.put("description","Pay to grocery app")
        checkout.open(this,paymentOptions)


    }


    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        val placingOrderDialog = ProgressDialog.progressDialog(this,"Placing order hold on...")
        placingOrderDialog.show()

        var map=HashMap<String,String>()
        map["order_id"]=p1!!.orderId
        map["pay_id"]=p1!!.paymentId
        map["signature"]=p1!!.signature

        retroInterface.updateTransaction(map)
            .enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                    if(response.body().equals("success")){
                        Toast.makeText(this@Cart_Activity,"Payment successful",Toast.LENGTH_SHORT).show()
                        placingOrderDialog.dismiss()
                        MakeOrder(p1!!.orderId,p1!!.paymentId)
                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                }

            })



    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this,"Payment unsuccessful",Toast.LENGTH_SHORT).show()
    }

    private  fun Notify(title:String, message:String,topic:String){

        val pushNotification =
            PushNotification(NotificationData(title, message), Constant.TOPIC +topic)
        SendNotification.Send(pushNotification, this)

    }

}