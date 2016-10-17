package com.yellow.photo.activity;

import com.yellow.customview.MyToolBar;
import com.yellow.photo.util.AlbumUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class BaseActivty extends Activity {

    RelativeLayout headview;
    protected MyToolBar toolbar;
    protected MenuItem menuitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBaseView();
    }

    @SuppressWarnings("deprecation")
    private void initBaseView() {
        toolbar = (MyToolBar) findViewById(R.id.toolbar);
        headview = (RelativeLayout) findViewById(R.id.headview);
        initToolBar();
        if (AlbumUtils.headViewTitleresId != 0) {
            headview.setBackground(this.getResources().getDrawable(AlbumUtils.headViewTitleresId));
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
