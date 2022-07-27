package com.mac.ecomadminphp


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import com.mac.ecomadminphp.Adapters.Pager
import com.mac.ecomadminphp.Authentication.EmailVerification_Activity
import com.mac.ecomadminphp.Authentication.Login_Activity
import com.mac.ecomadminphp.ClientArea.ClientAreaActivity
import com.mac.ecomadminphp.ClientArea.categories.CategoryAdapter
import com.mac.ecomadminphp.ClientArea.categories.Category_Model
import com.mac.ecomadminphp.FCM.Model.Constant.TOPIC
import com.mac.ecomadminphp.FCM.Model.NotificationData
import com.mac.ecomadminphp.FCM.Model.PushNotification
import com.mac.ecomadminphp.FCM.SendNotification
import com.mac.ecomadminphp.UserArea.Activities.Additional.Contact_us_Activity
import com.mac.ecomadminphp.UserArea.Activities.Additional.My_Account_Activity
import com.mac.ecomadminphp.UserArea.Activities.Additional.Notification_Activity
import com.mac.ecomadminphp.UserArea.Activities.Additional.Privacy_Policies_Activity
import com.mac.ecomadminphp.UserArea.Activities.Cart.Cart_Activity
import com.mac.ecomadminphp.UserArea.Activities.Orders.My_Orders_Activity
import com.mac.ecomadminphp.UserArea.Search.Search_Activity
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.Utils.ProgressDialog
import com.mac.ecomadminphp.Utils.SharedPref
import com.mac.ecomadminphp.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private  var finalList= mutableListOf<String>()
    private val getUrl = Constants.baseUrl+"/Categories/getCategory.php"
    private val fetchCart: String = Constants.baseUrl + "/Cart/fetchCart.php"
    private val adminUrl: String = Constants.baseUrl +"/adminAccess.php"
    private var cartList = mutableListOf<String>()
    private lateinit var  uid:String
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var navigationView:NavigationView
    private lateinit var drawerLayout: DrawerLayout




    private lateinit var binding: ActivityMainBinding
    private val fetchUrl: String = Constants.baseUrl + "/fetchUser.php"
    private val sendMail: String = Constants.baseUrl + "/sendMail.php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        SetupNavigation()
        checkUser();
        ClickOnLogoutBtn();
        checkVerification();
        ClickOnVerifybtn();
        GetCategories()
        SetupViewPager();
        ClickOnCartFABbtn()
        GetCartItemCount()
        CheckEmail()
        ClickOnSearchBtn()


    }

    private fun SetupNavigation() {
        navigationView=binding.NavigationDrawer
        drawerLayout=binding.drawerLayout

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menu:MenuItem->
            drawerLayout.close()

            when(menu.itemId){

                R.id.nav_profile->startActivity(Intent(this,My_Account_Activity::class.java))
                R.id.nav_order-> startActivity(Intent(this,My_Orders_Activity::class.java))
                R.id.nav_cart-> startActivity(Intent(this,Cart_Activity::class.java))
                R.id.nav_noti-> startActivity(Intent(this,Notification_Activity::class.java))
                R.id.nav_help-> startActivity(Intent(this,Contact_us_Activity::class.java))
                R.id.nav_pp-> startActivity(Intent(this,Privacy_Policies_Activity::class.java))
                R.id.nav_logout-> LogoutUser()
            }


        return@OnNavigationItemSelectedListener true

        })
    }

    private fun ClickOnSearchBtn() {
        binding.searchBar.setOnClickListener{
            val intent=Intent(this,Search_Activity::class.java)
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private fun CheckEmail() {
        val isEmailVerified = SharedPref.readFromSharedPref(this,"verifyEmail","0")
        if(!isEmailVerified.equals("1")){
            binding.dview1.visibility = GONE
            binding.dview2.visibility = GONE
        }
    }

    private fun ClickOnCartFABbtn() {
        binding.cartFabBtn.setOnClickListener {
            startActivity(Intent(this,Cart_Activity::class.java))
        }
    }


    private fun ClientAreaConfig(email: String) {


        val request: StringRequest =StringRequest(
            Request.Method.POST, adminUrl,
            { response ->

                val getAdmins = response.split(",")
                for(item in getAdmins){
                    if (email.equals(item)) {
                        binding.btnClientArea.visibility = View.VISIBLE
                        FirebaseMessaging.getInstance().subscribeToTopic("admin")
                        break
                    }

                }
            },
            { error ->
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()



            })


        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)

    }


    private fun checkUser() {
        val checkUser =
            SharedPref.readFromSharedPref(this, Constants.userPrefName, Constants.defaultPrefValue);
        if (!checkUser.equals("0")) {
            val splitDetails = checkUser?.split(",")
            binding.txtUsername.setText(splitDetails?.get(0) ?: "username")
            binding.txtEmail.setText(splitDetails?.get(1) ?: "email")
            uid=splitDetails?.get(3)?:"nothing"
            FirebaseMessaging.getInstance().subscribeToTopic(uid)



            ClientAreaConfig(splitDetails?.get(1) ?: "email");


        }

        binding.btnClientArea.setOnClickListener {
            SharedPref.writeInSharedPref(this,"backOrder","1")
            SharedPref.writeInSharedPref(this,"catRefresh","1")
            startActivity(
                Intent(
                    this,
                    ClientAreaActivity::class.java
                )

            )
        }

    }

    private fun ClickOnLogoutBtn() {

        binding.logoutBtn.setOnClickListener { LogoutUser() }

    }

    private fun LogoutUser() {
        val prefInfo = this.getSharedPreferences(Constants.mainUserPref, Context.MODE_PRIVATE)
        val editor = prefInfo.edit().clear()
        editor.commit()
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, Login_Activity::class.java))
        finish()


    }

    private fun ClickOnVerifybtn() {

        binding.everifyBtn.setOnClickListener {
            val dialog = ProgressDialog.progressDialog(this,"Sending verification email...")
            dialog.show()

            val milis: String = System.currentTimeMillis().toString()
            val otp: String = milis.substring(7, 13)
            val email: String = binding.txtEmail.text.toString().trim()
            val title = "email verification"

            sendOTPforEmailVerification(email, otp, title,dialog)
        }



    }

    private fun sendOTPforEmailVerification(
        email: String,
        otp: String,
        title: String,
        dialog: Dialog
    ) {

        val request: StringRequest = object : StringRequest(
            Method.POST, sendMail,
            Response.Listener { response ->

                if(response.equals("Mail sent")){
                    dialog.dismiss()
                    Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                    val intent:Intent = Intent(this,EmailVerification_Activity::class.java)
                    intent.putExtra("email",email)
                    intent.putExtra("otp",otp);
                    intent.putExtra("title",title)
                    startActivity(intent)
                    finish()
                }else{
                    dialog.dismiss()
                    Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                }



            },
            Response.ErrorListener { error ->
                dialog.dismiss()
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()



            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email
                params["otp"] = otp
                params["title"] = title
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

    private fun checkVerification() {
        fetchUserInfo(binding.txtEmail.text.toString().trim())

    }

    private fun fetchUserInfo(email: String) {
        val request: StringRequest = object : StringRequest(
            Method.POST, fetchUrl,
            Response.Listener { response ->

                try {

                    val jsonObject  = JSONObject(response)
                    val success:String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if(success.equals("1")){

                        val jsonObject: JSONObject = jsonArray.getJSONObject(0)

                        val id:String =jsonObject.getString("id")
                        val name:String =jsonObject.getString("username")
                        val email:String =jsonObject.getString("email")
                        val password:String =jsonObject.getString("password")
                        val emailv:String= jsonObject.getInt("emailv").toString()

                        if(emailv.equals("0")){
                            binding.emailVerificationLayout.visibility=VISIBLE;

                        }else{

                        }

                    }

                }catch (e: JSONException){
                    e.printStackTrace();
                }


            },
            Response.ErrorListener { error ->

            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email
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

                Collections.shuffle(categoryList)


                val  adapter:CategoryAdapter = CategoryAdapter(this,categoryList)
                adapter.notifyDataSetChanged()
                setRecyclerView(adapter)

            }else{

                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()

            }

        },{ error->

            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()

        })

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)


    }

    private fun setRecyclerView(finaladapter: CategoryAdapter) {
        binding.allCatRecycler.setHasFixedSize(true)
        binding.allCatRecycler.layoutManager= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.allCatRecycler.adapter=finaladapter


    }


    private fun SetupViewPager() {

       val tabLayoutMediator= TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,position->
            when(position){
                0->tab.text="Home"

                1->tab.text="Popular"

            }
        }
        binding.viewPager.adapter = Pager.PagerAdapter(this)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when(tab.position){
                        0->{
                            binding.dview1.visibility= VISIBLE
                            binding.dview2.visibility= GONE
                        }
                        1->{
                            binding.dview2.visibility= VISIBLE
                            binding.dview1.visibility= GONE
                        }

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        tabLayoutMediator.attach()


    }

    private fun GetCartItemCount() {

        val request: StringRequest = object : StringRequest(
            Method.POST, fetchCart,
            Response.Listener { response ->

                try {

                    val jsonObject = JSONObject(response)
                    val success: String = jsonObject.getString("success")
                    val jsonArray: JSONArray = jsonObject.getJSONArray("data")
                    if (success.equals("1")) {
                        cartList.clear()


                        for (item in 0 until jsonArray.length()) {

                            val jsonObject: JSONObject = jsonArray.getJSONObject(item)

                            val id: String = jsonObject.getString("id")
                            val pId: String = jsonObject.getString("pId")
                            val quantity: String = jsonObject.getString("quantity")


                            cartList.add(pId)

                        }

                        if(cartList.size>0){
                            binding.cartItemNumber.visibility= VISIBLE
                            binding.cartItemNumber.text=cartList.size.toString()
                        }else{
                            binding.cartItemNumber.visibility= GONE
                        }



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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        when(item.itemId){
            R.id.notification-> startActivity(Intent(this,Notification_Activity::class.java))

        }

        return super.onOptionsItemSelected(item)

    }






    override fun onResume() {
        super.onResume()
        GetCartItemCount()
        val value = SharedPref.readFromSharedPref(this,"catRefresh","0")
        if(value.equals("1")){
            GetCategories()
            SharedPref.writeInSharedPref(this,"catRefresh","0")
        }
    }


    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }



}