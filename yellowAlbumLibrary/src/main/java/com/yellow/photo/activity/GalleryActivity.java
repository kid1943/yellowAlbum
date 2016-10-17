package com.yellow.photo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yellow.customview.MyToolBar;
import com.yellow.photo.util.AlbumGlobalUtils;
import com.yellow.photo.util.PublicWay;
import com.yellow.photo.util.Res;
import com.yellow.photo.zoom.PhotoView;
import com.yellow.photo.zoom.ViewPagerFixed;

/**
 * 这个是用于进行图片浏览时的界面
 *
 * @version 2015年10月18日 下午11:47:53
 */
public class GalleryActivity extends BaseActivty {

    private Intent intent;
    // 返回按钮
    private TextView back_bt;
    // 发送按钮
    private TextView send_bt;
    // 删除按钮
    private TextView tv_del;
    // 顶部显示预览图片位置的textview
    private TextView positionTextView;
    // 获取前一个activity传过来的标记
    private int activityMark = 0;
    // 当前的位置
    private int location = 0;

    private ArrayList<View> listViews = null;
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;

    private Context mContext;
    private int tempSelectImgs;//本次在相册中选中的图片数量

    @Override
    public void onCreate(Bundle savedInstanceState) {
//		setContentView(Res.getLayoutID("imgupload_plugin_camera_gallery"));// 切屏到主界面
        setContentView(R.layout.imgupload_plugin_camera_gallery);
        super.onCreate(savedInstanceState);
        PublicWay.activityList.add(this);
        mContext = this;
        initData();
        initView();

        back_bt.setOnClickListener(new BackListener());
        send_bt.setOnClickListener(new GallerySendListener());
        tv_del.setOnClickListener(new DelListener());

        // 为发送按钮设置文字
        pager = (ViewPagerFixed) findViewById(Res.getWidgetID("gallery01"));
        pager.setOnPageChangeListener(pageChangeListener);

        if (activityMark == Const.FROM_ALBUM_ACTIVITY) {
            for (int i = 0; i < AlbumGlobalUtils.totalSelImgs.size(); i++) {
                if (i >= AlbumGlobalUtils.totalSelImgs.size() - tempSelectImgs) {
                    initListViews(AlbumGlobalUtils.totalSelImgs.get(i).getBitmap());
                }
            }
        } else {
            for (int i = 0; i < AlbumGlobalUtils.totalSelImgs.size(); i++) {
                initListViews(AlbumGlobalUtils.totalSelImgs.get(i).getBitmap());
            }
        }
        adapter = new MyPageAdapter(listViews);
        pager.setAdapter(adapter);
        pager.setPageMargin((int) getResources().getDimensionPixelOffset(Res.getDimenID("ui_10_dip")));
        int id = intent.getIntExtra("ID", 0);
        pager.setCurrentItem(id);
    }

    private void initData() {
        intent = getIntent();
        tempSelectImgs = intent.getIntExtra("tempSelectImgs", 0);
        if (intent.getStringExtra("position") != null) {
            activityMark = Integer.parseInt(intent.getStringExtra("position"));
            // 1代表album 2 代表ShowFolderPhotosActivity
        }
    }

