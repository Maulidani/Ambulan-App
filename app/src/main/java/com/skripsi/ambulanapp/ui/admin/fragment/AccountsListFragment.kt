package com.skripsi.ambulanapp.ui.admin.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.admin.AddEditDriverActivity

class AccountsListFragment : Fragment() {
    private val btnAdd: FloatingActionButton by lazy { requireActivity().findViewById(R.id.fabAdd) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accounts_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditDriverActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        //refresh
    }
}