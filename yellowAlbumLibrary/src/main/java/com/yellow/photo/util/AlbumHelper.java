package com.yellow.photo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

public class AlbumHelper {
	final String TAG = getClass().getSimpleName();
	Context context;
	ContentResolver cr;

	HashMap<String, String> thumbnailList = new HashMap<String, String>();

	List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
	HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

	private static  AlbumHelper instance;
	boolean hasBuildImagesBucketList = false;
	private Cursor cur;
	private String columns[];
	private List<ImageBucket> tmpList;

	public AlbumHelper() {
	}

	public static  AlbumHelper getHelper() {
		if (instance == null) {
			instance = new AlbumHelper();
		}
		return instance;
	}

	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			cr = context.getContentResolver();
		}
	}

	/**
	 * 查询略缩图片信息
	 *@author xp
	 *@time 2016-3-21 下午3:54:01 
	 */
	private void getThumbnail() {
		String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID,Thumbnails.DATA };
		Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,null, null, Thumbnails._ID + " DESC");
		getThumbnailColumnData(cursor);
	}

	private void getThumbnailColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			int _id;
			int image_id;
			String image_path;
			int _idColumn = cur.getColumnIndex(Thumbnails._ID);
			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

			do {
				// Get the field values
				_id = cur.getInt(_idColumn);
				image_id = cur.getInt(image_idColumn);
				image_path = cur.getString(dataColumn);

				// Do something with the values.
				// HashMap<String, String> hash = new HashMap<String, String>();
				// hash.put("image_id", image_id + "");
				// hash.put("path", image_path);
				// thumbnailList.add(hash);
				thumbnailList.put("" + image_id, image_path);
			} while (cur.moveToNext());
		}
	}

	void getAlbum() {
		String[] projection = { Albums._ID, Albums.ALBUM, Albums.ALBUM_ART,Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS };
		Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, null,null, Albums.DEFAULT_SORT_ORDER + " DESC");
		getAlbumColumnData(cursor);
	}

	private void getAlbumColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			int _id;
			String album;
			String albumArt;
			String albumKey;
			String artist;
			int numOfSongs;

			int _idColumn = cur.getColumnIndex(Albums._ID);
			int albumColumn = cur.getColumnIndex(Albums.ALBUM);
			int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
			int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
			int artistColumn = cur.getColumnIndex(Albums.ARTIST);
			int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);

			do {
				// Get the field values
				_id = cur.getInt(_idColumn);
				album = cur.getString(albumColumn);
				albumArt = cur.getString(albumArtColumn);
				albumKey = cur.getString(albumKeyColumn);
				artist = cur.getString(artistColumn);
				numOfSongs = cur.getInt(numOfSongsColumn);
				HashMap<String, String> hash = new HashMap<String, String>();
				hash.put("_id", _id + "");
				hash.put("album", album);
				hash.put("albumArt", albumArt);
				hash.put("albumKey", albumKey);
				hash.put("artist", artist);
				hash.put("numOfSongs", numOfSongs + "");
				albumList.add(hash);

			} while (cur.moveToNext());

		}
	}

	// 取得图片文件夹的集合并把旗下的图片信息(不包含bitmap对象)的集合ArrayList<ImageItem>
	//放到对应的文件夹集合的单个对象ImageBucket中
	void buildImagesBucketList() {
		long startTime = System.currentTimeMillis();
//		getThumbnail();
		if(columns == null){
		   columns = new String[] { Media._ID, Media.BUCKET_ID
				                             , Media.PICASA_ID
				                             , Media.DATA
				                             , Media.DISPLAY_NAME, Media.TITLE
				                             , Media.SIZE, Media.BUCKET_DISPLAY_NAME };
		}
        
		cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null, Media.DATE_MODIFIED + " DESC");
            
		if (cur.moveToFirst()) {
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
//			int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
//			// 不带扩展名的文件名
//			int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
//			int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
			// 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
			int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
//			int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
//			int totalNum = cur.getCount();
			do {
				String _id = cur.getString(photoIDIndex);
//				String name = cur.getString(photoNameIndex);
				String path = cur.getString(photoPathIndex);
//				String title = cur.getString(photoTitleIndex);
//				String size = cur.getString(photoSizeIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);
//				String picasaId = cur.getString(picasaIdIndex);
			
				ImageBucket bucket = bucketList.get(bucketId);
				if (bucket == null) {
					bucket = new ImageBucket();
					bucketList.put(bucketId, bucket);
					bucket.imageList = new ArrayList<ImageItem>();
					bucket.imageMap = new HashMap<Integer, String>();
					bucket.bucketName = bucketName;
				}
				
				if(bucket.imageList!=null){
					if(!bucket.imageMap.containsKey(Integer.valueOf(_id))){
						ImageItem imageItem = new ImageItem();
						imageItem.imageId = _id;
						imageItem.imagePath = path;
						imageItem.thumbnailPath = thumbnailList.get(_id);
						bucket.imageList.add(imageItem);
						bucket.imageMap.put(Integer.valueOf(_id), path);
						bucket.count = bucket.imageList.size();
					}
				}	
			} while (cur.moveToNext());
		}
        cur.close();
		
/*		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr.next();
			ImageBucket bucket = entry.getValue();
			for (int i = 0; i < bucket.imageList.size(); ++i) {
				ImageItem image = bucket.imageList.get(i);
			}
		}*/
		hasBuildImagesBucketList = true;
		long endTime = System.currentTimeMillis();
		Log.d("AlbumActivity", "use time: " + (endTime - startTime) + " ms");
	}

	public List<ImageBucket> getImagesBucketList(boolean refresh) {
		if (refresh || (!refresh && !hasBuildImagesBucketList)) {
			Log.i("AlbumActivity", "AlbumHelper-refresh-"+refresh);
			buildImagesBucketList();
		}
		if(tmpList!=null){
			tmpList.clear();
			tmpList = null;
		}
		tmpList = new ArrayList<ImageBucket>();
		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}

	String getOriginalImagePath(String image_id) {
		String path = null;
		String[] projection = { Media._ID, Media.DATA };
		Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,
				Media._ID + "=" + image_id, null, Media.DEFAULT_SORT_ORDER + " DESC");
		if (cursor != null) {
			cursor.moveToFirst();
			path = cursor.getString(cursor.getColumnIndex(Media.DATA));
		}
		return path;
	}

}
