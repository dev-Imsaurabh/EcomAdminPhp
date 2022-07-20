package com.mac.ecomadminphp.Adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mac.ecomadminphp.Fragments.HomeFragment
import com.mac.ecomadminphp.Fragments.PopularFragment

abstract class Pager{


    class PagerAdapter(appCompatActivity: AppCompatActivity):FragmentStateAdapter(appCompatActivity){


        override fun getItemCount(): Int {

            return 2

        }

        override fun createFragment(position: Int): Fragment {

            return when(position){
                0->HomeFragment()
                1->PopularFragment()
                else->HomeFragment()
            }
        }


    }

}
