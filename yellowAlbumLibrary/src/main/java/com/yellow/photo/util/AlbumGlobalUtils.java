package com.yellow.photo.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.yellow.photo.activity.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class AlbumGlobalUtils {
	public static int max = 0;
	// 剪切的头像
	public static Context context;
	public static Bitmap portrait;
	public static byte[] portraitBytes;
	public static ArrayList<ImageItem> totalSelectImgs = new ArrayList<ImageItem>(); // 选择的图片的临时列表
	public static ArrayList<ImageItem> tempSelectImgs = new ArrayList<ImageItem>();
	
	public static String MainActivityName;
	public static Intent intent = new Intent();
	public static ExecutorService service = Executors.newFixedThreadPool(10);
	public static int headViewTitleresId;
	//拍摄图片所在的文件夹
	public static String takePhotoFolder;
	public static ImageLoader imageLoader;
	public static ImageLoader albumImageLoader;
	public static DisplayImageOptions displayImgOptions0;
	public static DisplayImageOptions displayImgOptions1;
	public static HashMap<String, ImageView> imageViewMap;
	public static ArrayList<ImageView> imgeViewList;

	@SuppressWarnings("rawtypes")
	public static void back2MainActivity(Activity activity) {
		Class clazz = null;
		try {
			clazz = Class.forName(AlbumGlobalUtils.MainActivityName);
			intent.putExtra("isFromAlum", true);
			if(portraitBytes!=null&&portraitBytes.length>0){
				intent.putExtra("portraitBytes", portraitBytes);
			}
			intent.setClass(activity, clazz);
			activity.startActivity(intent);
			for (Activity act : PublicWay.activityList) {
				act.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void back2MainActivity(Activity activity, boolean goBack) {
		if (goBack) {
			back2MainActivity(activity);
		} else {
			activity.finish();
		}
	}

	/**
	 * 调用相册时必须要先初始化
	 * 
	 * @param ActivityName
	 * @param activity
	 */
	public static void initUpLoadImg(String ActivityName, Activity activity) {
		// 入口Activity的全类名com.xxx.xxx.TxCreateHelpActivity
		AlbumGlobalUtils.context = activity;
		AlbumGlobalUtils.MainActivityName = ActivityName;
		if(imageViewMap == null){
			imageViewMap = new HashMap<String, ImageView>();
		}
		if(imgeViewList == null){
			imgeViewList = new ArrayList<ImageView>();
		}
		Res.init(activity);// 初始化话ResAndroid 有自带这个方法，不需要反射去获取
	}

	/**
	 * 调用相册时必须要先初始化
	 * @param ActivityName
	 * @param activity
	 * @param resid 相册头部样式的id
	 */
	public static void initUpLoadImg(String ActivityName, Activity activity, int headViewTitleresId) {
		context = activity;
		//头部的样式
		AlbumGlobalUtils.headViewTitleresId = headViewTitleresId;
		// 入口Activity的全类名com.xxx.xxx.TxCreateHelpActivity
		AlbumGlobalUtils.MainActivityName = ActivityName;
		if(imageViewMap == null){
			imageViewMap = new HashMap<String, ImageView>();
		}
		
		if(imgeViewList == null){
			imgeViewList = new ArrayList<ImageView>();
		}
		Res.init(activity);// 初始化话ResAndroid 有自带这个方法，不需要反射去获取
	}
	
	
	public static void initImageLoader(){
		
		if(imageLoader==null||!imageLoader.isInited()){
			int maxMemory =4*1024*1024; //((int) Runtime.getRuntime().maxMemory()) / 1024 / 1024;
			int diskMemory = 10*1024*1024;
			// 缓存文件的目录
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(480, 800)
					.threadPoolSize(2)			
					// 线程池内加载的数量
					.threadPriority(Thread.NORM_PRIORITY - 1)
					.denyCacheImageMultipleSizesInMemory()
					.diskCacheFileNameGenerator(new Md5FileNameGenerator()) // 将保存的时候的URI名称用MD5													// 加密
					.memoryCache(new LargestLimitedMemoryCache (maxMemory)) 
					.memoryCacheSize(maxMemory) // 内存缓存的最大值
					.tasksProcessingOrder(QueueProcessingType.LIFO)
					.build();
			
			// 全局初始化此配置
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);
			displayImgOptions0 = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheInMemory(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .cacheOnDisk(true)
            .showImageOnLoading(R.drawable.onloading_pic0)
            .showImageOnFail(R.drawable.onloading_pic0)
            .showImageForEmptyUri(R.drawable.onloading_pic0)
            .build();
			
			displayImgOptions1 = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheInMemory(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheOnDisk(true)
            .showImageOnLoading(R.drawable.smalldefault)
            .showImageOnFail(R.drawable.smalldefault)
            .showImageForEmptyUri(R.drawable.smalldefault)
            .build(); 
		}
	}

	public static void displayImage(String url,ImageView iv){
		getImageLoader().displayImage(url, iv, getImageLoaderOptions());
	}
	
	
	/**
	 * 使用自定义的ImageLoaderOptions去加载
	 *@author xp
	 *@time 2016-4-18 下午4:04:16 
	 * @param url
	 * @param iv
	 * @param myOptions
	 */
	public static void displayImg(String url , ImageView iv, DisplayImageOptions myOptions){
		getImageLoader().displayImage(url, iv, myOptions);
	}
	
	public static void displayImage(String url, ImageView iv, SimpleImageLoadingListener listener){
		getImageLoader().displayImage(url, iv, getImageLoaderOptions(), listener);
	}
	
	public static void displayAlbumImage(String url,ImageView iv){
		getImageLoader().displayImage(url, iv, getImageLoaderOptions(), new SimpleImageLoadingListener(){
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {	
//				loadedImage = null;
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				Log.i("BitmapAndGlobalUtils", "BitmapAndGlobalUtils--onLoadingCancelled--"+imageUri);
				super.onLoadingCancelled(imageUri, view);
			}
		});
		
	}
	
	
	
	public static DisplayImageOptions getImageLoaderOptions(){
		return displayImgOptions0;
	}

	
	public static ImageLoader getImageLoader(){
		initImageLoader();
		return imageLoader;
	}
	
	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 5000)&& (options.outHeight >> i <= 5000)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	
	
}
