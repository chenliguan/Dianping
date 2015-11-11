package com.guan.consts;

public class CONSTS {

	//public static final String HOST = "http://192.168.1.107:8080/dianping-server";
	public static final String HOST_98 = "http://172.28.89.98:8080/dianping-server";
	public static final String HOST = "http://www.heartguard.cn:8080/dianping-server";

	// 城市数据
	public static final String City_Data_URI = HOST + "/api/city";
	// 商品分类
	public static final String Category_Data_URI = HOST_98 + "/api/category";
	// 商品的列表信息
	public static final String Goods_Datas_URL = HOST_98 + "/api/goods";

	// ?page=1&size=10
//	http://172.28.89.98:8080/dianping-server/api/goods?page=1&size=10

	// 附近商品列表信息
	public static final String Goods_NearBy_URI = HOST_98 + "/api/nearby";
	// 登录验证的URL
	public static final String USER_LOGIN = HOST + "/api/user?flag=login";
	// 注册的URL
	public static final String USER_REGISTER = HOST + "/api/user?flag=register";

}
