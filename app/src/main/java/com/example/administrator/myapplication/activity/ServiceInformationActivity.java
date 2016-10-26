package com.example.administrator.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.myapplication.Adapter.JiageBaseAdapter;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.entity.Category;
import com.example.administrator.myapplication.entity.Price;
import com.example.administrator.myapplication.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ServiceInformationActivity extends AppCompatActivity {

    @InjectView(R.id.tv_tbTitle)
    TextView tvTbTitle;
    @InjectView(R.id.tb_title)
    Toolbar tbTitle;
    @InjectView(R.id.lv_yuyue)
    ListView lvYuyue;
    @InjectView(R.id.tv_tishi)
    TextView tvTishi;
    @InjectView(R.id.tv_jiesao)
    TextView tvJiesao;
    @InjectView(R.id.iv_photo)
    ImageView ivPhoto;
    @InjectView(R.id.iv_head)
    ImageView ivHead;

    Category category;
    int Hid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_information);
        ButterKnife.inject(this);


        tbTitle.setTitle("");
        tbTitle.setNavigationIcon(R.mipmap.backs);
        setSupportActionBar(tbTitle);
        //返回
        tbTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String str = intent.getStringExtra("category");
            if (intent.getIntExtra("hid", 0) != 0) {
                Hid = intent.getIntExtra("hid", 0);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<Category>() {
            }.getType();
            category = gson.fromJson(str, type);
            Log.i("Service", "onCreate: " + category);
            if (category.getName() != null) {
                tvTbTitle.setText(category.getName());
            }
            if (category.getPhoto() != null) {
                Log.i("Service", "onCreate: " + "getphoto");
                String url = StringUtil.ip + category.getPhoto();
                ImageOptions imageOptions = new ImageOptions.Builder()
                        .setSize(DensityUtil.dip2px(385), DensityUtil.dip2px(200))
                        .setUseMemCache(true).build();

                Log.i("Service", "onCreate: " + url);
                x.image().bind(ivPhoto, url, imageOptions);
            }
            if (category.getPrompt() != null) {
                tvTishi.setText(category.getPrompt());
            }
            if (category.getProfile() != null) {
                tvJiesao.setText(category.getProfile());
            }
            if (category.getPrices() != null) {
                List<Price> list = category.getPrices();
                Log.i("Service", "onCreate: " + list.size());
                JiageBaseAdapter jiage = new JiageBaseAdapter(list, this);
                lvYuyue.setAdapter(jiage);
                fixListViewHeight(lvYuyue);
                //jiage.notifyDataSetChanged();
            }
            lvYuyue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //跳转到预约界面
                    Log.i("Service", "onItemClick: 跳转到预约");
                    Intent intent1 = new Intent(ServiceInformationActivity.this, OrderActivity.class);
                    intent1.putExtra("category", category);
                    intent1.putExtra("price", position);
                    if (Hid != 0) {
                        intent1.putExtra("hid", Hid);
                    }
                    startActivity(intent1);
                }
            });
        }
    }

    public void fixListViewHeight(ListView listView) {
        // 如果没有设置数据适配器，则ListView没有子项，返回。
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        if (listAdapter == null) {
            return;
        }
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listViewItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listViewItem.measure(0, 0);
            // 计算所有子项的高度和
            totalHeight += listViewItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // listView.getDividerHeight()获取子项间分隔符的高度
        // params.height设置ListView完全显示需要的高度
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 20;
        listView.setLayoutParams(params);
    }

    @OnClick(R.id.iv_head)
    public void onClick() {
        showShare();
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare share = new OnekeyShare();
        share.disableSSOWhenAuthorize();
        share.setText(category.getSlogan());
        // text是分享文本，所有平台都需要这个字段
        share.setTitle("家政服务");
        // url仅在微信（包括好友和朋友圈）中使用
        share.setUrl("http://sweetystory.com/");
        share.setTitleUrl("http://sweetystory.com/");
        share.setImageUrl("http://sweetystory.com/Public/ttwebsite/theme1/style/img/special-1.jpg");

        share.show(this);


    }
}
