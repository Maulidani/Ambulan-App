package com.skripsi.ambulanapp.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterViewPager

class AdminListChatContainerFragment : Fragment() {
    private val TAG = "AdminListChatFragment"
    private val actionType = "list_chat"

    private val viewPager: ViewPager2 by lazy { requireActivity().findViewById(R.id.viewPager) }
    private val tabLayout: TabLayout by lazy { requireActivity().findViewById(R.id.tabLayout) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_list_chat_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPagerAdapter = AdapterViewPager(
            requireActivity().supportFragmentManager, lifecycle, actionType
        )
        viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Driver"
                1 -> tab.text = "Customer"
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()

    }

}