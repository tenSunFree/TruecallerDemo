package com.home.truecallerdemo.view.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.home.truecallerdemo.R

class ContactPersonHomePageViewPagerIdentifiedItemFragment : Fragment() {

    lateinit var binding: com.home.truecallerdemo.databinding.FragmentContactPersonViewPagerIdentifiedItemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_contact_person_view_pager_identified_item,
            container,
            false
        )
        return binding.root
    }
}