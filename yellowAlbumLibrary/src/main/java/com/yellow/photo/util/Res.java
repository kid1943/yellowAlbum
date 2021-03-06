package com.yellow.photo.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;


public class Res {

	// 文件路径名
	private static String pkgName;
	// R文件的对象
	private static Resources resources;

	// 初始化文件夹路径和R资源
	public static void init(Context context) {
		pkgName = context.getPackageName();
		resources = context.getResources();
		context = null;
	}

	public static int getLayoutID(String layoutName) {
		try {
			return resources.getIdentifier(layoutName, "layout", pkgName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 获取到控件的ID
	public static int getWidgetID(String widgetName) {
		return resources.getIdentifier(widgetName, "id", pkgName);
	}

	public static int getAnimID(String animName) {
		return resources.getIdentifier(animName, "anim", pkgName);
	}

	public static int getXmlID(String xmlName) {
		return resources.getIdentifier(xmlName, "xml", pkgName);
	}

	// 获取xml文件
	public static XmlResourceParser getXml(String xmlName) {
		int xmlId = getXmlID(xmlName);
		return (XmlResourceParser) resources.getXml(xmlId);
	}


	public static int getRawID(String rawName) {
		return resources.getIdentifier(rawName, "raw", pkgName);
	}

	public static int getDrawableID(String drawName) {
		return resources.getIdentifier(drawName, "drawable", pkgName);
	}

	// 获取到Drawable文件
	public static Drawable getDrawable(String drawName) {
		int drawId = getDrawableID(drawName);
		return resources.getDrawable(drawId);
	}

	public static int getAttrID(String attrName) {
		return resources.getIdentifier(attrName, "attr", pkgName);
	}

	// 获取到dimen.xml文件里的元素的id
	public static int getDimenID(String dimenName) {
		return resources.getIdentifier(dimenName, "dimen", pkgName);
	}

	// 获取到color.xml文件里的元素的id
	public static int getColorID(String colorName) {
		return resources.getIdentifier(colorName, "color", pkgName);
	}

	// 获取到color.xml文件里的元素的id
	public static int getColor(String colorName) {
		return resources.getColor(getColorID(colorName));
	}

	// 获取到style.xml文件里的元素id
	public static int getStyleID(String styleName) {
		return resources.getIdentifier(styleName, "style", pkgName);
	}

	// 获取到String.xml文件里的元素id
	public static int getStringID(String strName) {
		return resources.getIdentifier(strName, "string", pkgName);
	}

	// 获取到String.xml文件里的元素
	public static String getString(String strName) {
		int strId = getStringID(strName);
		return resources.getString(strId);
	}

	// 获取color.xml文件里的integer-array元素
	public static int[] getInteger(String strName) {
		return resources.getIntArray(resources.getIdentifier(strName, "array", pkgName));
	}

}
