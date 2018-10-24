package com.yutao.ytutils.ui.toast;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yutao.ytutils.R;


public class ToastUtils {
    private static ToastUtils instance;
    private Toast toast;

    private View contentView;
    private ImageView toastIconIV;
    private TextView toastMessageTV;
    private ProgressBar toastProgressBar;

    private Handler mHandler;
    private RepeatRunnable repeatToast=new RepeatRunnable();
    private static Context application;
    public static void init(Context application){
        ToastUtils.application = application;
    }

    public static synchronized ToastUtils getInstance(){
        synchronized (ToastUtils.class){
            if (instance == null){
                instance = new ToastUtils();
            }
            return instance;
        }
    }

    private ToastUtils() {
        mHandler = new Handler();
    }

    public Toast getToast() {
        if (toast!=null)
            toast.cancel();
        toast = null;
        if (toast==null) {
            toast = Toast.makeText(application, "", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.setView(contentView==null?getContentView():contentView);
        }
        return toast;
    }

    public View getContentView() {
        if (contentView==null) {
            contentView = LayoutInflater.from(application).inflate(R.layout.toast_view_content, null);
            contentView.setMinimumWidth(application.getResources().getDisplayMetrics().widthPixels/2);

            toastIconIV = contentView.findViewById(R.id.toastIconIV);
            toastMessageTV = contentView.findViewById(R.id.toastMessageTV);
            toastProgressBar = contentView.findViewById(R.id.toastProgressBar);
        }

        toastIconIV.setVisibility(View.VISIBLE);
        toastMessageTV.setVisibility(View.VISIBLE);
        toastProgressBar.setVisibility(View.GONE);
        return contentView;
    }

    public void showToast(String message, Type type){
        switch (type){
            case WARN:
                showWarnToast(message);
                break;
            case ERROR:
                showErrorToast(message);
                break;
            case MESSAGE:
                showMessageToast(message);
                break;
            case PROGRESS:
                showProgressToast(message);
                break;
            case SURE:
                showSureToast(message);
                break;
        }
    }

    public void show(){
        getToast().show();
    }

    public void cancel(){
        mHandler.removeCallbacks(repeatToast);
        getToast().cancel();
        toast = null;
    }

    public void showErrorToast(String message){
        if (application == null)
            return;

        getContentView();

        toastMessageTV.setText(message);
        toastIconIV.setImageResource(R.mipmap.icon_close);

        show();
    }

    public void showMessageToast(String message){
        if (application == null)
            return;

        getContentView();

        toastMessageTV.setText(message);
        toastIconIV.setVisibility(View.GONE);

       show();
    }

    public void showWarnToast(String message){
        if (application == null)
            return;

        getContentView();

        toastMessageTV.setText(message);
        toastIconIV.setImageResource(R.mipmap.icon_close);

        show();
    }

    public void showProgressToast(String message){
        if (application == null)
            return;

        getContentView();
        Toast toast = getToast();

        toastMessageTV.setText(message);
        toastProgressBar.setVisibility(View.VISIBLE);
        toastIconIV.setVisibility(View.GONE);

        toast.show();

        mHandler.post(repeatToast.setDurationMillions(200));
    }

    public void showSureToast(String message){
        if (application == null)
            return;

        getContentView();

        toastMessageTV.setText(message);
        toastIconIV.setImageResource(R.mipmap.icon_selected);

        show();
    }

    private class RepeatRunnable implements Runnable {
        private long durationMillions=3000;
        @Override
        public void run() {
            if (application == null)
                return;

            getToast().show();
            if (mHandler!=null){
                mHandler.postDelayed(this,durationMillions);
            }
        }

        public Runnable setDurationMillions(long durationMillions) {
            this.durationMillions = durationMillions;
            return this;
        }
    }
}
