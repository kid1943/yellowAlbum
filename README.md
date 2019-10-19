# yelloAlbum
an android album project

一个浏览图片的相册 

使用说明:

1 在清单文件中先注册以下几个类
  AlbumActivity
  GalleryActivity
  ClipImgActivity

2 在进入相册前先调用此方法:
  AlbumManager.initLoadImgConfig(context);


3 调用AlbumManager.openAlbum(UsePerpose.CUT_PIC, 0)进入相册
   第一个参数为UsePerpose.CUT_PIC时表示进入相册剪切图片,为UsePerpose.SEL_PIC时表示进入相册浏览选择图片
   第二个参数表示当第一个参数是UsePerpose.SEL_PIC时，进入相册所需要选择的图片数量

4 在选择完图片后(AlbumManager.selImgList集合的size大于0) 调用 AlbumManager.openGallery(position)即可进入图片浏览界面

5 其他可选配置:
  1) AlbumUtils.setHeadViewTitleCololr(R.color.red); 设置相册toolbar的颜色
  2)...to be continue

6 选中后的图片对象ImageItem集合存放在AlbumManager.selImgList中，通过集合中的ImageItem的getPath方法可以获取图片路径。
  剪切后的图片路径是:AlbumManager.cutImgPath。当然获取图片路径后进行展示、上传或者其他操作要取决于你。



    依赖添加：
           compile 'com.yellow.photo.activity:yellowAlbumLibrary:1.2.0'
           compile 'com.android.support:appcompat-v7:23.4.0'






![image](https://github.com/kid1943/yellowAlbum/blob/master/album.gif?raw=true)





`剪切图片`:

![image](https://github.com/kid1943/yellowAlbum/blob/master/album_cut.gif?raw=true)



`选择图片`:

![image](https://github.com/kid1943/yellowAlbum/blob/master/album_sel.gif?raw=true)


## License

The source code  is licensed under the [MIT License](https://opensource.org/licenses/MIT)
















