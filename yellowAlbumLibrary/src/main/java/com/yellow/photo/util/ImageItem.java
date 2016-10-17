package com.yellow.photo.util;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;


public class ImageItem  implements Serializable ,Comparable<ImageItem>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public String time;
	private WeakReference<Bitmap> weakReferenceBitmap;
	public boolean isSelected = false;
	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Bitmap getBitmap() {	
		if(thumbnailPath!=null&&!"".equals(thumbnailPath)){
			return getBitmap(this.thumbnailPath);
		}
		return getBitmap(this.imagePath);
		
	}
	//传入路径返回缩小的图片
	public Bitmap getBitmap(String path) {
		Bitmap bitmap=null;
		if(this.weakReferenceBitmap!=null){
			bitmap=this.weakReferenceBitmap.get();
		}
		if(bitmap == null){
			try {
				setBitmap(AlbumUtils.revitionImageSize(path));
				return this.weakReferenceBitmap.get();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	
	
	public void setBitmap(Bitmap bitmap) {
		this.weakReferenceBitmap=new WeakReference<Bitmap>(bitmap);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ImageItem) {
			ImageItem other = (ImageItem) obj;
            return (other.imagePath.equals(this.imagePath));
        }
        return super.equals(obj);
	}
	@Override
	public int hashCode() {
		return imagePath.hashCode();
	}
	@Override
	public int compareTo(ImageItem another) {
		// TODO Auto-generated method stub
		File currentFile=new File(this.imagePath);
		File anotherFile=new File(another.imagePath);
		if(currentFile.lastModified()<anotherFile.lastModified()){  
	        return 1;
	    }else{  
	        return -1;  
	    } 
	}
	
	
	
}
