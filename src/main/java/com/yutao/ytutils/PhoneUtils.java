package com.yutao.ytutils;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yutao.ytutils.ui.toast.ToastUtils;

import java.io.File;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by 004 on 2018/2/22.
 */

public class PhoneUtils {
    /**
     * 将dp转换成px
     *
     * @param mContext
     * @param size
     * @return
     */
    public static float dpTopx(Context mContext, float size) {
        if (mContext == null)
            return 0;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, mContext.getResources().getDisplayMetrics());
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断是否安装了应用
     *
     * @param mContext
     * @param packageName
     * @return
     * @throws Exception
     */
    public static boolean isApplicationInstalled(Context mContext, String packageName) throws Exception {
        if (mContext == null
                || StringUtils.isBlank(packageName)) {
            throw new Exception("判断是否安装了应用---数据不全");
        }
        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String pn = packageInfos.get(i).packageName;
                if (packageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param dir
     */
    private static void deleteFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * 描述：判断网络是否可用.
     */
    public static boolean isNetworkAvailable(Context mContext) throws Exception {
        if (mContext == null)
            throw new Exception("判断网络是否可用---上下文为空");
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    public static int getVersionCode(Application application){
        if (application == null)
            return 0;
        int versioncode = 0;
        PackageManager pm = application.getPackageManager();
        String packagename = application.getPackageName();
        try {
            PackageInfo info = pm.getPackageInfo(packagename, 0);
            versioncode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versioncode;
    }

    /**
     * 获取当前版本名称
     *
     * @return
     */
    public static String getVersionName(Context application) throws Exception {
        if (application == null)
            throw new Exception("获取当前版本名称---application不可为空");

        String versionName = null;
        PackageManager pm = application.getPackageManager();
        String packagename = application.getPackageName();
        try {
            PackageInfo info = pm.getPackageInfo(packagename, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 进入手机自带的应用市场
     *
     * @param context
     * @return
     */
    public static boolean goToMarket(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 关闭软键盘
     *
     * @param mContext
     */
    public static void closeSoftInput(Context mContext, View currentFocus) {
        if (mContext == null
                || currentFocus == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 启动软键盘
     *
     * @param mContext
     * @param currentFocus
     */
    public static void openSoftInput(Context mContext, View currentFocus) {
        if (mContext == null
                || currentFocus == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(currentFocus, 0);
    }

    /**
     * 获取屏幕高度
     *
     * @param mContext
     * @return
     */
    public static int getScreenHeight(Context mContext) {
        if (mContext == null)
            return 0;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @param mContext
     * @return
     */
    public static int getScreenWidth(Context mContext) {
        if (mContext == null)
            return 0;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    /**
     * 获取手机唯一标示
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String id;
        //android.telephony.TelephonyManager
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (mTelephony.getDeviceId() != null) {
            id = mTelephony.getDeviceId();
        } else {
            //android.provider.Settings;
            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return id;
    }

    /**
     * 跳转到打电话界面
     *
     * @param phoneNumber
     */
    public static void toCall(Activity mContext, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ToastUtils.init(mContext.getApplicationContext());
            ToastUtils.getInstance().showMessageToast("App没有被您赋予拨打电话的权限");
            ActivityCompat.requestPermissions(mContext,new String[]{ Manifest.permission.CALL_PHONE},0);
            return;
        }
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(callIntent);
    }


    public static String binaryToDecimal(int n) {
        String result = Integer.toBinaryString(n);

        return result;
    }

    /**
     * 开始震动
     */
    public static boolean startVibrate(Context mContext) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_DENIED) {
            //权限没有开启
            return false;
        }
        Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(500);
        return true;
    }

    /**
     * 获得状态栏高度
     *
     * @param mContext
     * @return
     */
    public static int getStatusHeight(Context mContext) {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        if (statusBarHeight1 == 0)
            statusBarHeight1 = (int) dpTopx(mContext, 25);
        return statusBarHeight1;
    }



    /**
     * 震动一下
     */
    public static void vibrate(Context mContext) {
        if (mContext == null)
            return;
        Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    /**
     * 必备的权限
     */
    private static String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.ACCESS_WIFI_STATE
            ,Manifest.permission.READ_PHONE_STATE
            ,Manifest.permission.VIBRATE
            ,Manifest.permission.CAMERA
            ,Manifest.permission.ACCESS_COARSE_LOCATION
    };

    /**
     * 请求必要权限
     */
    public static void requestPermissions(Activity activity){
        if (activity == null)
            return;
        boolean isNeedRequest = false;
        for (String permission:permissions){
           if (ContextCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED){
               isNeedRequest = true;
           }
        }
        //没有赋予此项权限
        if (activity!=null&&isNeedRequest){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(permissions,0);
            }else{
                ActivityCompat.requestPermissions(activity,permissions,0);
            }
        }
    }
}
