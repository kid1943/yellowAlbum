package com.yellow.photo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.yellow.photo.adapter.FolderAdapter;
import com.yellow.photo.adapter.FolderAdapter.FolderSelectListener;
import com.yellow.photo.util.AlbumGlobalUtils;
import com.yellow.photo.util.PublicWay;
import com.yellow.photo.util.Res;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 * 
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:48:06
 */
public class ShowImageFilesActivity extends BaseActivty {

	private FolderAdapter folderAdapter;
	private TextView tv_cancel;
	private TextView back;
	private Context mContext;
	private String ActivityName;
	private GridView gridView;
	private TextView tv_title;

	protected void onCreate(Bundle savedInstanceState) {
		ActivityName = getIntent().getStringExtra("ActivityName");
		setContentView(Res.getLayoutID("imgupload_plugin_camera_image_file"));
		super.onCreate(savedInstanceState);
		PublicWay.activityList.add(this);
		mContext = this;
		initView();

		tv_cancel.setOnClickListener(new CancelListener());
		tv_title = (TextView) findViewById(Res.getWidgetID("title"));
		tv_title.setText(Res.getString("photo"));
		folderAdapter = new FolderAdapter(this,new FolderSelectListener() {

			@Override
			public void selectComplete(Object obj, String folderName) {
				
			}
		});
		gridView.setAdapter(folderAdapter);
	}

	private void initView() {
		tv_cancel = (TextView) findViewById(Res.getWidgetID("cancel"));
//		tv_cancel.setText("所有照片");
		tv_cancel.setVisibility(View.GONE);
		tv_cancel.setText("");
		back = (TextView) findViewById(Res.getWidgetID("back"));
		gridView = (GridView) findViewById(Res.getWidgetID("fileGridView"));
		back.setOnClickListener(new CancelListener());
//		back.setVisibility(View.INVISIBLE);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			// 清空选择的图片
			// BimpNGlobalUtils.tempSelectBitmap.clear();
			AlbumGlobalUtils.back2MainActivity(ShowImageFilesActivity.this,
					false);
		}
	}

}
