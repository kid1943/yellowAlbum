package com.yellow.photo.adapter;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.king.photo.R;
import com.yellow.photo.activity.AlbumActivity;
import com.yellow.photo.util.AlbumGlobalUtils;
import com.yellow.photo.util.BitmapCache;
import com.yellow.photo.util.ImageItem;
import com.yellow.photo.util.PublicWay;
import com.yellow.photo.util.BitmapCache.ImageCallback;

/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 * @version 2014年10月18日 下午11:49:35
 */
public class AlbumGridViewAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();
	private Context mContext;
	public List<ImageItem> dataList;
	//本次进入界面时所选中的图片集合
	public ArrayList<ImageItem> tempSelectDataList = new ArrayList<ImageItem>();
	public ArrayList<ViewHolder> listConverView;
	private DisplayMetrics dm;
	BitmapCache cache;
	private OnItemClickListener mOnItemClickListener;
	public final int TAKE_PICTURE = 0x000001;
	private int selectPosition;
	//一屏幕中的所以imagiew
	public ArrayList<ImageView> imageViewList = new ArrayList<ImageView>();
	
	public AlbumGridViewAdapter(Context c, List<ImageItem> dataList , List<ImageItem> selectedDataList) {
		mContext = c;
		listConverView = new ArrayList<ViewHolder>();
		cache = BitmapCache.getBitmapCache();
		this.dataList = dataList;
		selectPosition = selectedDataList.size();
		while(selectPosition>PublicWay.SELECTIMGNUM-PublicWay.SURPLUSSELECTIMGNUM){
			tempSelectDataList.add(selectedDataList.get(selectPosition-1));
			--selectPosition;
		}		
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
	}


	public int getCount() {
	    if(dataList!=null){
	    	return dataList.size()+1;
	    }else{
	    	return 1;
	    }	
	}

	public Object getItem(int position) {
		return dataList.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap ,Object... params) {
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
	private RelativeLayout rl_camera;


	/**
	 * 存放列表项控件句柄
	 */
	private class ViewHolder {
		public ImageView imageView;
		public ToggleButton toggleButton;
		public ToggleButton choosetoggle;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
			if(position == 0){
		         viewHolder = new ViewHolder();
			     rl_camera = (RelativeLayout) LayoutInflater.from(mContext)
					                                                       .inflate(R.layout.imgupload_plugin_camera_select_imageview
					                                                		  , parent
					                                                		  ,false);
				viewHolder.toggleButton = (ToggleButton) rl_camera.findViewById(R.id.toggle_button);
			    viewHolder.choosetoggle = (ToggleButton) rl_camera.findViewById(R.id.choosedbt);
			    viewHolder.imageView = (ImageView) rl_camera.findViewById(R.id.image_view);
		
				viewHolder.choosetoggle.setVisibility(View.INVISIBLE);
				viewHolder.toggleButton.setVisibility(View.INVISIBLE);
				viewHolder.imageView.setImageResource(R.drawable.camera);
			    imageViewList.add(viewHolder.imageView);
				listConverView.add(viewHolder);
				rl_camera.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(AlbumGlobalUtils.totalSelectImgs.size() >= PublicWay.SELECTIMGNUM){
			                Toast.makeText(mContext, "超出可选张数", 0).show();
		                    return;   
						}
						Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						((Activity) mContext).startActivityForResult(openCameraIntent, TAKE_PICTURE);			
					}
				});
				rl_camera.setTag(viewHolder);
				return rl_camera;
			}
			
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.imgupload_plugin_camera_select_imageview 
																												, parent
																												, false);
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
				viewHolder.toggleButton = (ToggleButton) convertView.findViewById(R.id.toggle_button);
				viewHolder.choosetoggle = (ToggleButton) convertView.findViewById(R.id.choosedbt);
				viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.smalldefault));
			    imageViewList.add(viewHolder.imageView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.choosetoggle.setVisibility(View.VISIBLE);
				viewHolder.toggleButton.setVisibility(View.VISIBLE);
				viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.smalldefault));
			}
			
			if(AlbumActivity.isPortrait){
				viewHolder.choosetoggle.setVisibility(View.INVISIBLE);
			}else{
				viewHolder.choosetoggle.setVisibility(View.VISIBLE);
			}
			
			
			if (position != 0) {
			if (dataList != null && dataList.size() > position-1){
				try { 
				  ImageItem item = dataList.get(position - 1);
				  viewHolder.imageView.setTag(item.imagePath);
//				  BitmapAndGlobalUtils.displayAlbumImage("file://" + item.imagePath, viewHolder.imageView);
				  AlbumGlobalUtils.displayImg("file://" + item.imagePath, viewHolder.imageView, AlbumGlobalUtils.displayImgOptions1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		   }
			viewHolder.toggleButton.setTag(position-1);
			viewHolder.choosetoggle.setTag(position-1);
			if (position != 0) {
				viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));			
			} 
					
			try {
				if (tempSelectDataList.contains(dataList.get(position-1))) {
					if (tempSelectDataList.contains(dataList.get(position))) {
					viewHolder.toggleButton.setChecked(true);
					viewHolder.choosetoggle.setChecked(true);
				} else {
					viewHolder.toggleButton.setChecked(false);
					viewHolder.choosetoggle.setChecked(false);
				}
			    }
			} catch (Exception e) {
				e.printStackTrace();
			} 	
		return convertView;
	}

	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}

	/**
	 * 背景ToggleButton的点击事件
	 * @author Administrator
	 */
	private class ToggleClickListener implements OnClickListener {
		ToggleButton chooseBt;

		public ToggleClickListener(ToggleButton choosebt) {
			this.chooseBt = choosebt;
		}

		@Override
		public void onClick(View view) {
			if (view instanceof ToggleButton) {
				ToggleButton toggleButton = (ToggleButton) view;
				int position = (Integer) toggleButton.getTag();
				if (dataList != null && mOnItemClickListener != null&& position < dataList.size()) {
					mOnItemClickListener.onItemClick(toggleButton, position
							                                     , toggleButton.isChecked()
							                                     , chooseBt);
				}
			}
		}
	}

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnItemClickListener {
		public void onItemClick(ToggleButton view, int position,
				boolean isChecked, ToggleButton chooseBt);
	}
	
	public void setDataList(List<ImageItem> list){
		this.dataList = list;
	}
	
	public List<ImageItem> getDataList(){
		return this.dataList;
	}
	
	public void clearAlbumImageView(){
        for(ImageView view : imageViewList){
        	view.setImageResource(0);
        	view.setImageDrawable(null);
        }
	}
	
	
	
}
