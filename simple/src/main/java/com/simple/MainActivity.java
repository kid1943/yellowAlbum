package com.simple;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.yellow.photo.activity.GalleryActivity;
import com.yellow.photo.util.AlbumManager;
import com.yellow.photo.util.FileUtils;
import com.yellow.photo.util.ImageItem;
import com.yellow.photo.util.PublicWay;
import com.yellow.photo.util.Res;
import com.yellow.photo.util.UsePerpose;

import java.io.File;

/**
 *测试界面
 * Created by yellow on 14:59  2016/5/28.onKeyDown
 */
public class MainActivity extends Activity {

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private Button btn_cut;
    private ImageView iv_cut;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeDir();
        AlbumManager.initLoadImgConfig(MainActivity.this);
        Res.init(this);// 初始化话ResAndroid 有自带这个方法，不需要反射去获取
        parentView = getLayoutInflater().inflate(R.layout.imgupload_activity_selectimg,null);
        setContentView(parentView);
        initView();
   }

    @Override
    protected void onStart() {
        AlbumManager.selImgList.size();
        if(!TextUtils.isEmpty(AlbumManager.cutImgPath)){
            AlbumManager.displayImg("file://" + AlbumManager.cutImgPath, iv_cut, AlbumManager.displayImgOptions1);
        }
        adapter.update();
        super.onStart();
    }

   public void initView() {
       iv_cut = (ImageView) parentView.findViewById(R.id.iv_cut);
       btn_cut = (Button) parentView.findViewById(R.id.btn_cut);
       pop = new PopupWindow(MainActivity.this);
       pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
     // 弹出选择图片源的popwin
       View view = getLayoutInflater().inflate(R.layout.imgupload_item_popupwindows, null);
       ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);// popwin主布局
       pop.setWidth(LayoutParams.MATCH_PARENT);
       pop.setHeight(LayoutParams.WRAP_CONTENT);
       pop.setBackgroundDrawable(new BitmapDrawable());
       pop.setFocusable(true);
       pop.setOutsideTouchable(true);
       pop.setContentView(view);
       RelativeLayout parent = (RelativeLayout) view.findViewById(Res.getWidgetID("parent"));
       Button bt1 = (Button) view.findViewById(Res.getWidgetID("item_popupwindows_camera"));
       Button go2Album = (Button) view.findViewById(Res.getWidgetID("item_popupwindows_Photo"));
       Button bt3 = (Button) view.findViewById(Res.getWidgetID("item_popupwindows_cancel"));

       parent.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               pop.dismiss();
               ll_popup.clearAnimation();
           }
       });

       bt1.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
               photo();
               pop.dismiss();
               ll_popup.clearAnimation();
           }
       });

       go2Album.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
               AlbumManager.openAlbum(UsePerpose.SEL_PIC, 4);
               overridePendingTransition(R.anim.activity_translate_in,R.anim.activity_translate_out);
               pop.dismiss();
               ll_popup.clearAnimation();
           }
       });

       bt3.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
               pop.dismiss();
               ll_popup.clearAnimation();
           }
       });

       noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
       noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
       adapter = new GridAdapter(this);
       adapter.update();
       noScrollgridview.setAdapter(adapter);
       noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
           public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
               if (position == 0) {
                   ll_popup.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.activity_translate_in));
                   pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);// 弹出选择图片源
               } else {
                   AlbumManager.openGallery(position-1);
               }
           }
       });

       btn_cut.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               AlbumManager.openAlbum(UsePerpose.CUT_PIC, 0);
           }
       });
   }

   @SuppressLint("HandlerLeak")
   public class GridAdapter extends BaseAdapter {
       private LayoutInflater inflater;
       private boolean shape;
       public boolean isShape() {
           return shape;
       }
       public void setShape(boolean shape) {
           this.shape = shape;
       }

       public GridAdapter(Context context) {
           inflater = LayoutInflater.from(context);
       }

       public void update() {
           loading();
       }

       public int getCount() {
           return (AlbumManager.selImgList.size() + 1);
       }

       public Object getItem(int arg0) {
           return null;
       }

       public long getItemId(int arg0) {
           return 0;
       }

       public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder holder = null;
           if (convertView == null) {
               convertView = inflater.inflate(R.layout.imgupload_item_published_grida, parent, false);
               holder = new ViewHolder();
               holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
               convertView.setTag(holder);
           } else {
               holder = (ViewHolder) convertView.getTag();
           }

           if (position == 0) {
               holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
           } else {
               holder.image.setImageBitmap(AlbumManager.selImgList.get(position - 1).getBitmap());
               holder.imgPath = AlbumManager.selImgList.get(position - 1).imagePath;
               // 在这里设置图片的路径
               // tempSelectBitmap图片集合不包括"加"图片
           }
           return convertView;
       }

       public void loading() {
           if (AlbumManager.max == AlbumManager.selImgList.size()) {
               Message message = new Message();
               message.what = 1;
               handler.sendMessage(message);
           } else if (AlbumManager.max < AlbumManager.selImgList.size()) {
               AlbumManager.max += 1;
               Message message = new Message();
               message.what = 1;
               handler.sendMessage(message);
           } else {
               AlbumManager.max -= 1;
               Message message = new Message();
               message.what = 1;
               handler.sendMessage(message);
           }
       }
   }

   private static final int TAKE_PICTURE = 0x000001;

   // 拍照-----------------------------
   public void photo() {
       Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       startActivityForResult(openCameraIntent, TAKE_PICTURE);
   }

   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       switch (requestCode) {
       case TAKE_PICTURE:
           if (AlbumManager.selImgList.size() < 4 && resultCode == RESULT_OK) {
               String sdPath = null;
               String fileName = String.valueOf(System.currentTimeMillis());
               Bitmap bm = (Bitmap) data.getExtras().get("data");
               sdPath = Environment.getExternalStorageDirectory() + "/"+ AlbumManager.takePhotoFolder+"/";
               FileUtils.saveBitmap(bm, fileName);
               ImageItem takePhoto = new ImageItem();
               takePhoto.setImagePath(sdPath+fileName);
               takePhoto.setBitmap(bm);
               AlbumManager.selImgList.add(takePhoto);
           }
           break;
       }
   }

   public boolean onKeyDown(int keyCode, KeyEvent event) {
       if (keyCode == KeyEvent.KEYCODE_BACK) {
           for (int i = 0; i < PublicWay.activityList.size(); i++) {
               if (null != PublicWay.activityList.get(i)) {
                   PublicWay.activityList.get(i).finish();
               }
           }
           System.exit(0);
       }
       return true;
   }

   public class ViewHolder {
       public ImageView image;
       public String imgPath;
   }

     /**
      *创建图片文件夹
      * Created by yellow on 11:08  2016/10/17.
      */
    private void makeDir(){
        String dir = Environment.getExternalStorageDirectory().getPath() + File.separator+"luhe" ;
        File dirFile = new File(dir);
        if(!dirFile.exists()){
            boolean b  = dirFile.mkdir();
        }
    }
}
