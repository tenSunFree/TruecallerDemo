package com.home.truecallerdemo.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.home.truecallerdemo.data.ContactPersonHomePageData
import com.home.truecallerdemo.view.fragment.ContactPersonHomePageViewPagerIdentifiedItemFragment
import com.home.truecallerdemo.view.fragment.ContactPersonHomePageViewPagerSavedItemFragment

class ContactPersonFragmentPagerAdapter(
    private val contactDataList: List<ContactPersonHomePageData>,
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {

    private val saved = "已儲存"
    private val identified = "已識別"
    private var tabTitles = arrayOf(saved, identified)

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    override fun getItem(position: Int): Fragment? {
        val saved = 0
        val identified = 1
        return when (position) {
            saved -> ContactPersonHomePageViewPagerSavedItemFragment(contactDataList)
            identified -> ContactPersonHomePageViewPagerIdentifiedItemFragment()
            else -> null
        }
    }
}