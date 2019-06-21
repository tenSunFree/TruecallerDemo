package com.home.truecallerdemo.view.fragment

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.home.truecallerdemo.R
import com.home.truecallerdemo.data.ContactPersonHomePageData
import com.home.truecallerdemo.view.adapter.ContactRecyclerViewAdapter

@SuppressLint("ValidFragment")
class ContactPersonHomePageViewPagerSavedItemFragment
@SuppressLint("ValidFragment") constructor(
    private val contactDataList: List<ContactPersonHomePageData>
) : Fragment() {

    private lateinit var binding: com.home.truecallerdemo.databinding.FragmentContactPersonViewPagerSavedItemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_contact_person_view_pager_saved_item,
            container,
            false
        )
        initializeRecyclerView(contactDataList)
        return binding.root
    }

    private fun initializeRecyclerView(dataList: List<ContactPersonHomePageData>) {
        val adapter = ContactRecyclerViewAdapter(dataList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val cacheSize = -1
        binding.recyclerView.setItemViewCacheSize(cacheSize) // 解決複用錯亂的問題
        binding.recyclerView.setHasFixedSize(true)
    }
}