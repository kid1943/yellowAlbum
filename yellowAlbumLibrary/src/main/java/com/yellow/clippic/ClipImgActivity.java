package com.yellow.clippic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.yellow.photo.activity.Const;
import com.yellow.photo.activity.R;
import com.yellow.clippic.view.ClipImageLayout;
import com.yellow.photo.activity.BaseActivty;

public class ClipImgActivity extends BaseActivty {

	private ClipImageLayout mClipImageLayout;
	private String imgpath;
	private Bitmap bitmap4Clip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.clipimg_layout);
		super.onCreate(savedInstanceState);
		imgpath = getIntent().getStringExtra(Const.IMG_PATH);
		initView();
	}

	@Override
	protected void initToolBar() {
		super.initToolBar();
		toolbar.setTitle("剪切图片");
		menuitem.setTitle("剪切");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				clipImg();
				return true;
			}
		});

		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipImgActivity.this.finish();
			}
		});
	}

	private void initView() {
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		// 获取屏幕宽高（方法1）
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth() ; // 屏幕宽（像素，如：480px）
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight() ; // 屏幕高（像素，如：800p）
		BitmapFactory.Options option = new Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgpath, option);// 有用的
		// 调用上面定义的方法计算inSampleSize值
		option.inSampleSize = calculateInSampleSize(option, screenWidth, screenHeight);
		option.inJustDecodeBounds = false;
		bitmap4Clip = BitmapFactory.decodeFile(imgpath, option);
		mClipImageLayout.mZoomImageView.setImageBitmap(bitmap4Clip);
	}

	private int calculateInSampleSize(Options option, int reqWidth
			                                        , int reqHeight) {
		// 源图片的宽度
		int width = option.outWidth;
		int height = option.outHeight;
		int inSampleSize = 1;
		if (width > reqWidth && height > reqHeight) {
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	private void clipImg() {
		Bitmap bitmap = mClipImageLayout.clip();
		bitmap4Clip.recycle();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
		byte[] datas = baos.toByteArray();

        Random random = new Random();
		int ran = random.nextInt(1000);
		File file = new File(this.getExternalCacheDir().toString() + "/"+ran+".jpeg");
		Log.i("ClipImgActivity", "clipImg---"+file.getPath());
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(datas);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//剪切图片的字节流
		Intent intent = new Intent();
		intent.putExtra("bitmap", datas);
		intent.putExtra("cutimgpath", file.toString());
		setResult(0, intent);
		bitmap.recycle();
		finish();
	}
}
