package com.skripsi.ambulanapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skripsi.ambulanapp.ui.admin.AdminListChatCustomerFragment
import com.skripsi.ambulanapp.ui.admin.AdminListChatDriverFragment
import com.skripsi.ambulanapp.ui.admin.AdminListUserCustomerAccountFragment
import com.skripsi.ambulanapp.ui.admin.AdminListUserDriverAccountFragment

class AdapterViewPager (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    type: String,
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val _type = type
    private val total = 2

    override fun getItemCount(): Int = total

    override fun createFragment(position: Int): Fragment {

        return when (_type) {
            "list_user" -> {
                return when (position) {
                    0 -> AdminListUserDriverAccountFragment()
                    1 -> AdminListUserCustomerAccountFragment()
                    else -> Fragment()
                }
            }
            "list_chat" -> {
                return when (position) {
                    0 -> AdminListChatDriverFragment()
                    1 -> AdminListChatCustomerFragment()
                    else -> Fragment()
                }
            }
            else -> {
                Fragment()
            }
        }
    }
}