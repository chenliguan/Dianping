package com.guan.dianping;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.guan.entity.Goods;
import com.guan.entity.Shop;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.loopj.android.image.SmartImageView;


public class GoodsDetailActivity extends FrameActivity {
    @ViewInject(R.id.goods_image)
    private SmartImageView goods_image;
    @ViewInject(R.id.goods_title)
    private TextView goods_title;
    @ViewInject(R.id.goods_desc)
    private TextView goods_desc;
    @ViewInject(R.id.shop_title)
    private TextView shop_title;
    @ViewInject(R.id.shop_phone)
    private TextView shop_phone;
    @ViewInject(R.id.goods_price)
    private TextView goods_price;
    @ViewInject(R.id.goods_old_price)
    private TextView goods_old_price;
    @ViewInject(R.id.tv_more_details_web_view)
    private WebView tv_more_details_web_view;
    @ViewInject(R.id.wv_gn_warm_prompt)
    private WebView wv_gn_warm_prompt;
    private Goods goods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuan_goods_detail);
        ViewUtils.inject(this);
        // TextView的文字中划线效果
        goods_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        // 让网页自适应屏幕
        WebSettings webSettings = tv_more_details_web_view.getSettings();
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        WebSettings webSettings1 = wv_gn_warm_prompt.getSettings();
        webSettings1.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            goods = (Goods) bundle.get("goods");
        }
        if (goods != null) {
            // 更新页面上所有的内容
            updateTitleImage();
            updateGoodsInfo();
            updateShopInfo();
            updateMoreDetails();
        }

    }

    @OnClick({R.id.shop_call, R.id.goods_detail_goback})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shop_call:
                Intent callin = new Intent(Intent.ACTION_DIAL);
                callin.setData(Uri.parse("tel:" + goods.getShop().getTel()));
                startActivity(callin);
                break;
            case R.id.goods_detail_goback:
                finish();
            default:
                break;
        }

    }

    // 更新商品的标题图片
    public void updateTitleImage() {
//		Picasso.with(this).load(goods.getImgUrl())
//				.placeholder(R.drawable.ic_empty_dish).into(goods_image);
        goods_image.setImageUrl(goods.getImgUrl(), R.mipmap.ic_empty_dish, R.mipmap.ic_empty_dish);
    }

    //商品的标题，显示，价钱处理
    public void updateGoodsInfo() {
        goods_title.setText(goods.getSortTitle());
        goods_desc.setText(goods.getTip());
        goods_price.setText("￥" + goods.getPrice());
        goods_old_price.setText("￥" + goods.getValue());
    }

    public void updateShopInfo() {
        Shop shop = goods.getShop();
        shop_title.setText(shop.getName());
        shop_phone.setText(shop.getTel());
    }

    public void updateMoreDetails() {
        String[] data = htmlSub(goods.getDetail());
        tv_more_details_web_view.loadDataWithBaseURL("", data[1], "text/html", "utf-8", "");
        wv_gn_warm_prompt.loadDataWithBaseURL("", data[0], "text/html", "utf-8", "");
    }

    /*
     * <div class="prodetail-sp"><h4 style="background:#ff6600">
     * 【本单详情】</h4><p class="ti">凭拉手券享受西萨蛋糕一款，10寸约2.5磅精品水果蛋糕任选1款/8寸月饼蛋糕一份，市场价238元，现在团购仅售<strong class="f18" style="color:#ff6600;">98</strong>元。</p><p>注：1磅≈453.59237克</p><p style="text-align:center"><a style="color:#00ccff" href="http://beijing.lashou.com/deal/7260985.html" target="_new"><img src="http://img.lashou.com/wg/beijing/201209/21/7260985.jpg" border="0" /></a></p><h4 style="background:#ff6600">
     * 【温馨提示】</h4><ul><li><p>本次团购仅限配送地址为北京市五环内地区的用户购买，免费配送； &nbsp; &nbsp; </p></li><li><p>拉手券于2012年9月25日（周二）生效； &nbsp; &nbsp; </p></li><li><p>拉手券有效期截止至延期至2014年07月31日，2013年2月9-15号 不可以使用； &nbsp; &nbsp; </p></li><li><p>营业时间见地址栏； &nbsp; &nbsp; </p></li><li><p><span style="color:#ff6600;">请您至少提前1天预约订购，商家于9月25日开始接受预约，电话见地址栏，预定时请告知您订购的蛋糕名称，并提供拉手券号和密码； </span> &nbsp; &nbsp;</p></li><li><p><span style="color:#ff6600;">请您在预定时详细说明收货人的联系方式及收货地址，服务人员将在配送时会提前与您联系，希望您能保持送货当日手机畅通，如超过约定时间无法联系，服务人员将放弃配送，由此产生的责任由收货人自行承担； &nbsp;</span> &nbsp; &nbsp; </p></li><li><p>蛋糕均为送货当日制作，建议1天内食用完，本产品0-10℃可保藏24小时，5℃食用最佳； &nbsp; &nbsp; </p></li><li><p>每张拉手券仅限享用一个蛋糕，每个蛋糕附送刀叉盘5套； &nbsp; &nbsp; </p></li><li><p>由于产品的特殊性，蛋糕一经订购非质量问题不予退换； &nbsp; &nbsp; </p></li><li><p>拉手券不兑现、不找零，不与店内其他优惠同时享用。 </p></li></ul><h4 style="background:#ff6600">
     * 【精品展示】</h4><p><strong>阿雅客</strong></p><p class="tc"><img src="http://img.lashou.com/wg/beijing/201209/21/ayk1.jpg" /></p><p><strong>粉色佳人</strong></p><p class="tc"><img src="http://img.lashou.com/wg/beijing/201209/24/fsjr1.jpg" /></p><p><strong>黄桃经典</strong></p><p class="tc"><img src="http://img.lashou.com/wg/beijing/201209/24/htjd1.jpg" /></p><p><strong>极品诱惑</strong></p><p class="tc"><img src="http://img.lashou.com/wg/beijing/201209/24/jpyh1.jpg" /></p><p><strong>科西嘉</strong></p><p class="tc"><img src="http://img.lashou.com/wg/beijing/201209/24/kxj1.jpg" /></p><p><strong>秘与密</strong></p><p class="tc"><img src="http://img.lashou.com/wg/beijing/201209/24/mym1.jpg" /></p><p><strong>盛之情</strong></p><p class="tc"><img src="http://img.lashou.com/wg/beijing/201209/24/szq1.jpg" /></p><p><strong>意浓</strong></p><p class="tc"><img src="http://img.lashou.com/wg/beijing/201209/24/yn1.jpg" /></p><p><strong>月饼蛋糕 </strong>密豆/蓝莓/草莓/黄桃馅</p><p class="tc"><img src="http://img.lashou.com/wg/beijing/201209/24//ybdg1.jpg" /></p><h4 style="background:#ff6600">西萨西饼屋</h4><p class="ti">西萨西饼屋主要以经营西式面包、西式糕点为主。面包品种繁多，主要有花色面包和夹心面包类。店内还可根据顾客需要订做生日蛋糕，样式美观新颖、价格合理！公司成立以来一直坚持以“高效率、高标准、高品质”为企业服务宗旨，力争能为广大消费者提供最完美的产品与服务！</p></div>
     */
    /**
     * 解析这个html类型的字符串  分别获取 本单详情  温馨提示  精品展示
     */
    public String[] htmlSub(String html) {
        char[] str = html.toCharArray();
        int len = str.length;
        int n = 0;
        String[] data = new String[3];
        int oneIndex = 0;
        int secIndex = 1;
        int ThiIndex = 2;
        for (int i = 0; i < len; i++) {
            if (str[i] == '【') {
                n++;
                if (n == 1) oneIndex = i;
                if (n == 2) secIndex = i;
                if (n == 3) ThiIndex = i;
            }
        }
        if (oneIndex > 0 && secIndex > 1 && ThiIndex > 2) {
            data[0] = html.substring(oneIndex, secIndex);
            data[1] = html.substring(secIndex, ThiIndex);
            data[2] = html.substring(ThiIndex, html.length() - 6);
        }
        return data;
    }
}
