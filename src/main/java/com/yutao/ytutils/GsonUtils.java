package com.yutao.ytutils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/10/13 0013.
 */

public class GsonUtils {
    private static Gson mGson;

    public static Gson getmGson() {
        if (mGson==null)
            mGson=new Gson();
        return mGson;
    }

    public static <T> T fromJson(String json, Class<T> classOfT){
        if (StringUtils.isBlank(json))
            return null;
        Object object;
        try {
           object = getmGson().fromJson(json,classOfT);
        }catch (JsonSyntaxException jsonSE){
            jsonSE.printStackTrace();
            return null;
        }
        if (object==null)
            return null;
        return Primitives.wrap(classOfT).cast(object);
    }

    /**
     * 把json字符串转换为JavaBean。如果json的根节点就是一个集合，则使用此方法<p>
     * type参数的获取方式为：Type type = new TypeToken<集合泛型>(){}.getType();
     * @param json json字符串
     * @return type 指定要解析成的数据类型
     */
    public static <T> T json2Collection(String json, Type type) {
        T bean = null;
        try {
            bean = mGson.fromJson(json, type);
        } catch (Exception e) {
            Log.i("JsonUtil", "解析json数据时出现异常\njson = " + json, e);
        }
        return bean;
    }
}
