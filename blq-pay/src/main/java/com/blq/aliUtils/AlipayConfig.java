package com.blq.aliUtils;

import org.springframework.stereotype.Component;

@Component
public class AlipayConfig {
    public String gatewayUrl = "https://openapi.alipay.com/gateway.do";
    private String app_id = "";// 应用ID
    private String merchant_private_key = "";// 商户私钥
    private String alipay_public_key = "";// 支付宝公钥
    private String notify_url_pay = "";// 服务器异步通知页面路径
    private String notify_url_order = "";// 服务器异步通知页面路径
    private String notify_url_recharge = "";
    private String return_url = "/port/pay/return";// 页面跳转同步通知页面路径
    private String sign_type = "RSA2";// 签名方式
    private String charset = "utf-8";// 字符编码格式
    private String format = "json";// 返回格式
    private String paymentSuccessUrl = "";// 支付成功跳转页面
    private String paymentFailureUrl = "";// 支付失败跳转页面

    public String getApp_id() {
        return app_id;
    }

    public String getMerchant_private_key() {
        return merchant_private_key;
    }

    public String getAlipay_public_key() {
        return alipay_public_key;
    }

    public String getNotify_url_pay() {
        return notify_url_pay;
    }

    public void setNotify_url_pay(String notify_url_pay) {
        this.notify_url_pay = notify_url_pay;
    }

    public String getNotify_url_order() {
        return notify_url_order;
    }

    public void setNotify_url_order(String notify_url_order) {
        this.notify_url_order = notify_url_order;
    }

    public String getSign_type() {
        return sign_type;
    }

    public String getCharset() {
        return charset;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public String getFormat() {
        return format;
    }

    public String getPaymentSuccessUrl() {
        return paymentSuccessUrl;
    }

    public String getPaymentFailureUrl() {
        return paymentFailureUrl;
    }

    public String getNotify_url_recharge() {
        return notify_url_recharge;
    }

    public void setNotify_url_recharge(String notify_url_recharge) {
        this.notify_url_recharge = notify_url_recharge;
    }
}
