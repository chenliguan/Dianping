package com.guan.dianping;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guan.consts.CONSTS;
import com.guan.entity.City;
import com.guan.entity.ResponseObject;
import com.guan.view.SiderBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CityActivity extends FrameActivity implements SiderBar.OnTouchingLetterChangedListener{

    @ViewInject(R.id.city_list)
    private ListView listDatas;
    private List<City> cityList;
    @ViewInject(R.id.city_side_bar)
    private SiderBar siderBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_city_list);
        ViewUtils.inject(this);

        View view = LayoutInflater.from(this).inflate(R.layout.home_city_search,
                null);
        listDatas.addHeaderView(view);
        // 执行异步任务
        new CityDataTask().execute();
        // 给自定义ViewSiderBar设置touch监听
        siderBar.setOnTouchingLetterChangedListener(this);
    }

    @OnClick({R.id.index_city_back, R.id.index_city_flushcity})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.index_city_back://返回
                finish();
                break;
            case R.id.index_city_flushcity://刷新
                new CityDataTask().execute();
                break;

            default:
                break;
        }
    }

    @OnItemClick({R.id.city_list})
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {

        Intent intent = new Intent();
        TextView textView = (TextView) view.findViewById(R.id.city_list_item_name);
        intent.putExtra("cityName", textView.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    //使用异步任务获取城市的json串
    public class CityDataTask extends AsyncTask<Void, Void, List<City>> {
        @Override
        protected List<City> doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(CONSTS.City_Data_URI);
            try {
                HttpResponse httpResponse = client.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    String jsonString = EntityUtils.toString(httpResponse.getEntity());
                    return parseCityDatasJson(jsonString);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<City> result) {
            super.onPostExecute(result);
            cityList = result;
            //适配显示
            MyAdapter adapter = new MyAdapter(cityList);
            listDatas.setAdapter(adapter);
        }
    }

    //解析城市数据的json
    public List<City> parseCityDatasJson(String json) {
        Gson gson = new Gson();
        ResponseObject<List<City>> responseObject = gson.fromJson(json, new TypeToken<ResponseObject<List<City>>>() {
        }.getType());
        return responseObject.getDatas();
    }

    //用来第一次保存首字母的索引
    private StringBuffer buffer = new StringBuffer();
    //用来保存索引值对象的城市名称
    private List<String> firdtList = new ArrayList<String>();

    //适配器
    public class MyAdapter extends BaseAdapter {
        private List<City> listCityDatas;

        public MyAdapter(List<City> listCityDatas) {
            this.listCityDatas = listCityDatas;
        }

        @Override
        public int getCount() {
            return listCityDatas.size();//返回集合的长度
        }

        @Override
        public Object getItem(int position) {
            return listCityDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_city_list_item, null);
                ViewUtils.inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            //数据显示的处理
            City city = listCityDatas.get(position);
            String sort = city.getSortKey();
            String name = city.getName();
            //索引不存在
            if (buffer.indexOf(sort) == -1) {
                buffer.append(sort);
                firdtList.add(name);
            }
            if (firdtList.contains(name)) {
                holder.keySort.setText(sort);
                holder.keySort.setVisibility(View.VISIBLE);//包含对应的城市就让索引显示
            } else {
                holder.keySort.setVisibility(View.GONE);
            }
            holder.cityName.setText(name);
            return convertView;
        }
    }

    public class Holder {
        @ViewInject(R.id.city_list_item_sort)
        public TextView keySort;
        @ViewInject(R.id.city_list_item_name)
        public TextView cityName;
    }

    /**
     * 实现SiderBar接口的方法
     * @param s
     */
    @Override
    public void onTouchingLetterChanged(String s) {
        //找到listView中显示的索引位置
        listDatas.setSelection(findIndex(cityList, s));
    }

    //根据s找到对应的s的位置
    public int findIndex(List<City> list, String s) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                City city = list.get(i);
                //根据city中的sortKey进行比较
                if (s.equals(city.getSortKey())) {
                    return i;
                }
            }
        } else {
            Toast.makeText(this, "暂无信息", Toast.LENGTH_SHORT).show();
        }
        return -1;
    }
}

