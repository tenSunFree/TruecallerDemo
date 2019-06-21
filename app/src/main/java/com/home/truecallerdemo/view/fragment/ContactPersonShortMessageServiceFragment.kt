package com.home.truecallerdemo.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.home.truecallerdemo.ContactPersonActivity
import com.home.truecallerdemo.R
import com.home.truecallerdemo.databinding.FragmentContactPersonShortMessageServiceBinding
import com.home.truecallerdemo.eventbus.ContactPersonShortMessageServiceEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

/**
 * 聯絡人簡訊
 * 目前已經有了取得所有簡訊內容的功能 與發送短信的功能
 * 如果有興趣的話, 可以試著完成這個畫面
 */
class ContactPersonShortMessageServiceFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(activity: ContactPersonActivity): ContactPersonShortMessageServiceFragment {
            val fragment = ContactPersonShortMessageServiceFragment()
            fragment.activity = activity
            return fragment
        }
    }

    lateinit var activity: ContactPersonActivity // lateinit表名變量需要在定義後才賦值
    lateinit var binding: FragmentContactPersonShortMessageServiceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_contact_person_short_message_service, container, false
        )
        setActivityCurrentShowFragmentLabel()
        initializeClickListener()
        return binding.root
    }

    /** 告訴Activity, 目前顯示的Fragment是哪一個 */
    private fun setActivityCurrentShowFragmentLabel() {
        activity.currentShowFragment = ContactPersonActivity.CONTACT_PERSON_SHORT_MESSAGE_SERVICE_FRAGMENT
    }

    private fun initializeClickListener() {
        binding.previousPageFrameLayout.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.previousPageFrameLayout -> { // 上一頁按鈕
                activity.showFragment(ContactPersonActivity.CONTACT_PERSON_HOME_PAGE_FRAGMENT)
            }
        }
    }

    /**
     * 取得所有簡訊內容
     */
    @SuppressLint("SimpleDateFormat")
    private fun getAllSmsContent() {
        val smsUriAll = "content://sms/"
        val uri = Uri.parse(smsUriAll)
        val projection = arrayOf("_id", "address", "body", "date", "type")
        val sortOrder = "date desc"
        val cursor = activity.contentResolver.query(
            uri, projection, null, null, sortOrder
        )
        if (cursor!!.moveToFirst()) {
            val indexAddress = cursor.getColumnIndex("address") // 發件人地址, 即手機號, 如0988568544
            val indexBody = cursor.getColumnIndex("body") // 短信具體內容
            val indexDate = cursor.getColumnIndex("date") // 日期, long型, 如1256539465022
            val indexType = cursor.getColumnIndex("type") // 短信類型, 1是接收, 2是傳送
            do {
                val strAddress = cursor.getString(indexAddress)
                Log.d("more", "ContactPersonActivity, 手機: $strAddress")
                val strbody = cursor.getString(indexBody)
                Log.d("more", "ContactPersonActivity, 簡訊: $strbody")
                val longDate = cursor.getLong(indexDate)
                val intType = cursor.getInt(indexType)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val d = Date(longDate)
                val strDate = dateFormat.format(d)
                Log.d("more", "ContactPersonActivity, 日期: $strDate")
                var strType: String
                strType = when (intType) {
                    1 -> "接收"
                    2 -> "傳送"
                    else -> "null"
                }
                Log.d("more", "ContactPersonActivity, 類型: $strType")
            } while (cursor.moveToNext())
            if (!cursor.isClosed) {
                cursor.close()
            }
        }
    }

    /**
     * 發送短信
     * 監聽短信發送狀況
     */
    fun sendSMS(phoneNumber: String) {
        val smsSent = "SMS_SENT"
        val smsDelivered = "SMS_DELIVERED"
        val requestCode = 0
        val flags = 0
        val smsSentPendingIntent = PendingIntent.getBroadcast(activity, requestCode, Intent(smsSent), flags)
        val smsDeliveredPendingIntent = PendingIntent.getBroadcast(activity, requestCode, Intent(smsDelivered), flags)
        val message = "Test 20190621" // 傳送的簡訊內容
        activity.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(activity, "Invitation SMS sent", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
                        activity,
                        "Generic failure",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
                        activity,
                        "No service",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(activity, "Null PDU", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
                        activity,
                        "Radio off",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(smsSent))
        activity.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        activity,
                        "Invitation SMS delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(
                        activity,
                        "Invitation SMS not delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(smsDelivered))
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(
            phoneNumber, null, message, smsSentPendingIntent, smsDeliveredPendingIntent
        )
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 取得點擊的聯絡人的姓名與電話
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ContactPersonShortMessageServiceEvent) {
        Log.d("more", "ContactPersonActivity, 聯絡人姓名: ${event.name}")
        binding.nameTextView.text = event.name // 設置姓名
        val numberList = event.number
        for (number in numberList) {
            Log.d("more", "ContactPersonActivity, 聯絡人電話: $number")
        }
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