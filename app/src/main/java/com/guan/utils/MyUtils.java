package com.guan.utils;

import com.guan.dianping.R;

public class MyUtils {

    //返回值
    public static final int RequestLoginCode = 1;
    public static final int RequestCityCode = 2;
    public static final int RequestCaptureCode = 3;
    public static String[] navsSort = {"美食", "电影", "酒店", "KTV", "自助餐", "休闲娱乐",
            "旅游", "购物", "都市丽人", "母婴", "女装", "美妆", "户外运动", "生活服务", "全部"};
    public static int[] navsSortImages = {R.mipmap.icon_home_food_99, R.mipmap.icon_home_movie_29,
            R.mipmap.icon_home_hotel_300, R.mipmap.icon_home_ktv_31, R.mipmap.icon_home_self_189,
            R.mipmap.icon_home_happy_2, R.mipmap.icon_home_flight_400, R.mipmap.icon_home_shopping_3,
            R.mipmap.icon_home_liren_442, R.mipmap.icon_home_child_13, R.mipmap.icon_home_nvzhuang_84,
            R.mipmap.icon_home_meizhuang_173, R.mipmap.icon_home_yundong_20, R.mipmap.icon_home_life_46,
            R.mipmap.icon_home_all_0};
    public static String[] allCategray = {"全部分类", "今日新单", "美食", "休闲娱乐",
            "电影", "生活服务", "写真生活", "酒店", "旅游", "都市丽人", "教育培训", "抽奖公益", "购物"};
    public static int[] allCategrayImages = {R.mipmap.ic_all, R.mipmap.ic_newest,
            R.mipmap.ic_food, R.mipmap.ic_entertain, R.mipmap.ic_movie, R.mipmap.ic_life,
            R.mipmap.ic_photo, R.mipmap.ic_hotel, R.mipmap.ic_travel, R.mipmap.ic_beauty,
            R.mipmap.ic_edu, R.mipmap.ic_luck, R.mipmap.ic_shopping};
    public static long allCategoryNumber[] = new long[allCategray.length + 5];//定义的大些

    private static final String RANDOMS = "1234567890poiuytrewqasdfghjklmnbvcxzQWERTYUIOPASDFGHJKLZXCVBNM";

    public static String getRandom(int num) {
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int random = (int) (Math.random() * RANDOMS.length());
            sbf.append(RANDOMS.charAt(random));
        }
        return sbf.toString();
    }
}
