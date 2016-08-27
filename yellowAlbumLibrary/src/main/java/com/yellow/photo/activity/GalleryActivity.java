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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yellow.photo.activity.R;
import com.yellow.photo.util.AlbumGlobalUtils;
import com.yellow.photo.util.PublicWay;
import com.yellow.photo.util.Res;
import com.yellow.photo.zoom.PhotoView;
import com.yellow.photo.zoom.ViewPagerFixed;

/**
 * 这个是用于进行图片浏览时的界面
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

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();

	private Context mContext;

	RelativeLayout photo_relativeLayout;
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
		
		if(activityMark == Const.FROM_ALBUM_ACTIVITY){
			for (int i = 0; i < AlbumGlobalUtils.totalSelectImgs.size(); i++) {
				if(i >= 																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												AlbumGlobalUtils.totalSelectImgs.size() - tempSelectImgs){
				   initListViews(AlbumGlobalUtils.totalSelectImgs.get(i).getBitmap());
				}
			}
		}else{
			for (int i = 0; i < AlbumGlobalUtils.totalSelectImgs.size(); i++) {
				initListViews(AlbumGlobalUtils.totalSelectImgs.get(i).getBitmap());
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
		if(intent.getStringExtra("position")!=null){
		  activityMark = Integer.parseInt(intent.getStringExtra("position"));
		  Log.i("GalleryActivity", "GalleryActivity---position--"+activityMark);
	   	// 1代表album 2 代表ShowFolderPhotosActivity	
		}
	}

	private void initView() {
//		tv_del = (TextView) findViewById(Res.getWidgetID("cancel"));
		tv_del = (TextView) findViewById(R.id.cancel);
		if(activityMark == Const.FROM_ALBUM_ACTIVITY){
			tv_del.setVisibility(View.INVISIBLE);
		}else{
			tv_del.setText("删除");
		}
		back_bt = (TextView) findViewById(R.id.back);
		if(activityMark == 0){
			back_bt.setText("");
		}
		send_bt = (TextView) findViewById(Res.getWidgetID("send_button"));
		isShowOkBt();
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
			if(activityMark == Const.FROM_ALBUM_ACTIVITY){
				send_bt.setText(Res.getString("finish") +  "(" +(location+1) +"/"+tempSelectImgs+")");
			}else{
				send_bt.setText(Res.getString("finish") +  "(" +(location+1) +"/"+ AlbumGlobalUtils.totalSelectImgs.size()+")");
			}
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private void initListViews(Bitmap bm) {
		if (listViews == null){
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
			if(activityMark == 0){
				finish();
				return;
			}
			finish();
//			intent.setClass(GalleryActivity.this, ShowImageFilesActivity.class);
//			startActivity(intent);
		}
	}

	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		public void onClick(View v) {
			if (listViews.size() == 1) {
				AlbumGlobalUtils.totalSelectImgs.clear();
				AlbumGlobalUtils.max = 0;
				if(activityMark == Const.FROM_ALBUM_ACTIVITY){
					send_bt.setText(Res.getString("finish") +  "(" +(location+1) +"/"+tempSelectImgs+")");
				}else{
					send_bt.setText(Res.getString("finish") + "(" +(location+1) +"/"+ AlbumGlobalUtils.totalSelectImgs.size()+")");
				}
				Intent intent = new Intent("data.broadcast.action");
				sendBroadcast(intent);
				finish();
			} else {
				AlbumGlobalUtils.totalSelectImgs.remove(location);
				AlbumGlobalUtils.max--;
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				if(activityMark == Const.FROM_ALBUM_ACTIVITY){
					send_bt.setText(Res.getString("finish") +  "(" +(location+1) +"/"+tempSelectImgs+")");
				}else{
					send_bt.setText(Res.getString("finish") + "(" +(location+1) +"/"+ AlbumGlobalUtils.totalSelectImgs.size()+")");
				}
				adapter.notifyDataSetChanged();
			}
		}
	}

	// 完成按钮的监听
	private class GallerySendListener implements OnClickListener {
		public void onClick(View v) {
			finish();
//			BitmapAndGlobalUtils.back2MainActivity(GalleryActivity.this);
		}
	}

	public void isShowOkBt() {
		if (AlbumGlobalUtils.totalSelectImgs.size() > 0) {
			if(activityMark == Const.FROM_ALBUM_ACTIVITY){
				send_bt.setText(Res.getString("finish") +  "(" +(location+1) +"/"+tempSelectImgs+")");
			}else{
				send_bt.setText(Res.getString("finish") + "(" +(location+1) +"/"+ AlbumGlobalUtils.totalSelectImgs.size()+")");
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
			if (activityMark ==Const.FROM_ALBUM_ACTIVITY) {
				this.finish();
//				intent.setClass(GalleryActivity.this, AlbumActivity.class);
//				startActivity(intent);
			} else if (activityMark == Const.FROM_SHOWFOLDERPHOTOS_ACTIVITY) {
				this.finish();
				intent.setClass(GalleryActivity.this,ShowFolderPhotosActivity.class);
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
			}else{
			finish();
		}
		}
		return true;
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
			if(activityMark == Const.FROM_ALBUM_ACTIVITY){
				return tempSelectImgs;
			}else{
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
