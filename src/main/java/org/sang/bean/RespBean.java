package org.sang.bean;

import java.io.Serializable;

/**
 * Created by sang on 2017/12/17.
 */
public class RespBean implements Serializable {
    private String status;
    private String msg;
    private Object obj;

    public RespBean() {
    }

    public RespBean(String status, String msg) {

        this.status = status;
        this.msg = msg;
    }

    public RespBean(String status, String msg,Object obj) {

        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }

    public static RespBean build() {
        return new RespBean();
    }

    public static RespBean ok(String msg, Object obj) {
        return new RespBean("200",msg,obj);
    }

    public static RespBean ok(String msg) {
        return new RespBean("200", msg, null);
    }

    public static RespBean error(String msg, Object obj) {
        return new RespBean("500", msg, obj);
    }

    public static RespBean error(String msg) {
        return new RespBean("500", msg, null);
    }

    public static RespBean loginError(String msg){
        return new RespBean("100", msg, null);
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public RespBean setObj(Object obj) {
        this.obj = obj;
        return this;
    }
}
