package com.yellow.photo.popupwin;

import com.yellow.photo.activity.R;
import com.yellow.photo.adapter.FolderAdapter;
import com.yellow.photo.adapter.FolderAdapter.FolderSelectListener;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;

public class FolderPopupWin extends PopupWindow {
	private View mMenuView;
	private ListView lv_list_dir;
	private FolderAdapter folderAdapter;
    private FolderSelectListener listener;
	
	public FolderPopupWin(Activity activity, FolderSelectListener listener) {
		super(activity);
		this.listener = listener;
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.list_dir_layout, null);
		lv_list_dir = (ListView) mMenuView.findViewById(R.id.lv_list_dir);
//		ViewGroup.LayoutParams params;
//		params = lv_list_dir.getLayoutParams();
		WindowManager manager = (WindowManager) 
				activity.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int height = display.getHeight();
		height = (int) (0.65*height);
		Log.i("FolderPopupWin", "FolderPopupWin---"+height);
/*		  if(params==null){
				params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
			}else{
				params.height = height;
				params.width = ViewGroup.LayoutParams.MATCH_PARENT;
			}
		  lv_list_dir.setLayoutParams(params);*/
		
		lv_list_dir.setAdapter(new FolderAdapter(activity,listener));
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(height);
		this.setFocusable(true);
	}
}
