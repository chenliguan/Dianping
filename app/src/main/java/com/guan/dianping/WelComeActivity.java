package com.guan.dianping;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.guan.utils.SharedUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 欢迎页面
 *
 * @author Guan
 * @file com.guan.dianping.activity
 * @date 2015/11/3
 * @Version 1.0
 */
public class WelComeActivity extends FrameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler(new Handler.Callback() {
			//处理接收到的消息的方法
			@Override
			public boolean handleMessage(Message msg) {
				//实现页面跳转
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				return false;
			}
		}).sendEmptyMessageDelayed(0, 3000);//表示延时三秒进行任务的执行
        //使用java中的定时器进行处理
        Timer timer = new Timer();
        timer.schedule(new Task(), 3000);//定时器延时执行任务的方法
    }

    class Task extends TimerTask {

        @Override
        public void run() {
            //实现页面的跳转
            //不是第一次启动
            if (SharedUtils.getWelcomeBoolean(getBaseContext())) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }else{
                startActivity(new Intent(WelComeActivity.this, WelcomeGuideActivity.class));
                //保存访问记录
                SharedUtils.putWelcomeBoolean(getBaseContext(), true);
            }
            finish();
        }

    }
}

