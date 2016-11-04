# yelloAlbum
an android album project

一个浏览图片的相册 

使用说明:
1 在清单文件中先注册以下几个类
  AlbumActivity
  GalleryActivity
  ClipImgActivity

2 在进入相册前先调用此方法:
  AlbumManager.initLoadImgConfig(xxActivity.this.getClass().getName(), xxActivity.this);
  两个参数分别表示跳转到AlbumActivity前的Activity的类名和对象

3 调用AlbumManager.openAlbum(UsePerpose.CUT_PIC, 0)进入相册
   第一个参数为UsePerpose.CUT_PIC时表示进入相册剪切图片,为UsePerpose.SEL_PIC时表示进入相册浏览图片









gradle：  compile 'com.yellow.photo.activity:yellowAlbumLibrary:1.1.0'





![image](https://github.com/kid1943/yellowAlbum/blob/master/album.gif?raw=true)















