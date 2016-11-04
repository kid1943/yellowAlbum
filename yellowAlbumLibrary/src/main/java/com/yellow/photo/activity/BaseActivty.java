package com.yellow.photo.activity;

import com.yellow.customview.MyToolBar;
import com.yellow.photo.util.AlbumManager;
import com.yellow.photo.util.PublicWay;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class BaseActivty extends Activity {

    protected MyToolBar toolbar;
    protected MenuItem menuitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PublicWay.activityList.add(this);
        initBaseView();
    }

    private void initBaseView() {
        toolbar = (MyToolBar) findViewById(R.id.toolbar);
        initToolBar();
        if (AlbumManager.headColorId != 0) {
            toolbar.setBackgroundColor(this.getResources().getColor(AlbumManager.headColorId));
        }
    }

    /**
     * 初始化toolbar
     * Created by yellow on 17:34  2016/10/14.
     */
    protected void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.inflateMenu(R.menu.complete_menu);
        menuitem = toolbar.getMenu().getItem(0);
    }
}
