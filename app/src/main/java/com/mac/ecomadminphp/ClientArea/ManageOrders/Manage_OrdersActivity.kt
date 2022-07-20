package com.mac.ecomadminphp.ClientArea.ManageOrders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mac.ecomadminphp.Adapters.Pager
import com.mac.ecomadminphp.ClientArea.ManageOrders.Adapters.OrderPager
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.Utils.Constants
import com.mac.ecomadminphp.databinding.ActivityManageOrdersBinding

class Manage_OrdersActivity : AppCompatActivity() {
    private lateinit var binding:ActivityManageOrdersBinding
    private lateinit var page:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManageOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Manage orders"

        SetupViewPager()
    }


    private fun SetupViewPager() {

        val tabLayoutMediator= TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,position->
            when(position){
                0->tab.text="Active"
                1->tab.text="Canceled"
                2->tab.text="Delivered"
                3->tab.text="Return"
                4->tab.text="Refund"
                5->tab.text="Assigned"

            }
        }
        binding.viewPager.adapter = OrderPager.OrderPagerAdapter(this)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when(tab.position){

                        0->page="0";
                        1->page="1";
                        2->page="2";
                        3->page="3";
                        4->page="4";
                        5->page="5";

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
}