package com.home.truecallerdemo.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.home.truecallerdemo.ContactPersonActivity
import com.home.truecallerdemo.R
import com.home.truecallerdemo.data.ContactPersonHomePageData
import com.home.truecallerdemo.view.adapter.ContactPersonFragmentPagerAdapter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 聯絡人首頁
 */
@Suppress("NAME_SHADOWING")
class ContactPersonHomePageFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(activity: ContactPersonActivity): ContactPersonHomePageFragment {
            val fragment = ContactPersonHomePageFragment()
            fragment.activity = activity
            return fragment
        }
    }

    lateinit var activity: ContactPersonActivity // lateinit表名變量需要在定義後才賦值
    lateinit var binding: com.home.truecallerdemo.databinding.FragmentContactPersonHomePageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_contact_person_home_page, container, false
        )
        setActivityCurrentShowFragmentLabel()
        initializeClickListener()
        requestPermission()
        return binding.root
    }

    /** 告訴Activity, 目前顯示的Fragment是哪一個 */
    private fun setActivityCurrentShowFragmentLabel() {
        activity.currentShowFragment = ContactPersonActivity.CONTACT_PERSON_HOME_PAGE_FRAGMENT
    }

    private fun initializeClickListener() {}

    override fun onClick(view: View?) {}

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS
        )
            .subscribe { granted ->
                if (granted!!) {
                    binding.rootCoordinatorLayout.post {
                        activity.createLoadingDialog()
                        val just = "開始異步轉換DataList"
                        Observable.just(just)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io()) // 使用IO線程去處理 下面的map()相關
                            .map { s -> conversionContactDataList(getContactInfo()) }
                            .observeOn(AndroidSchedulers.mainThread()) // 使用主線程去處理 下面的subscribe()相關
                            .subscribe { dataList ->
                                initializeViewPager(dataList)
                                activity.dismissLoadingDialog()
                            }
                    }
                } else {
                    Toast.makeText(activity, "請先允許權限", Toast.LENGTH_SHORT).show()
                    activity.finish()
                }
            }
    }

    /**
     * 取得聯絡人資料
     */
    private fun getContactInfo(): List<ContactPersonHomePageData> {
        val contactDataList = ArrayList<ContactPersonHomePageData>()
        val contentUri = ContactsContract.Contacts.CONTENT_URI // 提供聯繫人的內容提供者
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI // 提供聯繫人電話的內容提供者
        val id = ContactsContract.Contacts._ID
        val displayName = ContactsContract.Contacts.DISPLAY_NAME
        val hasPhoneNumber = ContactsContract.Contacts.HAS_PHONE_NUMBER
        val phoneId = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        val number = ContactsContract.CommonDataKinds.Phone.NUMBER
        val contentResolver = activity.contentResolver
        @SuppressLint("Recycle") val cursor = contentResolver.query(
            contentUri, null, null, null,
            displayName // 按照什麼進行排序
        )
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val contactId = cursor.getString(cursor.getColumnIndex(id)) // 獲得聯繫人的ID號
                val name = cursor.getString(cursor.getColumnIndex(displayName)) // 獲得聯繫人的名稱
                val hasPhoneNumber =
                    cursor.getInt(cursor.getColumnIndex(hasPhoneNumber)) // 如果值為1 則該聯繫人至少有一個電話號碼; 如果值為0 則該聯繫人沒有電話號碼
                val contactData = ContactPersonHomePageData()
                if (hasPhoneNumber > 0) { // 如果聯繫人至少有一個電話號碼
                    contactData.name = name
                    val phoneCursor = contentResolver.query(
                        phoneUri,
                        arrayOf(number),
                        "$phoneId = ?", // where條件式
                        arrayOf(contactId), // 條件的參數
                        null
                    )
                    val contactList = ArrayList<String>()
                    assert(phoneCursor != null)
                    phoneCursor!!.moveToFirst()
                    while (!phoneCursor.isAfterLast) { // 如果指針不是在最後一條記錄
                        val phoneNumber = phoneCursor.getString(
                            phoneCursor.getColumnIndex(number)
                        ).replace(" ", "") // 去掉空白
                        contactList.add(phoneNumber)
                        phoneCursor.moveToNext()
                    }
                    contactData.number = contactList
                    contactDataList.add(contactData)
                    phoneCursor.close()
                }
            }
        }
        return contactDataList
    }


    /**
     * 將原本的dataList轉換成指定的樣式
     * 添加標籤分類, 比如說A B C..等
     */
    private fun conversionContactDataList(contactDataList: List<ContactPersonHomePageData>): List<ContactPersonHomePageData> {
        val newContactDataList = ArrayList<ContactPersonHomePageData>()
        var previousLableName = ""
        for (i in contactDataList.indices) {
            val contactData = ContactPersonHomePageData()
            val name = contactDataList[i].name
            val lableName = name[0].toString()
            if (lableName != previousLableName) {
                previousLableName = lableName
                contactData.isShowLabel = true
            }
            contactData.name = name
            contactData.number = contactDataList[i].number
            newContactDataList.add(contactData)
        }
        return newContactDataList
    }

    private fun initializeViewPager(contactDataList: List<ContactPersonHomePageData>) {
        val adapter = ContactPersonFragmentPagerAdapter(
            contactDataList, activity.supportFragmentManager
        )
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, p1: Float, p2: Int) {
                val identified = 1
                if (position == identified) {
                    binding.addContactFrameLayout.visibility = View.INVISIBLE
                } else {
                    binding.addContactFrameLayout.visibility = View.VISIBLE
                }
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(p0: Int) {}
        })
        val pageLimit = 2
        binding.viewPager.offscreenPageLimit = pageLimit
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    /**
     * 判斷此Fragment目前屬於顯示還是隱藏
     * 第二次顯示之後, 每次都會觸發此方法; 第一次顯示, 只會觸發onCreateView
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            setActivityCurrentShowFragmentLabel()
        }
    }
}