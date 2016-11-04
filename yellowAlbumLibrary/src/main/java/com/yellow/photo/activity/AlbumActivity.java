package com.yellow.photo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.PopupWindow.OnDismissListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yellow.clippic.ClipImgActivity;
import com.yellow.photo.adapter.AlbumGridViewAdapter;
import com.yellow.photo.adapter.AlbumGridViewAdapter.OnItemClickListener;
import com.yellow.photo.adapter.FolderAdapter.FolderSelectListener;
import com.yellow.photo.popupwin.FolderPopupWin;
import com.yellow.photo.util.AlbumHelper;
import com.yellow.photo.util.AlbumManager;
import com.yellow.photo.util.Cons;
import com.yellow.photo.util.FileUtils;
import com.yellow.photo.util.ImageBucket;
import com.yellow.photo.util.ImageItem;
import com.yellow.photo.util.PublicWay;

/**
 * 这个是进入相册显示所有图片的界面
 */
public class AlbumActivity extends BaseActivty {


    // 显示手机里的所有图片的列表控件
    private GridView gridView;
    // 当手机里没有图片时，提示用户没有图片的控件
    private TextView tv;
    // gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    // 左下角按钮
    private TextView tv_folders;
    private Intent intent;
    // 预览按钮
    private TextView preview;
    public static ArrayList<ImageItem> dataList;// 所以图片的集合
    private ArrayList<ImageItem> folderImgList;//某个文件夹里的所有图片集合
    private AlbumHelper helper;// 数据库
    public static List<ImageBucket> contentList;// 图片文件夹集合
    private RelativeLayout rl_bottom_layout;
    public static boolean isPortrait; // 判断是否是要选择裁剪的图片
    public int selectImgNum;// 选择图片的数目
    private RelativeLayout bottom_layout;
    public final int TAKE_PICTURE = 0x000001;
    private String cutimgpath; // 剪切图片的路径
    private FolderPopupWin folderPopupWin;
    //本次进入相册一共选中的图片
    private int tempSelectImgs = 0;


    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.imgupload_plugin_camera_album);
        isPortrait = getIntent().getBooleanExtra(Cons.IS_PORTRAIT, false);// 判断是否选择裁剪图片
        super.onCreate(savedInstanceState);
        // 初始化要选择的图片数目
        selectImgNum = getIntent().getIntExtra(Cons.SELECT_COUNT, 3);
        PublicWay.SELECTIMGNUM = selectImgNum;
        PublicWay.activityList.add(this);
        PublicWay.SURPLUS_SEL_NUM = PublicWay.SELECTIMGNUM - AlbumManager.selImgList.size();
        // 注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver, filter);
        initPhotoData();
        initView();
        initListener();
        // 这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            gridImageAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        clearData();
        super.onDestroy();
    }

    // 预览按钮的监听
    private class PreviewListener implements OnClickListener {
        public void onClick(View v) {
            if (tempSelectImgs > 0) {
                intent.putExtra("position", "1");
                intent.putExtra("isFromAlbumActivity", true);
                intent.putExtra("tempSelectImgs", tempSelectImgs);
                intent.setClass(AlbumActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * 选择完成
     * Created by yellow on 16:31  2016/10/14.
     */
    private void selectComplete() {
        overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
        AlbumManager.back2MainActivity(AlbumActivity.this);
        AlbumActivity.this.finish();
    }

    /**
     * 返回后退相册时做相应的处理
     * Created by yellow on 17:05  2016/10/14.
     */
    private void backHandle() {
        int total = AlbumManager.selImgList.size();
        int removeIndex = 0;

        if (total > 0 && tempSelectImgs > 0) {
            removeIndex = total - tempSelectImgs;
            for (int i = removeIndex; i < total; i++) {
                try {
                    AlbumManager.selImgList.remove(AlbumManager.selImgList.size() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        AlbumActivity.this.finish();
        AlbumManager.getImageLoader().clearMemoryCache();
    }

    // 取消按钮的监听
    private class CancelListener implements OnClickListener {
        public void onClick(View v) {
            // 去掉选择的图片上的钩子
            if (AlbumManager.selImgList.size() == 0) {
                AlbumActivity.this.finish();
                return;
            }
            AlbumManager.selImgList.clear();
            gridImageAdapter.notifyDataSetChanged();
            tv_folders.setText(AlbumActivity.this.getResources().getString(R.string.finish) + "("
                    + (PublicWay.SURPLUS_SEL_NUM
                    - (selectImgNum - AlbumManager.selImgList.size())) + "/"
                    + PublicWay.SURPLUS_SEL_NUM + ")");
        }
    }

    // 初始化，给一些对象赋值
    private void initView() {
        folderPopupWin = new FolderPopupWin(AlbumActivity.this, new FolderSelectListener() {
            @Override
            public void selectComplete(Object obj, String folderName) {
                folderImgList = (ArrayList<ImageItem>) obj;
                gridImageAdapter.setDataList(folderImgList);
                gridImageAdapter.notifyDataSetChanged();
                folderPopupWin.dismiss();
                tv_folders.setText(folderName);
            }
        });

        folderPopupWin.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        rl_bottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
        if (isPortrait) {
            AlbumManager.selImgList.clear();
            rl_bottom_layout.setVisibility(View.GONE);
        }
        bottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
        preview = (TextView) findViewById(R.id.preview);
        preview.setOnClickListener(new PreviewListener());
        intent = getIntent();
        gridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, AlbumManager.selImgList);
        gridView.setAdapter(gridImageAdapter);
        tv = (TextView) findViewById(R.id.myText);
        gridView.setEmptyView(tv);
        tv_folders = (TextView) findViewById(R.id.ok_button);
        menuitem.setTitle("完成" + "("
                + (PublicWay.SURPLUS_SEL_NUM
                - (selectImgNum - AlbumManager.selImgList.size())) + "/"
                + PublicWay.SURPLUS_SEL_NUM + ")");

        boolean pauseOnScroll = true; // or true
        boolean pauseOnFling = true; // or false
        PauseOnScrollListener listener = new PauseOnScrollListener(AlbumManager.getImageLoader(), pauseOnScroll, pauseOnFling);
        gridView.setOnScrollListener(listener);
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        if(AlbumManager.headColorId != 0){
            toolbar.setBackgroundColor(this.getResources().getColor(AlbumManager.headColorId));
        }
        toolbar.setTitle("相册");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectComplete();
                return true;
            }
        });

        if (isPortrait) {
            toolbar.setTitle("剪切图片");
            menuitem.setVisible(false);
            menuitem.setEnabled(false);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHandle();
            }
        });
    }

    /**
     * 查询相册数据库的信息
     *
     * @author xp
     * @time 2015-12-17 下午6:51:26
     */
    private void initPhotoData() {
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        if (contentList != null) {
            contentList.clear();
            contentList = null;
        }
        contentList = helper.getImagesBucketList(true);//boolean值用于判断当再次进入相册时是否重新查找一遍数据库
        if (dataList != null) {
            dataList.clear();
            dataList = null;
        }
        dataList = new ArrayList<ImageItem>();
        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }
        try {
            Collections.sort(dataList);
        } catch (Exception ex) {

        }
    }

    private void initListener() {
        tv_folders.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                folderPopupWin.setAnimationStyle(R.style.anim_popup_dir);
                folderPopupWin.showAsDropDown(rl_bottom_layout, 0, 0);
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });

        // 先判断是否是头像选择
        if (!isPortrait) {
            // 选择图片上传
            selectImg();
        } else {
            // 选择要剪切的图片
            selectNclipImg();
        }
    }

    private void selectNclipImg() {
        gridImageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ToggleButton view, int position, boolean isChecked, ToggleButton chooseBt) {
                String path = dataList.get(position).getImagePath();
                Intent intent = new Intent(AlbumActivity.this, ClipImgActivity.class);
                intent.putExtra("imgpath", path);
                startActivityForResult(intent, 0);
            }
        });
    }


    private void selectImg() {
        // 列出所以图片时的监听
        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(final ToggleButton toggleButton, int position
                                                                           , boolean isChecked
                                                                           , ToggleButton chooseBt) {
                        synchronized (this) {
                            if (AlbumManager.selImgList.size() >= PublicWay.SELECTIMGNUM) {
                                toggleButton.setChecked(false);
                                boolean isCheck = chooseBt.isChecked();
                                if (isCheck) {
                                    chooseBt.setChecked(false);
                                    if (folderImgList == null) {
                                        tempSelectImgs--;
                                        AlbumManager.selImgList.remove(dataList.get(position));
                                        gridImageAdapter.tempSelectDataList.remove(dataList.get(position));
                                    } else {
                                        tempSelectImgs--;
                                        AlbumManager.selImgList.remove(folderImgList.get(position));
                                        gridImageAdapter.tempSelectDataList.remove(folderImgList.get(position));
                                    }
                                    menuitem.setTitle("完成" + "("
                                            + (PublicWay.SURPLUS_SEL_NUM
                                            - (selectImgNum - AlbumManager.selImgList.size())) + "/"
                                            + PublicWay.SURPLUS_SEL_NUM + ")");
                                } else {
                                    Toast.makeText(AlbumActivity.this, "超出可选张数", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            if (isChecked) {
                                chooseBt.setChecked(true);
                                if (folderImgList == null) {
                                    tempSelectImgs++;
                                    AlbumManager.selImgList.add(dataList.get(position));
                                    gridImageAdapter.tempSelectDataList.add(dataList.get(position));
                                } else {
                                    tempSelectImgs++;
                                    AlbumManager.selImgList.add(folderImgList.get(position));
                                    gridImageAdapter.tempSelectDataList.add(folderImgList.get(position));
                                }

                                menuitem.setTitle("完成" + "("
                                        + (PublicWay.SURPLUS_SEL_NUM
                                        - (selectImgNum - AlbumManager.selImgList.size())) + "/"
                                        + PublicWay.SURPLUS_SEL_NUM + ")");
                            } else {
                                if (folderImgList == null) {
                                    tempSelectImgs--;
                                    AlbumManager.selImgList.remove(dataList.get(position));
                                    gridImageAdapter.tempSelectDataList.remove(dataList.get(position));
                                } else {
                                    tempSelectImgs--;
                                    AlbumManager.selImgList.remove(folderImgList.get(position));
                                    gridImageAdapter.tempSelectDataList.remove(folderImgList.get(position));
                                }
                                chooseBt.setChecked(false);
                                menuitem.setTitle("完成" + "("
                                        + (PublicWay.SURPLUS_SEL_NUM
                                        - (selectImgNum - AlbumManager.selImgList.size())) + "/"
                                        + PublicWay.SURPLUS_SEL_NUM + ")");
                            }
                            isShowOkBt();
                        }
                    }
                });
    }

    public void isShowOkBt() {
        menuitem.setTitle("完成" + "("
                                 + (PublicWay.SURPLUS_SEL_NUM
                                 - (selectImgNum - AlbumManager.selImgList.size())) + "/"
                                 + PublicWay.SURPLUS_SEL_NUM + ")");
        preview.setPressed(true);
        tv_folders.setPressed(true);
        preview.setClickable(true);
        tv_folders.setClickable(true);
        tv_folders.setTextColor(Color.WHITE);
        preview.setTextColor(Color.WHITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                String sdPath = null;
                Log.e("TAKE_PICTURE", "TAKE_PICTURE-----");
                if (AlbumManager.selImgList.size() < PublicWay.SELECTIMGNUM && resultCode == Activity.RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    sdPath = Environment.getExternalStorageDirectory() + "/" + AlbumManager.takePhotoFolder + "/";
                    FileUtils.saveBitmap(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setImagePath(sdPath + fileName + ".jpeg");
                    takePhoto.setBitmap(bm);
                    AlbumManager.selImgList.add(takePhoto);

                    if (isPortrait) {
                        String path = AlbumManager.selImgList.get(0).getImagePath();
                        Intent intent = new Intent(AlbumActivity.this, ClipImgActivity.class);
                        intent.putExtra("imgpath", path);
                        startActivityForResult(intent, 0);
                        AlbumManager.selImgList.clear();
                    } else {
                        AlbumManager.back2MainActivity(AlbumActivity.this);
                        AlbumActivity.this.finish();
                    }
                    return;
                }
                break;
            case 0:
                if (data != null) {
                    cutimgpath = data.getStringExtra("cutimgpath");
                }
                break;
        }

        if (data != null) {
            try {
                AlbumManager.portraitBytes = data.getByteArrayExtra("bitmap");
                AlbumManager.portrait = BitmapFactory.decodeByteArray(
                        data.getByteArrayExtra("bitmap"), 0,
                        data.getByteArrayExtra("bitmap").length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AlbumManager.back2MainActivity(AlbumActivity.this);
    }

    @Override
    protected void onRestart() {
        if (!isPortrait) {
            isShowOkBt();
        }
        super.onRestart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PublicWay.SELECTIMGNUM = intent.getIntExtra("selectnum", 3);
    }

    @Override
    public void onBackPressed() {
        finish();
    }



    public void clearData() {
        for (Activity activity : PublicWay.activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        PublicWay.activityList.clear();
        gridImageAdapter.clearAlbumImageView();
        AlbumManager.getImageLoader().clearMemoryCache();
        AlbumManager.imageViewMap.clear();
        if (contentList != null) {
            contentList.clear();
        }
        if (dataList != null) {
            dataList.clear();
        }
        try {
            gridImageAdapter.getDataList().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
