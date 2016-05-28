package com.yellow.clippic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.king.photo.R;
import com.yellow.clippic.view.ClipImageLayout;
import com.yellow.photo.activity.BaseActivty;

public class ClipImgActivity extends BaseActivty {
	private ClipImageLayout mClipImageLayout;
	private String imgpath;
	private TextView tv_clip;
	private Drawable drawable;
	private Bitmap bitmap4Clip;
	private TextView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.clipimg_layout);
		super.onCreate(savedInstanceState);
		imgpath = getIntent().getStringExtra("imgpath");
		initView();
		initListener();
	}

	private void initListener() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipImgActivity.this.finish();		
			}
		});
		
		tv_clip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clipImg();
			}
		});
	}

	private void initView() {
		// 获取屏幕宽高（方法1）
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth() / 3; // 屏幕宽（像素，如：480px）
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight() / 3; // 屏幕高（像素，如：800p）
		tv_clip =  (TextView) findViewById(R.id.cancel);
		tv_clip.setText("使用");
		back = (TextView) findViewById(R.id.back);
		
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);

		// drawable = Drawable.createFromPath(imgpath);
		BitmapFactory.Options option = new Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgpath, option);// 有用的
		// 调用上面定义的方法计算inSampleSize值
		option.inSampleSize = calculateInSampleSize(option, screenWidth, screenHeight);
		option.inJustDecodeBounds = false;
		bitmap4Clip = BitmapFactory.decodeFile(imgpath, option);
		Log.i("ClipImgActivity  ", "被剪切图片的大少---" + bitmap4Clip.getByteCount()/ 1024 + "kb");
		mClipImageLayout.mZoomImageView.setImageBitmap(bitmap4Clip);
		// mClipImageLayout.mZoomImageView.setImageDrawable(drawable);
	}

	private int calculateInSampleSize(Options option, int reqWidth,
			int reqHeight) {
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
		File file = new File(this.getExternalCacheDir().toString()
				+ "/potrait.jpeg");
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
