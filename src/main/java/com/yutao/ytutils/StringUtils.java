package com.yutao.ytutils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * Created by 004 on 2018/2/22.
 */

public class StringUtils {

    /**
     * 判断是否是空字符串
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        boolean isBlank = false;
        if (str == null
                || str.length() == 0
                || str.trim().length() == 0)
            isBlank = true;
        return isBlank;
    }

    /**
     * 返回是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 星期几转String
     *
     * @param intWeek
     * @return
     */
    public static String weekIntToString(int intWeek) {
        switch (intWeek) {
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "日";
        }
        return null;
    }

    /**
     * 自动在前方加一个0
     *
     * @param num
     * @param count
     * @return
     */
    public static String autoAddZero(Object num, int count) {
        StringBuilder stringBuilder = new StringBuilder();
        if ((num + "").length() < count) {
            for (int i = 0; i < count - (num + "").length(); i++) {
                stringBuilder.append("0");
            }
        }
        stringBuilder.append(num + "");
        return stringBuilder.toString();
    }

    /**
     * 返回展示 sn
     *
     * @param sn
     * @return
     */
    public static String getShowSn(String sn) {
        String devSn = "[" + sn.substring(sn.length() - 4, sn.length()) + "]";
        return devSn;
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 获取带颜色的文字
     * @param text
     * @param color
     * @return
     */
    public static SpannableString getColorText(String text,int color){
        if (StringUtils.isBlank(text))
            return null;
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        spannableString.setSpan(span,0,text.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
