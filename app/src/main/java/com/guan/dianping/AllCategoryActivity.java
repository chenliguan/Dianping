package com.guan.dianping;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.R.integer;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guan.consts.CONSTS;
import com.guan.dianping.R;
import com.guan.entity.Category;
import com.guan.entity.ResponseObject;
import com.guan.utils.GsonUtil;
import com.guan.utils.MyUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class AllCategoryActivity extends FrameActivity {
    @ViewInject(R.id.home_nav_all_categray)
    private ListView categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载布局
        setContentView(R.layout.home_index_nav_all);
        ViewUtils.inject(this);
        // 适配
        // categoryList.setAdapter(new MyAdapter());
        // 开启异步任务
        new CategoryDataTask().execute();
    }

    // 使用异步任务获取信息
    public class CategoryDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(CONSTS.Category_Data_URI);
            try {
                HttpResponse response = client.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonString = EntityUtils.toString(response
                            .getEntity());
                    // 更新数据的内容
                    paseCategoryDataJson(jsonString);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //适配
            MyAdapter adapter = new MyAdapter();
            categoryList.setAdapter(adapter);
        }
    }

    // 解析数据信息
    public void paseCategoryDataJson(String json) {
        Gson gson = new Gson();
        ResponseObject<List<Category>> responseObject = gson.fromJson(json,
                new TypeToken<ResponseObject<List<Category>>>() {
                }.getType());

        List<Category> datas = responseObject.getDatas();
        //遍历集合对象
        for (Category category : datas) {
            int position = Integer.parseInt(category.getCategoryId());
            //将集合中的categoryId定义成数组的下标，categoryNumber定义数组存储的值
            MyUtils.allCategoryNumber[position-1] = category.getCategoryNumber();
        }
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MyUtils.allCategray.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyHolder myHolder = null;
            if (convertView == null) {
                myHolder = new MyHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.home_index_nav_all_item, null);
                ViewUtils.inject(myHolder, convertView);
                convertView.setTag(myHolder);// 打标签
            } else {
                myHolder = (MyHolder) convertView.getTag();
            }
            // 赋值
            myHolder.textDesc.setText(MyUtils.allCategray[position]);
            myHolder.imageView
                    .setImageResource(MyUtils.allCategrayImages[position]);
            myHolder.textNumber.setText(MyUtils.allCategoryNumber[position]+"");
            return convertView;
        }
    }

    public class MyHolder {
        @ViewInject(R.id.home_nav_all_item_number)
        public TextView textNumber;
        @ViewInject(R.id.home_nav_all_item_desc)
        public TextView textDesc;
        @ViewInject(R.id.home_nav_all_item_image)
        public ImageView imageView;
    }

    // 点击的监听
    @OnClick(R.id.home_nav_all_back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_nav_all_back:// 点击返回
                finish();
                break;

            default:
                break;
        }
    }
}
