# yelloAlbum
an android album project

一个浏览图片的相册 

使用说明:
1 在清单文件中先注册以下几个类
  AlbumActivity
  GalleryActivity
  ClipImgActivity

2 在调用AlbumActivity进入相册前先调用此方法:
  AlbumManager.initLoadImgConfig(xxActivity.this.getClass().getName(), xxActivity.this);
  两个参数分别表示跳转到AlbumActivity前的Activity的类名和对象

3  Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
   startActivity(intent);






gradle：  compile 'com.yellow.photo.activity:yellowAlbumLibrary:1.1.0'





![image](https://github.com/kid1943/yellowAlbum/blob/master/album.gif?raw=true)















