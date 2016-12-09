package com.yellow.browseNetImage;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;
import com.yellow.photo.activity.R;

public class BroswerNetViewActivity extends Activity {

	/** Item数据实体集合 */
	private ArrayList<ItemEntity> itemEntities;
	/** ListView对象 */
	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_net_main);
		listview = (ListView) findViewById(R.id.ll_content);
		initData();
		listview.setAdapter(new ListItemAdapter(this, itemEntities));
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		itemEntities = new ArrayList<ItemEntity>();
		// 1.无图片
		ItemEntity entity1 = new ItemEntity(//
				"http://img.my.csdn.net/uploads/201410/19/1413698871_3655.jpg", "张三", "今天天气不错...", null);
		itemEntities.add(entity1);
		// 2.1张图片
		ArrayList<String> urls_1 = new ArrayList<String>();
		urls_1.add("http://img.my.csdn.net/uploads/201410/19/1413698883_5877.jpg");
		ItemEntity entity2 = new ItemEntity(//
				"http://img.my.csdn.net/uploads/201410/19/1413698865_3560.jpg", "李四", "今天雾霾呢...", urls_1);
		itemEntities.add(entity2);
		// 3.3张图片
		ArrayList<String> urls_2 = new ArrayList<String>();
		urls_2.add("http://img.my.csdn.net/uploads/201410/19/1413698867_8323.jpg");
		urls_2.add("http://img.my.csdn.net/uploads/201410/19/1413698883_5877.jpg");
		urls_2.add("http://img.my.csdn.net/uploads/201410/19/1413698837_5654.jpg");
		ItemEntity entity3 = new ItemEntity(//
				"http://img.my.csdn.net/uploads/201410/19/1413698837_5654.jpg", "王五", "今天好大的太阳...", urls_2);
		itemEntities.add(entity3);
		// 4.6张图片
		ArrayList<String> urls_3 = new ArrayList<String>();
		urls_3.add("http://img.my.csdn.net/uploads/201410/19/1413698837_7507.jpg");
		urls_3.add("http://img.my.csdn.net/uploads/201410/19/1413698865_3560.jpg");
		urls_3.add("http://img.my.csdn.net/uploads/201410/19/1413698867_8323.jpg");
		urls_3.add("http://img.my.csdn.net/uploads/201410/19/1413698837_5654.jpg");
		urls_3.add("http://img.my.csdn.net/uploads/201410/19/1413698883_5877.jpg");
		urls_3.add("http://img.my.csdn.net/uploads/201410/19/1413698839_2302.jpg");
		ItemEntity entity4 = new ItemEntity(//
				"http://img.my.csdn.net/uploads/201410/19/1413698883_5877.jpg", "赵六", "今天下雨了...", urls_3);
		itemEntities.add(entity4);
	}
}
