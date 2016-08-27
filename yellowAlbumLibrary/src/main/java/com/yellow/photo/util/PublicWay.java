package com.yellow.photo.util;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;


/**
 * 存放所有的list在最后退出时一起关闭
 */
public class PublicWay {
	public static List<Activity> activityList = new ArrayList<Activity>();
	public static int SELECTIMGNUM = 3;
	public static int SURPLUSSELECTIMGNUM = 0;//剩余未选图片
	
}
