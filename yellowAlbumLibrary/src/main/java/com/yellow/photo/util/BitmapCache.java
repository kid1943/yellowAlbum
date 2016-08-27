package com.yellow.photo.util;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.yellow.photo.util.ImageLoader.Type;

public class BitmapCache extends Activity {

	public Handler h = new Handler();
	public final String TAG = getClass().getSimpleName();
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private Bitmap bmp;
	Bitmap thumb;
	private static BitmapCache bitMapCache;
	 BitmapCache() {

	}

	public static BitmapCache getBitmapCache() {
		if (bitMapCache == null) {
			bitMapCache = new BitmapCache();
			Log.e("BitmapCache", "BitmapCache is null");
		}else{
			Log.e("BitmapCache", "BitmapCache is exit");
		}
		return bitMapCache;
	}


	public void displayBmp(final ImageView iv, final String sourcePath) {
		try {
			ImageLoader.getInstance(5, Type.LIFO).loadImage(sourcePath, iv);
		} catch (Throwable throwable) {
			System.gc();
		}
	}

	

	// 压图片
	public Bitmap revitionImageSize(String path) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 256)
					&& (options.outHeight >> i <= 256)) {
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeFile(path, options);
				break;
			}
			i += 1;
		}
		options = null;
		return bitmap;
	}

	public interface ImageCallback {
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params);
	}
}
