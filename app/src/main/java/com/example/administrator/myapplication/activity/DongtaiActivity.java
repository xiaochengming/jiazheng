package com.example.administrator.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.Adapter.CommonAdapter;
import com.example.administrator.myapplication.Adapter.ViewHolder;
import com.example.administrator.myapplication.Application.MyApplication;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.entity.Dynamic;
import com.example.administrator.myapplication.entity.Post;
import com.example.administrator.myapplication.entityMi.Zan;
import com.example.administrator.myapplication.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DongtaiActivity extends AppCompatActivity {

    @InjectView(R.id.touxiang)
    ImageView touxiang;
    @InjectView(R.id.luntan_listitem_textView_name)
    TextView luntanListitemTextViewName;
    @InjectView(R.id.luntan_listitem_textView_time)
    TextView luntanListitemTextViewTime;
    @InjectView(R.id.luntan_listitem_textView_content)
    TextView luntanListitemTextViewContent;
    @InjectView(R.id.luntan_listitem_photo_list)
    LinearLayout luntanListitemPhotoList;
    @InjectView(R.id.luntan_listitem_horizontalview)
    HorizontalScrollView luntanListitemHorizontalview;
    @InjectView(R.id.layout_tiezhi)
    LinearLayout layoutTiezhi;
    @InjectView(R.id.luntan_listitem_layout)
    LinearLayout luntanListitemLayout;
    @InjectView(R.id.lv_tiezi_pinglun)
    ListView lvTieziPinglun;
    @InjectView(R.id.tv_tbTitle)
    TextView tvTbTitle;
    @InjectView(R.id.tb_workerlist)
    Toolbar tbWorkerlist;
    @InjectView(R.id.luntan_listitem_textView_pinglun)
    TextView luntanListitemTextViewPinglun;
    @InjectView(R.id.luntan_listitem_textView_cishu)
    TextView pinglunCishu;
    @InjectView(R.id.luntan_listitem_textView_dianzan)
    ImageView ivdianzan;
    @InjectView(R.id.luntan_listitem_textView_geshu)
    TextView dianzanliang;
    @InjectView(R.id.layout_tiezhibuju)
    LinearLayout layoutTiezhibuju;


    Post post;
    List<Dynamic> list = new ArrayList<Dynamic>();
    CommonAdapter<Dynamic> adapter;
    @InjectView(R.id.et_fasong_pinglun)
    EditText etFasongPinglun;
    @InjectView(R.id.show_luntan_text_enterpinglun)
    TextView showLuntanTextEnterpinglun;

    MyApplication myApplication;
    @InjectView(R.id.dibubuju)
    LinearLayout dibubuju;

    InputMethodManager imm;
    int louceng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongtai);
        ButterKnife.inject(this);
        myApplication = (MyApplication) getApplication();
        initTiezi();//初始化帖子数据
        getPinglun();//获取帖子评论 并设置listview
        inittoobar();//初始化toolbar
        initEven();//初始化事件
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    public void initEven() {
        //评论帖子
        layoutTiezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dibubuju.setVisibility(View.VISIBLE);
                imm.showSoftInput(showLuntanTextEnterpinglun, InputMethodManager.SHOW_FORCED);
                louceng = 0;


            }
        });
    }

    public void inittoobar() {
        tbWorkerlist.setNavigationIcon(R.mipmap.back);
        tbWorkerlist.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //初始化帖子数据
    public void initTiezi() {
        Intent intent = getIntent();
        post = intent.getParcelableExtra("post");

        if (post.getUser() != null && post.getUser().getPhoto() != null) {
            ImageOptions imageOptions = new ImageOptions.Builder().
                    setLoadingDrawableId(R.mipmap.ic_launcher).
                    setRadius(DensityUtil.dip2px(30.0f)).build();
            x.image().bind(touxiang, StringUtil.ip + "/" + post.getUser().getPhoto(), imageOptions);
            luntanListitemTextViewName.setText(post.getUser().getName());
        }
        luntanListitemTextViewContent.setText(post.getPostContent());
        luntanListitemTextViewTime.setText(post.getPostTimes() + "");
        gengxintiezhi();
    }

    public void gengxintiezhi() {
        pinglunCishu.setText(post.getPingLunnum() + "");
        if (post.getiszan()) {
            ivdianzan.setImageResource(R.mipmap.good_checked);
        } else {
            ivdianzan.setImageResource(R.mipmap.good);
        }
        dianzanliang.setText(post.getNumber() + "");
    }

    //获取评论数据
    public void getPinglun() {
        RequestParams params = new RequestParams(StringUtil.ip + "/MiSelectPinglun");
        params.addQueryStringParameter("postId", post.getPostId() + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type type = new TypeToken<List<Dynamic>>() {
                }.getType();
                List<Dynamic> listTemp = gson.fromJson(result, type);
                list.clear();
                list.addAll(listTemp);
                listViewshezhishipeiqi();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("DongtaiActivity", "onError: " + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //listview s设置适配器
    public void listViewshezhishipeiqi() {
        if (adapter == null) {
            lvTieziPinglun.setAdapter(new CommonAdapter<Dynamic>(DongtaiActivity.this, list, R.layout.pinglun_item) {
                @Override
                public void convert(ViewHolder viewHolder, Dynamic dynamic, int position) {
                    ImageView touxiang = viewHolder.getViewById(R.id.listview_pinglun_item_imageview_icon);
                    TextView name = viewHolder.getViewById(R.id.listview_pinglun_item_textView_name);
                    TextView lou = viewHolder.getViewById(R.id.listview_pinglun_item_textView_lou);
                    TextView pinglun = viewHolder.getViewById(R.id.listview_pinglun_item_textView_location);
                    TextView shijian = viewHolder.getViewById(R.id.listview_pinglun_item_textView_time);
                    if (dynamic.getUser() != null && dynamic.getUser().getPhoto() != null) {
                        ImageOptions imageOptions = new ImageOptions.Builder().
                                setLoadingDrawableId(R.mipmap.ic_launcher).
                                setRadius(DensityUtil.dip2px(30.0f)).build();
                        x.image().bind(touxiang, StringUtil.ip + "/" + dynamic.getUser().getPhoto(), imageOptions);
                        name.setText(dynamic.getUser().getName());
                    }
                    int x = position + 1;
                    int b = dynamic.getParent();
                    if (b != 0) {
                        lou.setText("第" + x + "楼   回复" + b + "楼");
                    } else {
                        lou.setText("第" + x + "楼");
                    }
                    pinglun.setText(dynamic.getDynamicContent());
                    shijian.setText(dynamic.getDynamicTime() + "");
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    @OnClick({R.id.luntan_listitem_textView_dianzan, R.id.et_fasong_pinglun})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.luntan_listitem_textView_dianzan:
                //点赞
                boolean flag = false;
                if (post.getiszan()) {

                } else {
                    flag = true;
                }
                dianzan(view, flag, post);
                break;
            case R.id.et_fasong_pinglun:
                //底部发表评论
                String string = showLuntanTextEnterpinglun.getText().toString();
                if (string == null) {
                    Toast.makeText(DongtaiActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                Dynamic dynamic = null;
                if (louceng == 0) {
                    //回复帖子
                    dynamic = new Dynamic(0, myApplication.getUser(), post.getPostId(), string, new Timestamp(System.currentTimeMillis()), 0, 0);

                } else {
                    int x = list.get(louceng - 1).getDynamicId();
                    dynamic = new Dynamic(0, myApplication.getUser(), post.getPostId(), string, new Timestamp(System.currentTimeMillis()), x, 0);
                }
                //隐藏输入框
                imm.hideSoftInputFromWindow(showLuntanTextEnterpinglun.getWindowToken(), 0);
                //刷新界面和插入数据库
                insertPinglun(dynamic);
                break;
        }
    }

    //刷新界面和插入数据库
    public void insertPinglun(Dynamic dynamic) {
        //刷新界面
        List<Dynamic> newList=new ArrayList<>();
       for (int i=0;i<newList.size();i++){

         //  if (newList.get(i).)
       }
    }

    //点赞后改变图标
    public void dianzan(final View v, final boolean flag, final Post post) {
        int a = 0;
        if (flag) {
            post.setNumber(post.getNumber() + 1);
            a = 1;
        } else {
            post.setNumber(post.getNumber() - 1);
            a = 0;
        }
        post.setIszan(flag);
        Zan zan = new Zan(post.getPostId(), myApplication.getUser().getUserId(), a);
        //更新界面
        gengxintiezhi();
//        if (flag) {
//                    ((ImageView) v).setImageResource(R.mipmap.good_checked);
//                    dianzanliang
//
//                } else {
//                    ((ImageView) v).setImageResource(R.mipmap.good);
//
//                }
        //修改数据库
        RequestParams params = new RequestParams(StringUtil.ip + "/DianzanServlet");
        Gson gson = new Gson();
        String str = gson.toJson(zan);
        params.addQueryStringParameter("zan", str);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    //对
    private List<Dynamic> sortList(List<Dynamic> list) {

        List<Dynamic> jieguolist = new ArrayList<Dynamic>();
        for (Dynamic dynamic : list) {
            if (dynamic.getParent() == 0) {

                jieguolist.add(dynamic);
                sortSubList(list, dynamic, jieguolist);

            }
        }
        return jieguolist;
    }

    private void sortSubList(List<Dynamic> list, Dynamic dynamic,
                             List<Dynamic> jieguolist) {

        if (dynamic.getHasNext() == 0) {
            return;
        }
        for (Dynamic dynamic2 : list) {
            if (dynamic2.getParent() == dynamic.getDynamicId()) {
                jieguolist.add(dynamic2);
                sortSubList(list, dynamic2, jieguolist);
            }
        }

    }
}