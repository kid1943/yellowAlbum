package com.yellow.photo.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

@TargetApi(Build.VERSION_CODES.FROYO)
public class BitmapCache {
	private LruCache<String, Bitmap> mCache;
	public BitmapCache() {
		// int maxSize = 5 * 1024 * 1024;
		int maxSize = (int) Runtime.getRuntime().maxMemory() / 8;
		mCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}

		};
	}

}
