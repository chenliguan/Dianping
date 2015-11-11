package com.guan.dianping;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.guan.consts.CONSTS;
import com.guan.entity.Goods;
import com.guan.entity.ResponseObject;
import com.guan.entity.Shop;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

//呈现地图的样式
public class NearbyMapActivity extends FrameActivity implements LocationSource,
        AMapLocationListener, OnMarkerClickListener, OnMapLoadedListener,
        OnInfoWindowClickListener, InfoWindowAdapter {
    @ViewInject(R.id.search_mymap)
    private MapView mapView;
    private AMap aMap;
    private double longitude = 116.367612;// 经度
    private double latitude = 40.075483;// 纬度
    private List<Goods> listDatas;// 商品信息

    private LocationManagerProxy mAMapLocManager = null;
    private OnLocationChangedListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_map_act);
        ViewUtils.inject(this);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setLocationSource(this);// 设置了定位的监听
            aMap.setMyLocationEnabled(true);// 显示定位层并且可以触发定位，默认是false
            aMap.setOnMapLoadedListener(this);// 设置aMap加载成功事件的监听
            aMap.setOnMarkerClickListener(this);// 设置点击marker事件的监听器
            aMap.setInfoWindowAdapter(this);// 设置自定义的InfoWindow样式
            aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow的事件监听器
        }
    }

    @OnClick({R.id.search_back, R.id.search_refresh})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back:// 返回按钮
                finish();
                break;
            case R.id.search_refresh:// 刷新加载数据
                loadData(String.valueOf(latitude), String.valueOf(longitude),
                        "1000");
                break;

            default:
                break;
        }
    }

    // 按照定位的地址和搜索半径加载周边的数据
    private void loadData(String lat, String lon, String radius) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("lat", lat);
        params.addQueryStringParameter("lon", lon);
        params.addQueryStringParameter("radius", radius);
        new HttpUtils().send(HttpMethod.GET, CONSTS.Goods_NearBy_URI, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Toast.makeText(NearbyMapActivity.this, "数据加载失败请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        // 将服务端的返回的json数据进行解析
                        ResponseObject<List<Goods>> object = new GsonBuilder()
                                .create()
                                .fromJson(
                                        arg0.result,
                                        new TypeToken<ResponseObject<List<Goods>>>() {
                                        }.getType());
                        // Log.i("TAG", object.getDatas()+"");
                        listDatas = object.getDatas();

                        // 设置地图的缩放
                        /*
						 * new LatLng(latitude, longitude) 以当前位置为中心 16 缩放级别
						 * 地图缩放级别为4-20级
						 * 缩放级别较低时，您可以看到更多地区的地图；缩放级别高时，您可以查看地区更加详细的地图。 0
						 * 默认情况下，地图的方向为0度，屏幕正上方指向北方。当您逆时针旋转地图时，
						 * 地图正北方向与屏幕正上方的夹角度数为地图方向
						 * ，范围是从0度到360度。例如，一个90度的查询结果，在地图上的向上的方向指向正东 30
						 * 地图倾角范围为0-45度。
						 * 
						 * 详情参考：http://lbs.amap.com/api/android-sdk/guide/camera/
						 */
                        aMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(new CameraPosition(
                                        new LatLng(latitude, longitude), 15, 0,
                                        30)));
                        // 标记到地图上
                        addMarker(listDatas);
                    }
                });
    }

    // 将数据标记到地图上
    public void addMarker(List<Goods> list) {
        // 声明标记对象
        MarkerOptions markerOptions;
        for (Goods goods : list) {
            Shop shop = goods.getShop();
            markerOptions = new MarkerOptions();
            // 设置当前的markerOptions对象的经纬度
            markerOptions.position(new LatLng(
                    Double.parseDouble(shop.getLat()), Double.parseDouble(shop
                    .getLon())));
            // 点击每一个图标显示信息 显示内容为商铺名称以及当前的商品价钱
            markerOptions.title(shop.getName()).snippet("￥" + goods.getPrice());
            // 不同类型的商品设置不同的类型图标
            if (goods.getCategoryId().equals("3")) {
                markerOptions.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_landmark_chi));
            } else if (goods.getCategoryId().equals("5")) {
                markerOptions.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_landmark_movie));
            } else if (goods.getCategoryId().equals("8")) {
                markerOptions.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_landmark_hotel));
            } else if (goods.getCategoryId().equals("6")) {
                markerOptions.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_landmark_life));
            } else if (goods.getCategoryId().equals("4")) {
                markerOptions.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_landmark_wan));
            } else {
                markerOptions.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_landmark_default));
            }
            // 在地图上显示所有的图标
            aMap.addMarker(markerOptions).setObject(goods);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        if (mAMapLocManager == null) {
            mListener = listener;
            mAMapLocManager = LocationManagerProxy.getInstance(this);
            mAMapLocManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 5000, 10, this);
        }
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onLocationChanged(Location location) {
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
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            // longitude = location.getLongitude();
            // latitude = location.getLatitude();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            mListener.onLocationChanged(location);// 显示系统的小蓝点
            Log.i("TAG", "当前的经度和纬度是：" + longitude + "," + latitude);
            //加载信息
            loadData(latitude + "", longitude + "", "1000");
            mAMapLocManager.removeUpdates(this);
        }
    }

    @Override
    public View getInfoContents(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    // 当显示的窗体进行点击的时候
    @Override
    public void onInfoWindowClick(Marker arg0) {
        //获取商店的名称
        String shopName = arg0.getTitle();
        //根据商铺名称找到对应的商品
        Goods goods = getGoodsByShopName(shopName);
        if (goods != null) {
            //跳转到详情页面
            Intent intent = new Intent(this, GoodsDetailActivity.class);
            intent.putExtra("goods", goods);
            startActivity(intent);
        }
    }

    //根据商店的名称来获取当前的商品信息
    public Goods getGoodsByShopName(String shopName) {
        for (Goods goods : listDatas) {//遍历商品集合进行商铺的匹配
            if (goods.getShop().getName().equals(shopName)) {
                return goods;
            }
        }
        return null;
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        return false;
    }
}
