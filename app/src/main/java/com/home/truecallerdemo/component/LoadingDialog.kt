package com.home.truecallerdemo.component

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.home.truecallerdemo.R
import com.wang.avi.AVLoadingIndicatorView

/** 彈出Dialog提示框, 展示讀取中的動畫  */
class LoadingDialog(
    context: Context, theme: Int, private val screenWidth: Int
) : Dialog(context, theme) {

    private lateinit var avLoadingIndicatorView: AVLoadingIndicatorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        avLoadingIndicatorView = findViewById(R.id.avLoadingIndicatorView)
        setProperty()
    }

    fun setView(view: View) {
        setContentView(view)
    }

    private fun setProperty() {
        val defaultXCoordinate = 0 // 對話框的位置, 0為中間
        val defaultYCoordinate = 0 // 對話框的位置, 0為中間
        val customWidth = screenWidth * 85 / 100
        val viewWidth: Int = customWidth // 對話框的寬度
        val viewHeight: Int = customWidth // 對話框的高度
        val layoutParams = window.attributes
        layoutParams.x = defaultXCoordinate // 設置對話框的位置
        layoutParams.y = defaultYCoordinate
        layoutParams.width = viewWidth
        layoutParams.height = viewHeight
        val alpha = 1f
        layoutParams.alpha = alpha // 設置對話框的透明度, 1f不透明
        layoutParams.gravity = Gravity.CENTER // 設置顯示在中間
        window.attributes = layoutParams
    }

    override fun show() {
        super.show()
        avLoadingIndicatorView.smoothToShow()
    }

    override fun hide() {
        super.hide()
        avLoadingIndicatorView.smoothToHide()
    }
}