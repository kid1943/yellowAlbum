package com.yellow.photo.activity;

import com.king.photo.R;
import com.yellow.photo.util.AlbumGlobalUtils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class BaseActivty extends Activity {

	RelativeLayout headview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBaseView();
	}

	@SuppressWarnings("deprecation")
	private void initBaseView() {
		headview = (RelativeLayout) findViewById(R.id.headview);
		if (AlbumGlobalUtils.headViewTitleresId != 0) {
			headview.setBackground(this.getResources().getDrawable(
					AlbumGlobalUtils.headViewTitleresId));
		}
	}
}
