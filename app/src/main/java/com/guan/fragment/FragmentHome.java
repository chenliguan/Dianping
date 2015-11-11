package com.guan.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.guan.dianping.AllCategoryActivity;
import com.guan.dianping.CityActivity;
import com.guan.dianping.R;
import com.guan.utils.MyUtils;
import com.guan.utils.SharedUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.IOException;
import java.util.List;

//首页
public class FragmentHome extends Fragment implements LocationListener {

    @ViewInject(R.id.index_top_city)
    private TextView topCity;
    private String cityName;//当前城市名称
    private LocationManager locationManager;
    @ViewInject(R.id.home_nav_sort)
    private GridView navSort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_index, null);
        ViewUtils.inject(this, view);
        //获取数据并且显示
        topCity.setText(SharedUtils.getCityName(getActivity()));
        navSort.setAdapter(new NavAdapter());
        return view;
    }

    public class NavAdapter extends BaseAdapter {
        //计算需要适配的数据总量
        @Override
        public int getCount() {
            return MyUtils.navsSort.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        //item对应的view的数据渲染
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //在convertView为null的时候讲布局转化成View
            MyHolder myHolder = null;
            if (convertView == null) {
                myHolder = new MyHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_index_nav_item, null);
                ViewUtils.inject(myHolder, convertView);
                convertView.setTag(myHolder);//打标签
            } else {
                myHolder = (MyHolder) convertView.getTag();
            }
            //赋值
            myHolder.textView.setText(MyUtils.navsSort[position]);
            myHolder.imageView.setImageResource(MyUtils.navsSortImages[position]);
            //选中的如果是全部的话
            if (position == MyUtils.navsSort.length - 1) {
                myHolder.imageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), AllCategoryActivity.class));
                    }
                });
            }
            return convertView;
        }

    }

    public class MyHolder {
        @ViewInject(R.id.home_nav_item_desc)
        public TextView textView;
        @ViewInject(R.id.home_nav_item_image)
        public ImageView imageView;
    }

    @OnClick({R.id.index_top_city, R.id.index_top_scan})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.index_top_city://城市
//                带有返回值的跳转
                startActivityForResult(new Intent(getActivity(), CityActivity.class),
                        MyUtils.RequestCityCode);
                break;

            default:
                break;
        }
    }

    //处理返回值的结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyUtils.RequestCityCode && resultCode == Activity.RESULT_OK) {
            cityName = data.getStringExtra("cityName");
            topCity.setText(cityName);
        } else if (requestCode == MyUtils.RequestCaptureCode && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Uri uri = Uri.parse(scanResult);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //检查当前的gps模块
        checkGPSIsOpen();
    }

    //检查是否打开GPS
    private void checkGPSIsOpen() {
        //获取当前的LocationManager对象
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isOpen) {
            //进入gps的设置页面
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 0);
        }
        //开始定位
        startLocation();
    }

    //使用gps定位的方法
    private void startLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
    }

    //接受并且处理消息
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                topCity.setText(cityName);
            }
            return false;
        }
    });

    //获取对应位置的经纬度并且定位城市
    private void updateWithNewLocation(Location location) {
        double lat = 0.0, lng = 0.0;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.i("TAG", "经度是" + lat + ",纬度是：" + lng);
        } else {
            cityName = "无法获取城市信息";
        }
        //通过经纬度来获取地址，由于地址会有多个，这个和经纬度精确度有关，本实例中定义了最大的返回数2.即在集合对象中有两个值
        List<Address> list = null;
        Geocoder ge = new Geocoder(getActivity());
        try {
            list = ge.getFromLocation(lat, lng, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Address ad = list.get(i);
                cityName = ad.getLocality();//获取城市
            }
        }
        //发送空消息
        handler.sendEmptyMessage(1);
    }

    //位置信息更改执行的方法
    @Override
    public void onLocationChanged(Location location) {
        //更新当前的位置信息
        updateWithNewLocation(location);

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存城市
        SharedUtils.putCityName(getActivity(), cityName);
        //停止定位
        stopLocation();
    }

    //停止定位
    private void stopLocation() {
        locationManager.removeUpdates(this);
    }
}