    private void initView() {
        tv_del = (TextView) findViewById(R.id.cancel);
        if (activityMark == Const.FROM_ALBUM_ACTIVITY) {
            tv_del.setVisibility(View.INVISIBLE);
        } else {
            tv_del.setText("删除");
        }
        back_bt = (TextView) findViewById(R.id.back);
        if (activityMark == 0) {
            back_bt.setText("");
        }
        send_bt = (TextView) findViewById(Res.getWidgetID("send_button"));
        isShowOkBt();
    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            location = arg0;
            if (activityMark == Const.FROM_ALBUM_ACTIVITY) {
                send_bt.setText(Res.getString("finish") + "(" + (location + 1) + "/" + tempSelectImgs + ")");
            } else {
                send_bt.setText(Res.getString("finish") + "(" + (location + 1) + "/" + AlbumGlobalUtils.totalSelImgs.size() + ")");
            }
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void initListViews(Bitmap bm) {
        if (listViews == null) {
            listViews = new ArrayList<View>();
        }
        PhotoView img = new PhotoView(this);
        img.setBackgroundColor(0xff000000);
        img.setImageBitmap(bm);
        img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        listViews.add(img);
    }

    // 返回按钮添加的监听器
    private class BackListener implements OnClickListener {
        public void onClick(View v) {
            back();
        }
    }


    // 删除按钮添加的监听器
    private class DelListener implements OnClickListener {
        public void onClick(View v) {
            delPic();
        }
    }

    // 完成按钮的监听
    private class GallerySendListener implements OnClickListener {
        public void onClick(View v) {
            finish();
        }
    }

    public void isShowOkBt() {
        if (AlbumGlobalUtils.totalSelImgs.size() > 0) {
            if (activityMark == Const.FROM_ALBUM_ACTIVITY) {
                send_bt.setText(Res.getString("finish") + "(" + (location + 1) + "/" + tempSelectImgs + ")");
            } else {
                send_bt.setText(Res.getString("finish") + "(" + (location + 1) + "/" + AlbumGlobalUtils.totalSelImgs.size() + ")");
            }
            send_bt.setPressed(true);
            send_bt.setClickable(true);
            send_bt.setTextColor(Color.WHITE);
        } else {
            send_bt.setPressed(false);
            send_bt.setClickable(false);
            send_bt.setTextColor(Color.parseColor("#E1E0DE"));
        }
    }

    /**
     * 监听返回按钮
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (activityMark == Const.FROM_ALBUM_ACTIVITY) {
                this.finish();
            } else if (activityMark == Const.FROM_SHOWFOLDERPHOTOS_ACTIVITY) {
                this.finish();
                intent.setClass(GalleryActivity.this, ShowFolderPhotosActivity.class);
                startActivity(intent);
            } else if (activityMark == Const.FROM_OUTSIDE_ACTIVITY) {
                this.finish();
                Intent intent = new Intent();
                Class clazz = null;
                try {
                    clazz = Class.forName(AlbumGlobalUtils.MainActivityName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                intent.setClass(mContext, clazz);
                startActivity(intent);
            } else {
                finish();
            }
        }
        return true;
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        toolbar.setTitle("图片预览");
        menuitem.setTitle("删除");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                delPic();
                return true;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }


    /**
     * 返回上一级
     * Created by yellow on 10:31  2016/10/17.
     */
    private void back() {
        if (activityMark == 0) {
            finish();
            return;
        }
        finish();
    }


    /**
     * 删除正在浏览的照片
     * Created by yellow on 10:29  2016/10/17.
     */
    private void delPic() {
        if (listViews.size() == 1) {
            AlbumGlobalUtils.totalSelImgs.clear();
            AlbumGlobalUtils.max = 0;
            if (activityMark == Const.FROM_ALBUM_ACTIVITY) {
                send_bt.setText(Res.getString("finish") + "(" + (location + 1) + "/" + tempSelectImgs + ")");
            } else {
                send_bt.setText(Res.getString("finish") + "(" + (location + 1) + "/" + AlbumGlobalUtils.totalSelImgs.size() + ")");
            }
            Intent intent = new Intent("data.broadcast.action");
            sendBroadcast(intent);
            finish();
        } else {
            AlbumGlobalUtils.totalSelImgs.remove(location);
            AlbumGlobalUtils.max--;
            pager.removeAllViews();
            listViews.remove(location);
            adapter.setListViews(listViews);
            if (activityMark == Const.FROM_ALBUM_ACTIVITY) {
                send_bt.setText(Res.getString("finish") + "(" + (location + 1) + "/" + tempSelectImgs + ")");
            } else {
                send_bt.setText(Res.getString("finish") + "(" + (location + 1) + "/" + AlbumGlobalUtils.totalSelImgs.size() + ")");
            }
            adapter.notifyDataSetChanged();
        }
    }

    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            if (activityMark == Const.FROM_ALBUM_ACTIVITY) {
                return tempSelectImgs;
            } else {
                return size;
            }
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}
