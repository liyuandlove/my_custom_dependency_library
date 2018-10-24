package com.yutao.ytutils.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yutao.ytutils.PhoneUtils;
import com.yutao.ytutils.R;


/**
 * a：余涛
 * b：1054868047
 * c：2018/9/10 11:35
 * d：选择视图
 */
public class SelectDialog extends Dialog implements View.OnClickListener {
    private View contentView;
    private OnCamrePhotoListener onCamrePhotoListener;
    private OnDialogSettingListener onDialogSettingListener;
    private OnSelectClickListener onSelectClickListener;
    private String title,message
            ,leftButtonText,rightButtonText;

    public SelectDialog(@NonNull Context context, int position) {
        super(context, R.style.customDialog);
        initViews(position);
    }

    private void initViews(int position){
        switch (position){
            case 0:
                initViewTypeOne();
                break;
            case 1:
                initViewTypeTwo();
                break;
            case 2:
                initViewTypeThree();
                break;
        }
    }

    public void setView(View contentView){
        this.contentView = contentView;
        setContentView(contentView);
    }

    /**
     * 初始化视图类型一
     */
    public void initViewTypeOne(){
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_select_dialog_type_one,null);

        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);

        Button selectDialogCaptrueButton = contentView.findViewById(R.id.selectDialogCaptrueButton);
        Button selectDialogAblumButton = contentView.findViewById(R.id.selectDialogAblumButton);

        selectDialogCaptrueButton.setOnClickListener(this);
        selectDialogAblumButton.setOnClickListener(this);

        setOnDialogSettingListener(new OnDialogSettingListener(contentView) {
            @Override
            public void onTitleSet(String title) {
                TextView selectDialogTitleTV = contentView.findViewById(R.id.selectDialogTitleTV);
                selectDialogTitleTV.setText(title);
            }
        });

        setView(contentView);
    }

    /**
     * 初始化第二种视图
     */
    public void initViewTypeTwo(){
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_select_dialog_type_two,null);
        getWindow().getDecorView().setPadding((int) PhoneUtils.dpTopx(getContext(),10), 0, (int) PhoneUtils.dpTopx(getContext(),10), 0);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels*0.8);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);

        setOnDialogSettingListener(new OnDialogSettingListener(contentView) {
            @Override
            void onTitleSet(String title) {
                super.onTitleSet(title);
                TextView selectDialogTitleTV = contentView.findViewById(R.id.selectDialogTitleTV);
                selectDialogTitleTV.setText(title);
            }

            @Override
            void onMessageSet(String message) {
                super.onMessageSet(message);
                TextView selectDialogMessageTV = contentView.findViewById(R.id.selectDialogMessageTV);
                selectDialogMessageTV.setText(message);
            }

            @Override
            void onLeftButtonSet(String text, View.OnClickListener onClickListener) {
                super.onLeftButtonSet(text, onClickListener);
                Button selectDialogLeftButton = contentView.findViewById(R.id.selectDialogLeftButton);
                selectDialogLeftButton.setText(text);
                selectDialogLeftButton.setOnClickListener(onClickListener);
            }

            @Override
            void onRightButtonSet(String text, View.OnClickListener onClickListener) {
                super.onRightButtonSet(text, onClickListener);
                Button selectDialogRightButton = contentView.findViewById(R.id.selectDialogRightButton);
                selectDialogRightButton.setText(text);
                selectDialogRightButton.setOnClickListener(onClickListener);
            }
        });

        setView(contentView);
    }

    /**
     * 设置第三种视图
     */
    public void initViewTypeThree(){
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_scan_dialog,null);
        getWindow().getDecorView().setPadding((int) PhoneUtils.dpTopx(getContext(),50), 0, (int) PhoneUtils.dpTopx(getContext(),50), 0);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
        setView(contentView);

        setOnDialogSettingListener(new OnDialogSettingListener(contentView) {

            @Override
            void onMessageSet(String message) {
                super.onMessageSet(message);
                TextView selectDialogMessageTV = contentView.findViewById(R.id.hintTextView);
                selectDialogMessageTV.setText(message);
            }

            @Override
            void onLeftButtonSet(String text, View.OnClickListener onClickListener) {
                super.onLeftButtonSet(text, onClickListener);
                Button selectDialogLeftButton = contentView.findViewById(R.id.hintBottomBotton);
                selectDialogLeftButton.setText(text);
                selectDialogLeftButton.setOnClickListener(onClickListener);
            }
        });
    }

    public SelectDialog setOnCamrePhotoListener(OnCamrePhotoListener onCamrePhotoListener) {
        this.onCamrePhotoListener = onCamrePhotoListener;
        return this;
    }

    public SelectDialog setOnDialogSettingListener(OnDialogSettingListener onDialogSettingListener) {
        this.onDialogSettingListener = onDialogSettingListener;
        return this;
    }

    public SelectDialog setOnSelectClickListener(OnSelectClickListener onSelectClickListener) {
        this.onSelectClickListener = onSelectClickListener;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public SelectDialog setTitle(String title) {
        this.title = title;
        if (onDialogSettingListener!=null
                &&onDialogSettingListener.contentView!=null)
            onDialogSettingListener.onTitleSet(title);
        return this;
    }

    public SelectDialog setMessage(String message){
        this.message = message;
        if (onDialogSettingListener!=null
                &&onDialogSettingListener.contentView!=null)
            onDialogSettingListener.onMessageSet(message);
        return this;
    }

    public SelectDialog setLeftButtonText(String leftButtonText){
        this.leftButtonText =leftButtonText;
        if (onDialogSettingListener!=null
                &&onDialogSettingListener.contentView!=null)
            onDialogSettingListener.onLeftButtonSet(leftButtonText,this);
        return this;
    }

    public SelectDialog setRightButtonText(String rightButtonText){
        this.rightButtonText =rightButtonText;
        if (onDialogSettingListener!=null
                &&onDialogSettingListener.contentView!=null)
            onDialogSettingListener.onRightButtonSet(rightButtonText,this);
        return this;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        int i = v.getId();
        if (i == R.id.selectDialogCaptrueButton) {
            if (onCamrePhotoListener != null)
                onCamrePhotoListener.onGoToCapture(v);

        } else if (i == R.id.selectDialogAblumButton) {
            if (onCamrePhotoListener != null)
                onCamrePhotoListener.onGoToAblum(v);

        } else if (i == R.id.selectDialogLeftButton) {
            if (onSelectClickListener != null)
                onSelectClickListener.onLeftClickListener(this, v);

        } else if (i == R.id.selectDialogRightButton) {
            if (onSelectClickListener != null)
                onSelectClickListener.onRightClickListener(this, v);

        } else if (i == R.id.hintBottomBotton) {
            if (onSelectClickListener != null)
                onSelectClickListener.onLeftClickListener(this, v);

        }
    }

    public abstract class OnDialogSettingListener{
        protected View contentView;

        public OnDialogSettingListener(View contentView) {
            this.contentView = contentView;
        }

        void onTitleSet(String title){

        }

        void onMessageSet(String message){

        }

        void onLeftButtonSet(String text, View.OnClickListener onClickListener){

        }
        void onRightButtonSet(String text, View.OnClickListener onClickListener){

        }
    }

    public interface OnSelectClickListener{
        void onLeftClickListener(SelectDialog selectDialog, View view);
        void onRightClickListener(SelectDialog selectDialog, View view);
    }

    public interface OnCamrePhotoListener{
        void onGoToCapture(View view);
        void onGoToAblum(View view);
    }
}
