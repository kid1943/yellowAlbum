package com.yellow.clippic;

import com.yellow.photo.activity.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
public class ShowImageActivity extends Activity
{
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);

		mImageView = (ImageView) findViewById(R.id.id_showImage);
		byte[] b = getIntent().getByteArrayExtra("bitmap");
		Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
		if (bitmap != null)
		{
			mImageView.setImageBitmap(bitmap);
		}
	}
}
