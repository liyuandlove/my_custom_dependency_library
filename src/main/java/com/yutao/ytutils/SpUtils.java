package com.yutao.ytutils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * a：余涛
 * b：1054868047
 * c：2018/9/27 13:58
 * d：
 */
public class SpUtils {
    private static Application application;
    private static final String defalutSpFileName = "ytSp";

    public static void init(Application application){
        SpUtils.application = application;
    }

    /**
     * 保存数据
     * @param spFileName
     * @param key
     * @param data
     * @param isAppend
     */
    public static void saveData(String spFileName,String key,String data,boolean isAppend){
        if (application == null)
            return;
        int mode = Context.MODE_APPEND;
        if (!isAppend)
            mode = Context.MODE_PRIVATE;
        SharedPreferences sp = application.getSharedPreferences(spFileName,mode);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,data);
        editor.commit();
    }

    /**
     * 提取数据
     * @param spFileName
     * @param key
     * @return
     */
    public static String getData(String spFileName,String key){
        if (application == null)
            return null;
        SharedPreferences sp = application.getSharedPreferences(spFileName,Context.MODE_PRIVATE);
        if (sp == null)
            return null;
        String result = sp.getString(key,null);
        return result;
    }

    public static void saveDataNoAppend(String spFileName,String key,String data){
        saveData(spFileName,key,data,false);
    }

    public static void saveDataUseDefault(String key,String data){
        saveDataNoAppend(defalutSpFileName,key,data);
    }

    public static String getDataUseDefault(String key){
        return getData(defalutSpFileName,key);
    }
}
