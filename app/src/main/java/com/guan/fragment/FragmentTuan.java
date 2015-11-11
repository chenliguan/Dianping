package com.guan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guan.consts.CONSTS;
import com.guan.dianping.GoodsDetailActivity;
import com.guan.dianping.R;
import com.guan.entity.Goods;
import com.guan.entity.ResponseObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.loopj.android.image.SmartImageView;
import com.squareup.picasso.Picasso;

import org.apache.http.client.HttpClient;

import java.util.List;

//团购
public class FragmentTuan extends Fragment {

    @ViewInject(R.id.index_listGoods)
    private PullToRefreshListView listGoods;
    //当商品列表点击的时候显示详情
    @OnItemClick(R.id.index_listGoods)
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
        intent.putExtra("goods", listDatas.get(position-1));
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tuan_index, null);
        ViewUtils.inject(this, view);

        //设置商品的信息列表属性
        listGoods.setMode(PullToRefreshBase.Mode.BOTH);//支持上拉也支持下拉
        listGoods.setScrollingWhileRefreshingEnabled(true);//滚动的时候不加载数据
        listGoods.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新 y<0
                loadDatas(listGoods.getScrollY() < 0);
            }
        });

        //首次来到页面的时候要自动加载数据
        new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message arg0) {
                listGoods.setRefreshing();
                return true;
            }
        }).sendEmptyMessageDelayed(0, 300);
        return view;
    }

    private int page, size = 10, count;// 初始化数据
    private MyAdapter adapter;
    private List<Goods> listDatas;

    // 请求数据
    public void loadDatas(final boolean reflush) {
        if (reflush) {
            page = 1;
        } else {
            page++;
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("page", page + "");
        params.addQueryStringParameter("size", size + "");

        // 使用Xutils框架中的这对HTTP请求封装好的方法
        new HttpUtils().send(HttpRequest.HttpMethod.POST, CONSTS.Goods_Datas_URL, params,
                new RequestCallBack<String>() {
                    // 请求失败的时候执行的方法
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 停止刷新
                        listGoods.onRefreshComplete();
                        Toast.makeText(getActivity(), arg1, Toast.LENGTH_SHORT)
                                .show();
                    }

                    // 成功请求返回的方法，参数为返回的数据，这边是一个ResponseInfo对象，这个对象中是一系列的参数字符串，用到的时候进行处理
                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        // 停止刷新
                        listGoods.onRefreshComplete();
                        Gson gson = new Gson();
                        ResponseObject<List<Goods>> object = gson.fromJson(
                                arg0.result,
                                new TypeToken<ResponseObject<List<Goods>>>() {
                                }.getType());
                        // 获取传递的对象中封装的内容
                        page = object.getPage();
                        size = object.getSize();
                        count = object.getCount();
                        listDatas = object.getDatas();
                        Log.i("TAG", "测试数据------------" + listDatas.size());
                        Log.i("TAG", "page = " + page + " , size = " + size + " , count = " + count);
                        adapter = new MyAdapter();
                        listGoods.setAdapter(adapter);
                        // 判断是下拉刷新还是上拉加载
                        if (reflush) {
                            // 下拉刷新
                            listDatas = object.getDatas();
                            adapter = new MyAdapter();
                            listGoods.setAdapter(adapter);
                        } else {
                            // 加载更多
                            listDatas.addAll(object.getDatas()); // 在数据末添加数据
                            adapter.notifyDataSetChanged();
                        }

                        if (count == page) {
                            // 没有更多的数据显示,//只能刷新
                            listGoods.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                    }
                });
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listDatas.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        // 渲染每一个item对应的视图
        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            MyHolder holder = null;
            if (convertView == null) {
                holder = new MyHolder();
                // 映射出视图
                convertView = LayoutInflater.from(arg2.getContext()).inflate(
                        R.layout.tuan_goods_list_item, null);
                ViewUtils.inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (MyHolder) convertView.getTag();
            }
            // 获取对应的索引内容
            Goods goods = listDatas.get(arg0);

            //picasso框架来避免图片的出现oom以及图片错位
//            Picasso.with(arg2.getContext()).load(goods.getImgUrl()).placeholder(R.mipmap.ic_empty_dish).into(holder.image);
            holder.image.setImageUrl(goods.getImgUrl(),R.mipmap.ic_empty_dish,R.mipmap.ic_empty_dish);

            StringBuffer sbf = new StringBuffer("￥" + goods.getValue());
            //添加中划线
            SpannableString spannable = new SpannableString(sbf);
            spannable.setSpan(new StrikethroughSpan(), 0, sbf.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            holder.value.setText(spannable);
            holder.count.setText(goods.getBought() + "份");
            holder.price.setText("￥" + goods.getPrice());
            holder.title.setText(goods.getSortTitle());
            holder.titleContent.setText(goods.getTitle());
            return convertView;
        }

    }

    // 标签类
    public class MyHolder {
        @ViewInject(R.id.index_gl_item_image)
        public SmartImageView image;
        @ViewInject(R.id.index_gl_item_title)
        public TextView title;
        @ViewInject(R.id.index_gl_item_titlecontent)
        public TextView titleContent;
        @ViewInject(R.id.index_gl_item_price)
        public TextView price;
        @ViewInject(R.id.index_gl_item_value)
        public TextView value;
        @ViewInject(R.id.index_gl_item_count)
        public TextView count;
    }
}