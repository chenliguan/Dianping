package com.guan.fragment;

import com.guan.dianping.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
//我的
public class FragmentMy extends Fragment {

	@ViewInject(R.id.my_index_login_text)
	private TextView loginText;
	@ViewInject(R.id.my_index_login_image)
	private ImageView loginImage;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_index, null);
		ViewUtils.inject(this, view);

		return view;
	}

}
