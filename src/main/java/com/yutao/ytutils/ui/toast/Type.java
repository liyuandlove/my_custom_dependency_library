package com.yutao.ytutils.ui.toast;

public enum Type {
    ERROR("错误",0)
    ,MESSAGE("消息",1)
    ,WARN("警告",2)
    ,PROGRESS("圆形进度",3)
    ,SURE("确认",4);

    private String value;
    private int code;

    Type(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }


    public int getCode() {
        return code;
    }
}
