package com.blq.common.utils;

import lombok.Data;

/**
 * description: 统一返回结果集 <br>
 * date: 2022/5/6 9:52 <br>
 * author: Blq <br>
 * version: 1.0 <br>
 */
@Data
public class ResultMsg<T>{

    //错误码
    private Integer resultCode;

    //错误信息，一般为前端提示信息
    private String resultMsg;

    //返回值，一般为成功后返回的数据
    private T data;

    //错误详情，一般为失败后的详细原因，如空指针之类的
    private String resultDetail;

    public ResultMsg() {}

    public ResultMsg(Integer resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public ResultMsg(Integer resultCode, String resultMsg, T data) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.data = data;
    }

    /**
     * 配合静态对象直接设置 data 参数
     * @param data
     * @return
     */
    public ResultMsg setNewData(T data) {
        ResultMsg error = new ResultMsg();
        error.setResultCode(this.resultCode);
        error.setResultMsg(this.resultMsg);
        error.setResultDetail(this.resultDetail);
        error.setData(data);
        return error;
    }

    /**
     * 配合静态对象直接设置 errorMsg 参数
     * @param errorMsg
     * @return
     */
    public ResultMsg setNewErrorMsg(String errorMsg) {
        ResultMsg error = new ResultMsg();
        error.setResultCode(this.resultCode);
        error.setResultMsg(errorMsg);
        error.setResultDetail(this.resultDetail);
        error.setData(this.data);
        return error;
    }

    public static final ResultMsg SUCCESS = new ResultMsg(200, "成功");

    public static final ResultMsg INSERT_SUCCESS = new ResultMsg(200, "新增成功");

    public static final ResultMsg UPDATE_SUCCESS = new ResultMsg(200, "更新成功");

    public static final ResultMsg DELETE_SUCCESS = new ResultMsg(200, "删除成功");

    public static final ResultMsg UPLOAD_SUCCESS = new ResultMsg(200, "上传成功");

    public static final ResultMsg DOWNLOAD_SUCCESS = new ResultMsg(200, "下载成功");

    public static final ResultMsg LOGIN_SUCCESS = new ResultMsg(200, "登陆成功");

    public static final ResultMsg LOGOUT_SUCCESS = new ResultMsg(200, "登出成功");

    public static final ResultMsg LOGIN_ERROR = new ResultMsg(201, "登陆错误");

    public static final ResultMsg LOGIN_EXPIRE = new ResultMsg(202, "登陆过期");

    public static final ResultMsg ACCESS_LIMITED = new ResultMsg(301, "访问受限");

    public static final ResultMsg ARGS_ERROR = new ResultMsg(501, "参数错误");

    public static final ResultMsg SELECT_SUCCESS = new ResultMsg(200, "查询成功");

    public static final ResultMsg SELECT_ERROR = new ResultMsg(510, "查询失败");


    public static final ResultMsg OBJECT_SUCCESS = new ResultMsg(200, "调用成功");
    public static final ResultMsg OBJECT_ERROR = new ResultMsg(511, "调用失败");

    public static final ResultMsg UNKOWN_ERROR = new ResultMsg(502, "系统异常");

    public static final ResultMsg INSERT_ERROR = new ResultMsg(503, "新增错误");

    public static final ResultMsg UPDATE_ERROR = new ResultMsg(504, "更新错误");

    public static final ResultMsg DELETE_ERROR = new ResultMsg(506, "删除错误");


    public static final ResultMsg UPLOAD_ERROR = new ResultMsg(507, "上传错误");

    public static final ResultMsg DOWNLOAD_ERROR = new ResultMsg(508, "下载错误");

    public static final ResultMsg OTHER_SYSTEM_ERROR = new ResultMsg(509, "调用系统异常");

    public static final ResultMsg REDIS_ERROR = new ResultMsg(510, "验证码异常");

    public static final ResultMsg CODE_ERROR = new ResultMsg(511, "验证码错误");

    public static final ResultMsg ENTIY_ERROR = new ResultMsg(512, "无此信息");
}
