package com.yellow.photo.activity;

import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yellow.clippic.ClipImgActivity;
import com.yellow.photo.adapter.AlbumGridViewAdapter;
import com.yellow.photo.adapter.AlbumGridViewAdapter.OnItemClickListener;
import com.yellow.photo.popupwin.FolderPopupWin;
import com.yellow.photo.util.AlbumUtils;
import com.yellow.photo.util.ImageItem;
import com.yellow.photo.util.ImageLoader;
import com.yellow.photo.util.PublicWay;
import com.yellow.photo.util.Res;

/**
 * 这个是显示一个文件夹里面的所有图片时的界面
 *
 * @version 2015年10月18日 下午11:49:10
 */
public class ShowFolderPhotosActivity extends BaseActivty {
	private GridView gridView;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	// 完成按钮
	private TextView okButton;
	// 预览按钮
	private TextView preview;
	// 返回按钮
	private TextView back;
	// 取消按钮
	private TextView header_meanu_right_btn;
	// 标题
	private TextView headTitle;
	private Intent intent;
	private Context mContext;
	// 在folderAdapter中被初始化
	public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
	// bottom
	RelativeLayout rl_showallphoto_bottom_layout;
	// 图片文件夹
	FolderPopupWin menuPopupWindow;

	// 剪切图片的路径
	private String cutimgpath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(Res
				.getLayoutID("imgupload_plugin_camera_show_all_photo"));
		super.onCreate(savedInstanceState);
	     PublicWay.activityList.add(this);
		initView();
		this.intent = getIntent();
		String folderName = intent.getStringExtra("folderName");// 图片列表所属文件名
		if (folderName.length() > 8) {
			folderName = folderName.substring(0, 9) + "...";
		}
		headTitle.setText(folderName);
//		header_meanu_right_btn.setOnClickListener(new CancelListener());
		back.setOnClickListener(new BackListener(intent));
		preview.setOnClickListener(new PreviewListener());
		init();
		initListener();
		isShowOkBt();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (ImageLoader.getInstance().mTasks != null) {
			Log.e("ShowAllPhotoss-stoppppppppp",
					"清理线程----" + ImageLoader.getInstance().mTasks.size());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			System.gc();
			unregisterReceiver(broadcastReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		mContext = this;
		back = (TextView) findViewById(Res.getWidgetID("back"));
		header_meanu_right_btn = (TextView) findViewById(Res.getWidgetID("cancel"));
		preview = (TextView) findViewById(Res
				.getWidgetID("showallphoto_preview"));
		okButton = (TextView) findViewById(Res
				.getWidgetID("ok_button"));
		headTitle = (TextView) findViewById(Res
				.getWidgetID("title"));
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			gridImageAdapter.notifyDataSetChanged();
		}
	};

	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (AlbumUtils.selImgList.size() > 0) {
				intent.putExtra("position", "2");
				intent.setClass(ShowFolderPhotosActivity.this,
						GalleryActivity.class);
				startActivity(intent);
			}
		}
	}

	private class BackListener implements OnClickListener {// 返回按钮监听
		Intent intent;

		public BackListener(Intent intent) {
			this.intent = intent;
		}

		public void onClick(View v) {
			intent.setClass(ShowFolderPhotosActivity.this, ShowImageFilesActivity.class);
			startActivity(intent);
			ShowFolderPhotosActivity.this.finish();
		}
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			if (AlbumUtils.selImgList.size() == 0) {
				unregisterReceiver(broadcastReceiver);
				finish();
				return;
			}
			// 清空选择的图片
			AlbumUtils.selImgList.clear();
			gridImageAdapter.notifyDataSetChanged();
			okButton.setText(Res.getString("finish") + "("
					+ (PublicWay.SURPLUS_SEL_NUM
					-(8- AlbumUtils.selImgList.size())) + "/"
					+ PublicWay.SURPLUS_SEL_NUM + ")");
			okButton.setText(Res.getString("finish") + "("
					+ (PublicWay.SURPLUS_SEL_NUM
							-(8- AlbumUtils.selImgList.size())) + "/"
							+ PublicWay.SURPLUS_SEL_NUM + ")");
		}
	}

	private void init() {
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		progressBar = (ProgressBar) findViewById(Res
				.getWidgetID("showallphoto_progressbar"));
		progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(Res
				.getWidgetID("showallphoto_myGrid"));
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList,
				AlbumUtils.selImgList);
		gridView.setAdapter(gridImageAdapter);
		rl_showallphoto_bottom_layout = (RelativeLayout) findViewById(Res
				.getWidgetID("rl_showallphoto_bottom_layout"));

		if (AlbumActivity.isPortrait) {
			rl_showallphoto_bottom_layout.setVisibility(View.GONE);
		}
	}

	private void initListener() {

		if (!AlbumActivity.isPortrait) {
			selectImg();
		} else {
			// 选择要剪切的图片
			selectNclipImg();
		}

	}

	private void selectNclipImg() {
		gridImageAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(ToggleButton view, int position,
					boolean isChecked, ToggleButton chooseBt) {
				String path = dataList.get(position).getImagePath();
				Intent intent = new Intent(ShowFolderPhotosActivity.this,
						ClipImgActivity.class);
				intent.putExtra("imgpath", path);
				startActivityForResult(intent, 0);
			}
		});

	}

	private void selectImg() {
		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked, ToggleButton button) {
						if (AlbumUtils.selImgList.size() >= PublicWay.SELECTIMGNUM
								&& isChecked) {
							toggleButton.setChecked(false);
							button.setVisibility(View.GONE);
							int i = button.getVisibility();
							if(i == View.VISIBLE){
								AlbumUtils.selImgList
								.remove(dataList.get(position));
							}
							return;
						}
						if (isChecked) {
							button.setVisibility(View.VISIBLE);
							AlbumUtils.selImgList.add(dataList
									.get(position));
							
							
							okButton.setText(Res.getString("finish") + "("
									+ (PublicWay.SURPLUS_SEL_NUM
											-(8- AlbumUtils.selImgList.size())) + "/"
											+ PublicWay.SURPLUS_SEL_NUM + ")");
							header_meanu_right_btn.setText(Res.getString("finish") + "("
									+ (PublicWay.SURPLUS_SEL_NUM
											-(8- AlbumUtils.selImgList.size())) + "/"
											+ PublicWay.SURPLUS_SEL_NUM + ")");
						} else {
							button.setVisibility(View.GONE);
							AlbumUtils.selImgList.remove(dataList
									.get(position));
							okButton.setText(Res.getString("finish") + "("
									+ (PublicWay.SURPLUS_SEL_NUM
											-(8- AlbumUtils.selImgList.size())) + "/"
											+ PublicWay.SURPLUS_SEL_NUM + ")");
							header_meanu_right_btn.setText(Res.getString("finish") + "("
									+ (PublicWay.SURPLUS_SEL_NUM
											-(8- AlbumUtils.selImgList.size())) + "/"
											+ PublicWay.SURPLUS_SEL_NUM + ")");
						}
						isShowOkBt();
					}
				});

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				okButton.setClickable(false);
				AlbumUtils.back2MainActivity(ShowFolderPhotosActivity.this);
			}
		});
		
		header_meanu_right_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				okButton.setClickable(false);
				AlbumUtils.back2MainActivity(ShowFolderPhotosActivity.this);
			}
		});
		

	}

	public void isShowOkBt() {
		if (!AlbumActivity.isPortrait) {
			okButton.setText(Res.getString("finish") + "("
					+ (PublicWay.SURPLUS_SEL_NUM
					-(8- AlbumUtils.selImgList.size())) + "/"
					+ PublicWay.SURPLUS_SEL_NUM + ")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			preview.setTextColor(Color.WHITE);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			intent.setClass(ShowFolderPhotosActivity.this, ShowImageFilesActivity.class);
			startActivity(intent);
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			cutimgpath = data.getStringExtra("cutimgpath");
			AlbumUtils.portrait = BitmapFactory.decodeByteArray(
					data.getByteArrayExtra("bitmap"), 0,
					data.getByteArrayExtra("bitmap").length);
		}
		AlbumUtils.back2MainActivity(ShowFolderPhotosActivity.this);
	}

	@Override
	protected void onRestart() {
		isShowOkBt();
		super.onRestart();
	}

}
