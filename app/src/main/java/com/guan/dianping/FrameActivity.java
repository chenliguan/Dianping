package com.guan.dianping;

import android.os.Bundle;
import android.view.Window;
import android.widget.PopupWindow;

/**
 * 框架类封装业务相关的方法
 *
 * @author Guan
 * @file com.guan.dianping.activity
 * @date 2015/11/3
 * @Version 1.0
 */
public class FrameActivity extends BaseActivity {

    private PopupWindow mPopupWindow;

    /**
     * 把与业务相关的系统框架、界面初始化、设置等操作封装
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
