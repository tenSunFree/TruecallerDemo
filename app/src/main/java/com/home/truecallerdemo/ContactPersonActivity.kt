package com.home.truecallerdemo

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.home.truecallerdemo.component.LoadingDialog
import com.home.truecallerdemo.view.fragment.ContactPersonHomePageFragment
import com.home.truecallerdemo.view.fragment.ContactPersonShortMessageServiceFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ContactPersonActivity : AppCompatActivity() {

    companion object {
        const val CONTACT_PERSON_HOME_PAGE_FRAGMENT: String = "ContactPersonHomePageFragment"
        const val CONTACT_PERSON_SHORT_MESSAGE_SERVICE_FRAGMENT: String = "ContactPersonShortMessageServiceFragment"
    }

    private var binding: com.home.truecallerdemo.databinding.ActivityContactPersonBinding? = null
    private var contactPersonHomePageFragment: ContactPersonHomePageFragment? = null
    private var contactPersonShortMessageServiceFragment: ContactPersonShortMessageServiceFragment? = null
    var currentShowFragment: String? = null // 提供判斷目前activity是顯示哪一個fragment
    private var firstCloseAppCurrentTimeMillis: Long = 0 // 第一次點擊返回鍵關閉app的時間
    private val closeAppTimeInterval: Long = 2000 // 在2秒內再次點擊返回鍵, 則關閉app
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_person)
        showFragment(CONTACT_PERSON_SHORT_MESSAGE_SERVICE_FRAGMENT)
        showFragment(CONTACT_PERSON_HOME_PAGE_FRAGMENT)
    }

    /**
     * 彈出讀取中的訊息框
     */
    fun createLoadingDialog() {
        loadingDialog = LoadingDialog(this, R.style.LoadingDialog, getScreenWidth())
        val view = View.inflate(this, R.layout.dialog_loading, null)
        loadingDialog.setView(view)
        loadingDialog.setCanceledOnTouchOutside(false) // 點擊周圍留白處不消失
        loadingDialog.setCancelable(false) // 點擊返回鍵不消失
        loadingDialog.show()
    }

    /**
     * 關閉讀取中的訊息框
     */
    fun dismissLoadingDialog() {
        val delayMillis = 200
        binding!!.containerFrameLayout.postDelayed({
            if (loadingDialog.isShowing) {
                loadingDialog.dismiss()
            }
        }, delayMillis.toLong())
    }

    /**
     * 取得螢幕的寬度
     */
    private fun getScreenWidth(): Int {
        return binding!!.containerFrameLayout.width
    }

    /**
     * 顯示Fragment
     * 多個Fragment切換時, 避免重複創建Fragment
     */
    fun showFragment(fragmentName: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction() // 開啟事務
        hideFragment(fragmentTransaction) // 先隱藏所有的Fragment
        when (fragmentName) {
            CONTACT_PERSON_HOME_PAGE_FRAGMENT -> // 聯絡人首頁
                if (contactPersonHomePageFragment == null) { // 如果Fragment為空, 就新建一個實例
                    contactPersonHomePageFragment = ContactPersonHomePageFragment.newInstance(this)
                    fragmentTransaction.add(
                        R.id.containerFrameLayout,
                        contactPersonHomePageFragment!!
                    ) // 執行事務, 添加Fragment
                } else { // 如果不為空, 就將它從棧中顯示出來
                    fragmentTransaction.show(contactPersonHomePageFragment!!)
                }
            CONTACT_PERSON_SHORT_MESSAGE_SERVICE_FRAGMENT -> // 聯絡人簡訊
                if (contactPersonShortMessageServiceFragment == null) { // 如果Fragment為空, 就新建一個實例
                    contactPersonShortMessageServiceFragment =
                        ContactPersonShortMessageServiceFragment.newInstance(this)
                    fragmentTransaction.add(
                        R.id.containerFrameLayout,
                        contactPersonShortMessageServiceFragment!!
                    ) // 執行事務, 添加Fragment
                } else { // 如果不為空, 就將它從棧中顯示出來
                    fragmentTransaction.show(contactPersonShortMessageServiceFragment!!)
                }
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    /**
     * 隱藏Fragment
     */
    private fun hideFragment(fragmentTransaction: FragmentTransaction) {
        if (contactPersonHomePageFragment != null) { // 如果不為空, 就先隱藏起來
            fragmentTransaction.hide(contactPersonHomePageFragment!!)
        }
        if (contactPersonShortMessageServiceFragment != null) { // 如果不為空, 就先隱藏起來
            fragmentTransaction.hide(contactPersonShortMessageServiceFragment!!)
        }
    }

    /**
     * 根據currentFragment判斷目前顯示的是哪一個Fragment, 並執行對應的行為
     */
    override fun onBackPressed() {
        when (currentShowFragment) {
            CONTACT_PERSON_HOME_PAGE_FRAGMENT -> { // 2秒內點擊2次返回鍵, 才會退出app
                if (firstCloseAppCurrentTimeMillis + closeAppTimeInterval > System.currentTimeMillis()) {
                    finish()
                } else {
                    Toast.makeText(baseContext, "再點一次離開", Toast.LENGTH_SHORT).show()
                }
                firstCloseAppCurrentTimeMillis = System.currentTimeMillis()
            }
            CONTACT_PERSON_SHORT_MESSAGE_SERVICE_FRAGMENT -> showFragment(CONTACT_PERSON_HOME_PAGE_FRAGMENT)
        }
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(messageEventString: String) {
        when (messageEventString) {
            CONTACT_PERSON_SHORT_MESSAGE_SERVICE_FRAGMENT -> showFragment(CONTACT_PERSON_SHORT_MESSAGE_SERVICE_FRAGMENT)
        }
    }
}
