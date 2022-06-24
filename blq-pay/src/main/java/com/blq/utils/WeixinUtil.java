package com.blq.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * description: WeixinUtil <br>
 * date: 2022/6/9 9:50 <br>
 * author: 18721 <br>
 * version: 1.0 <br>
 */
@Component
public class WeixinUtil {

    // 统一下单接口换取与支付订单号prepay_id
    public static final String unifiedorder = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    // 订单查询接口。查询是否成功，返回给微信服务器
    public static final String orderquery = "https://api.mch.weixin.qq.com/pay/orderquery";
    public static int isCreateMenu = 1;
    // 获取access_token的接口地址（GET） 限200（次/天）
    public static String ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    // 菜单创建（POST） 限100（次/天）
    public static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    // 客服接口地址
    public static String send_message_url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
    // 用户信息
    public static String getUserInfo_url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    private static Logger log = LoggerFactory.getLogger(WeixinUtil.class);
}
