package com.yellow.photo.adapter;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.yellow.photo.activity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yellow.photo.activity.AlbumActivity;
import com.yellow.photo.activity.ShowFolderPhotosActivity;
import com.yellow.photo.util.BitmapCache;
import com.yellow.photo.util.ImageBucket;
import com.yellow.photo.util.ImageItem;
import com.yellow.photo.util.Res;
import com.yellow.photo.util.BitmapCache.ImageCallback;

/**
 * 这个是显示所有包含图片的文件夹的适配器
 */
public class FolderAdapter extends BaseAdapter {

	private Context mContext;
	private Intent mIntent;
	private DisplayMetrics dm;
	BitmapCache cache;
	final String TAG = getClass().getSimpleName();
	ImageLoader	imageLoader;
	DisplayImageOptions options;
	//对应文件夹的图片信息集合
	private ArrayList<ImageItem> dataList;
	//文件夹选择监听
	private FolderSelectListener listner;
	
	public FolderAdapter(Context c, FolderSelectListener listner) {
		cache =  BitmapCache.getBitmapCache();
		this.listner = listner;
		init(c);
		initImageLoaderOptions();
	}

	private void initImageLoaderOptions() {
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	// 初始化
	public void init(Context c) {
		mContext = c;
		mIntent = ((Activity) mContext).getIntent();
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
	}

	@Override
	public int getCount() {
		return AlbumActivity.contentList.size()+1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	private class ViewHolder {
		public TextView tv_dir_item_name;
		public TextView tv_dir_item_count;
		public ImageView iv_choose;
		public ImageView iv_dir_item_image;
	}

	ViewHolder holder = null;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_dir_item,null);
			holder = new ViewHolder();
		    holder.iv_choose = (ImageView) convertView.findViewById(R.id.iv_choose);
		    holder.tv_dir_item_count = (TextView) convertView.findViewById(R.id.tv_dir_item_count);
		    holder.tv_dir_item_name = (TextView) convertView.findViewById(R.id.tv_dir_item_name);
		    holder.iv_dir_item_image = (ImageView) convertView.findViewById(R.id.iv_dir_item_image);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		String path = null;
		
		if(position == 0){
			try {
				path = AlbumActivity.contentList.get(0).imageList.get(0).imagePath;
				holder.tv_dir_item_name.setText("所有图片");
				holder.tv_dir_item_count.setText(""+AlbumActivity.dataList.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			if (AlbumActivity.contentList.get(position-1).imageList != null) {
				try {
					// 封面图片路径
					path = AlbumActivity.contentList.get(position-1).imageList.get(0).imagePath;
					// 给folderName设置值为文件夹名称
					holder.tv_dir_item_name.setText(AlbumActivity.contentList.get(position-1).bucketName);
					// 给fileNum设置文件夹内图片数量
					holder.tv_dir_item_count.setText(""+ AlbumActivity.contentList.get(position-1).count);
				} catch (Exception e) {
					Log.e("FolderAdapter", "FolderAdapter-----"+e.getMessage());
					e.printStackTrace();
				}

			} else{
				path = "android_hybrid_camera_default";
			}
		}
		
		
		if (path!=null && path.contains("android_hybrid_camera_default"))
			holder.iv_dir_item_image.setImageResource(Res.getDrawableID("plugin_camera_no_pictures"));
		else {
			ImageItem item = null;
			if(position == 0){
				item = AlbumActivity.contentList.get(0).imageList.get(0);
			}else{
				 try {
					item = AlbumActivity.contentList.get(position-1).imageList.get(0);
				} catch (Exception e) {
					Log.i("FolderAdapter", "FolderAdapter---"+e.getMessage());
					e.printStackTrace();
				}
			}
			
			holder.iv_dir_item_image.setTag(item.imagePath);
			imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage("file://"+item.imagePath, holder.iv_dir_item_image,options);
		}
		// 添加监听
		convertView.setOnClickListener(new ImageViewClickListener(
				position, mIntent, holder.iv_choose));
		return convertView;
	}

	// 为每一个文件夹构建的监听器
	private class ImageViewClickListener implements OnClickListener {
		private int position;
		private Intent intent;
		private ImageView choose_back;

		public ImageViewClickListener(int position, Intent intent,
				ImageView choose_back) {
			this.position = position;
			this.intent = intent;
			this.choose_back = choose_back;
		}

		public void onClick(View v) {
			if(position==0){
				listner.selectComplete(AlbumActivity.dataList,"所有图片");
			}else{
			    dataList = (ArrayList<ImageItem>) AlbumActivity.contentList
					                                   .get(position-1).imageList;
                listner.selectComplete(dataList,AlbumActivity.contentList.get(position-1).bucketName);
			}
		    choose_back.setVisibility(v.INVISIBLE);
		}
	}

	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}
	
	public interface FolderSelectListener{
		  void selectComplete(Object obj,String folderName);
	}
	
}
