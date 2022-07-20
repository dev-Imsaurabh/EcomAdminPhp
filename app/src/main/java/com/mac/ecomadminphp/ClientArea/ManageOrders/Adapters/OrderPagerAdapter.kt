package com.mac.ecomadminphp.ClientArea.ManageOrders.Adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mac.ecomadminphp.ClientArea.ManageOrders.Fragments.*
import com.mac.ecomadminphp.Fragments.HomeFragment
import com.mac.ecomadminphp.Fragments.PopularFragment

abstract class OrderPager{


    class OrderPagerAdapter(appCompatActivity: AppCompatActivity):FragmentStateAdapter(appCompatActivity){


        override fun getItemCount(): Int {

            return 6

        }

        override fun createFragment(position: Int): Fragment {

            return when(position){
                0->ActiveFragments()
                1->CanceledFragment()
                2->DeliveredFragment()
                3->ReturnFragment()
                4->RefundFragments()
                5->AssignedFragment()
                else->ActiveFragments()
            }
        }


    }

}
