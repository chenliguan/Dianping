package com.guan.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.guan.dianping.R;

/**
 * 绘制对应的英文字母
 *
 * @author Guan
 * @file com.guan.view
 * @date 2015/11/6
 * @Version 1.0
 */
public class SiderBar extends View {

    private int choose;
    private OnTouchingLetterChangedListener letterChangedListener;
    // 26个字母
    private static String[] sideBar;
    // 画笔
    private Paint paint;

    /**
     *  定义监听事件接口
     */
    public interface OnTouchingLetterChangedListener {
        // 根据滑动位置的索引做出处理
        public void onTouchingLetterChanged(String s);
    }

    /*
     * new对象时调用
     */
    public SiderBar(Context context) {
        super(context);
    }

    /*
     * XML文件创建控件对象时调用
     */
    public SiderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化视图
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        // 画笔
        paint = new Paint();
        // 26个字母
        sideBar = new String[]{"热门", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.letterChangedListener = onTouchingLetterChangedListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 设置字体灰色
        paint.setColor(Color.GRAY);
        // 设置字体样式粗体
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(20);
        // 获取自定义View的宽和高
        int height = getHeight();
        int width = getWidth();
        // 设定每一个字母所在控件的高度
        int each_height = height / sideBar.length;

        // 给每一个字母绘制出来
        for (int i = 0; i < sideBar.length; i++) {
            //字体所在区域在x轴的偏移量
            float x = width / 2 - paint.measureText(sideBar[i]) / 2;
            //字体所在区域在Y轴的偏移量(中轴线对半划分)
            float y = (1 + i) * each_height;
            canvas.drawText(sideBar[i], x, y, paint);
        }
    }

    /**
     * 分发对应的touch监听
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        // 获取对应的动作
        final int action = event.getAction();
        // 点击的y坐标
        final float y = event.getY();
        final OnTouchingLetterChangedListener listener = letterChangedListener;
        // 数组中点击的字母索引 = 获取点击y轴坐标所占总高度的比例 * 数组的长度
        final int c = (int) (y / getHeight() * sideBar.length);

        switch (action) {
            //抬起
            case MotionEvent.ACTION_UP:
                setBackgroundResource(android.R.color.transparent);
                // 重新初始化界面
                flushView();
                break;

            default:
                setBackgroundResource(android.R.color.transparent);
                if (c > 0 && c < sideBar.length) {
                    if (listener != null) {
                        listener.onTouchingLetterChanged(sideBar[c]);
                    }
                    choose = c;
                    // 重新初始化界面
                    flushView();
                }
                break;
        }
        return true;
    }

    /**
     * 刷新视图
     */
    protected void flushView() {
        // 刷新当前view会导致ondraw方法的执行
        invalidate();
    }
}

