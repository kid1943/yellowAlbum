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
import android.widget.ImageView;
import com.yellow.photo.activity.AlbumActivity;
import com.yellow.photo.activity.Const;
import com.yellow.photo.activity.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class AlbumManager {
	public static int max = 0;
	// 剪切的头像
	public static Context context;
	public static Bitmap portrait;
	public static byte[] portraitBytes;
	public static ArrayList<ImageItem> selImgList = new ArrayList<ImageItem>();
	public static String cutImgPath = "";

	public static Intent intent = new Intent();
	public static ExecutorService service = Executors.newFixedThreadPool(10);
	public static int headColorId;
	//拍摄图片所在的文件夹
	public static String takePhotoFolder;
	public static ImageLoader imageLoader;
	public static DisplayImageOptions displayImgOptions0;
	public static DisplayImageOptions displayImgOptions1;
	public static HashMap<String, ImageView> imageViewMap;
	public static ArrayList<ImageView> imgeViewList;

	@SuppressWarnings("rawtypes")
	public static void back2MainActivity() {
		try {
			intent.putExtra("isFromAlum", true);
			if(portraitBytes != null && portraitBytes.length > 0){
				intent.putExtra("portraitBytes", portraitBytes);
			}
			for (Activity act : PublicWay.activityList) {
				act.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 /**
	  * 打开相册
	  * Created by yellow on 12:21  2016/11/4.
	  */
	public static void openAlbum(UsePerpose perpose, int count){
		if(count <= 0){
            count = 3;
		}
		Intent intent = new Intent(context, AlbumActivity.class);
		switch (perpose){
			case CUT_PIC:
				//剪切图片
				intent.putExtra(Const.IS_PORTRAIT, true);
				break;
			case SEL_PIC:
				//选择图片
                intent.putExtra(Const.SELECT_COUNT, count);
				break;
		}
		context.startActivity(intent);
	}


	 /**
	  * 设置titlebar的颜色
	  * Created by yellow on 12:19  2016/11/4.
	  */
	public static void setHeadViewTitleCololr(int headViewTitleresId){
		//头部的样式
		AlbumManager.headColorId = headViewTitleresId;
	}

	//调用相册时必须要先初始化
	public static void initLoadImgConfig(Activity activity) {
		// 入口Activity的全类名com.xxx.xxx.TxCreateHelpActivity
		AlbumManager.context = activity;
		takePhotoFolder = activity.getResources().getString(R.string.album_name);
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

	public static void displayImg(String url , ImageView iv, DisplayImageOptions myOptions){
		getImageLoader().displayImage(url, iv, myOptions);
	}
	
	public static void displayImage(String url, ImageView iv, SimpleImageLoadingListener listener){
		getImageLoader().displayImage(url, iv, getImageLoaderOptions(), listener);
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
