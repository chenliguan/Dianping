package com.guan.dianping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.guan.fragment.FragmentHome;
import com.guan.fragment.FragmentMy;
import com.guan.fragment.FragmentSearch;
import com.guan.fragment.FragmentTuan;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 主页面
 *
 * @author Guan
 * @file com.guan.dianping.activity
 * @date 2015/11/3
 * @Version 1.0
 */
public class MainActivity extends FrameActivity implements RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.main_bottom_tabs)
    private RadioGroup group;
    @ViewInject(R.id.main_home)
    private RadioButton main_home;
    private FragmentManager fragmentManager;//管理fragment的类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewUtils.inject(this);
        //初始化FragmentManager
        fragmentManager = getSupportFragmentManager();
        //设置默认选中
        main_home.setChecked(true);
        group.setOnCheckedChangeListener(this);
        //切换不同的fragment
        changeFragment(new FragmentHome(), false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.main_home://首页
                changeFragment(new FragmentHome(), true);
                break;
            case R.id.main_my://我的
                changeFragment(new FragmentMy(), true);
                break;
            case R.id.main_search://发现
                changeFragment(new FragmentSearch(), true);
                break;
            case R.id.main_tuan://团购
                changeFragment(new FragmentTuan(), true);
                break;

            default:
                break;
        }

    }

    //切换不同的fragment
    public void changeFragment(Fragment fragment, boolean isInit) {
        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        if (!isInit) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
