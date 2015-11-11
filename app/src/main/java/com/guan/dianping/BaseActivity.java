package com.guan.dianping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * 基础类封装业务无关的方法
 *
 * @author Guan
 * @file com.guan.dianping.activity
 * @date 2015/11/3
 * @Version 1.0
 */
public class BaseActivity extends FragmentActivity {

    /**
     * 把最常用的与业务无关的方法封装,简化编码编写过程
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Toast公共方法
     * @param pMsg
     */
    public void showMsg(String pMsg) {
        Toast.makeText(BaseActivity.this, pMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * intent 跳转Activity公共方法
     * @param pClass
     */
    public void openActivity(Class<?> pClass) {
        Intent _intent = new Intent(this,pClass);
        startActivity(_intent);
    }

    /**
     * intent 跳转Activity并finish()公共方法
     * @param pClass
     */
    public void openActivityFn(Class<?> pClass) {
        Intent _intent = new Intent(this,pClass);
        startActivity(_intent);
        finish();
    }

}
