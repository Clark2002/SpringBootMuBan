package com.blq.utils;


import com.blq.wxPayUtil.XMLUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * description: WXPay <br>
 * date: 2022/6/24 16:19 <br>
 * author: Blq <br>
 * version: 1.0 <br>
 */
public class WXPay {
    /**
     * 获取微信支付的二维码地址
     *
     * @param total_fee    // 订单金额【备注：以分为单位】
     * @param body         // 产品名称
     * @param out_trade_no // 商户订单号
     * @param attach       // 附加參數
     * @return
     * @throws Exception
     */
    public static String getTwoCodeUrl(String total_fee, String body, String out_trade_no, String attach, int time, String url) throws Exception {
        /**
         * 账号信息
         */
        String appid = WXPayConfig.APPID;// 微信服务号的appid
        String mch_id = WXPayConfig.MCHID; // 微信支付商户号
        String key = WXPayConfig.APIKEY; // 微信支付的API密钥
        String ufdoder_url = WXPayConfig.UFDODER_URL;// 微信下单API地址
        String trade_type = "NATIVE"; // 类型 扫码支付【网页扫码支付NATIVE】H5支付的交易类型为MWEB
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time_expire = sdf.format(time * 60 * 1000 + System.currentTimeMillis()); // 交易结束时间

        /**
         * 时间字符串
         */
        String currTime = PayForUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = PayForUtil.buildRandom(4) + "";
        String nonce_str = strTime + strRandom;

        /**
         * 参数封装
         */
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mch_id);
        packageParams.put("nonce_str", nonce_str);// 随机字符串
        packageParams.put("body", body);// 支付的产品名称
        packageParams.put("out_trade_no", out_trade_no);// 商户订单号【备注：每次发起请求都需要随机的字符串，否则失败。】
        packageParams.put("total_fee", total_fee);// 支付金额
        packageParams.put("spbill_create_ip", PayForUtil.localIp());// 客户端主机
        packageParams.put("notify_url", url);
        packageParams.put("trade_type", trade_type);
        packageParams.put("attach", attach);// 额外的参数【业务类型 + 会员ID + 支付类型】
        packageParams.put("time_expire", time_expire);

        String sign = PayForUtil.createSign("UTF-8", packageParams, key);// 获取签名
        packageParams.put("sign", sign);

        String requestXML = PayForUtil.getRequestXml(packageParams);// 将请求参数转换成String类型
        String resXml = PayForUtil.postData(ufdoder_url, requestXML);// 解析请求之后的xml参数并且转换成String类型
        Map map = XMLUtil.doXMLParse(resXml);
        String urlCode = (String) map.get("code_url");
        System.err.println(map.toString());
        return urlCode;
    }

    /**
     * app支付
     *
     * @param total_fee    // 订单金额【备注：以分为单位】
     * @param body         // 产品名称
     * @param out_trade_no // 商户订单号
     * @param attach       // 附加參數
     * @return
     * @throws Exception
     */
    public static Map<String, String> getAppPay(String total_fee, String body, String out_trade_no, String attach, int time, String url) throws Exception {
        /**
         * 账号信息
         */
        String appid = WXPayConfig.APPID;// 微信服务号的appid
        String mch_id = WXPayConfig.MCHID; // 微信支付商户号
        String key = WXPayConfig.APIKEY; // 微信支付的API密钥
        String ufdoder_url = WXPayConfig.UFDODER_URL;// 微信下单API地址
        String trade_type = "APP"; // 类型 扫码支付【网页扫码支付NATIVE】H5支付的交易类型为MWEB
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time_expire = sdf.format(time * 60 * 1000 + System.currentTimeMillis()); // 交易结束时间

        /**
         * 时间字符串
         */
        String currTime = PayForUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = PayForUtil.buildRandom(4) + "";
        String nonce_str = strTime + strRandom;

        /**
         * 参数封装
         */
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mch_id);
        packageParams.put("nonce_str", nonce_str);// 随机字符串
        packageParams.put("body", body);// 支付的产品名称
        packageParams.put("out_trade_no", out_trade_no);// 商户订单号【备注：每次发起请求都需要随机的字符串，否则失败。】
        packageParams.put("total_fee", total_fee);// 支付金额
        packageParams.put("spbill_create_ip", PayForUtil.localIp());//客户端主机
        packageParams.put("notify_url", url);
        packageParams.put("trade_type", trade_type);
        packageParams.put("attach", attach);
        packageParams.put("time_expire", time_expire);
        packageParams.put("sign_type", "HMAC-SHA256");
        String sign = PayForUtil.generateSignature(packageParams, key, "HMAC-SHA256");// 获取签名
        packageParams.put("sign", sign);
        System.out.println(packageParams.toString());
        String requestXML = PayForUtil.getRequestXml(packageParams);// 将请求参数转换成String类型
        String resXml = PayForUtil.postData(ufdoder_url, requestXML);// 解析请求之后的xml参数并且转换成String类型

        Map resp = XMLUtil.doXMLParse(resXml);
        Map<String, String> payMap = new HashMap();
        if ("SUCCESS".equals(resp.get("result_code")) && "SUCCESS".equals(resp.get("return_code"))) {
            /*获取微信返回的参数*/
            String appId = resp.get("appid").toString();
            String mchId = resp.get("mch_id").toString();
            String nonceStr = resp.get("nonce_str").toString();
            String prepayId = resp.get("prepay_id").toString();
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            /*添加二签参数*/
            Map<String, String> signMap = new HashMap();
            signMap.put("appid", appId);
            signMap.put("partnerid", mchId);
            signMap.put("prepayid", prepayId);
            signMap.put("noncestr", nonceStr);
            signMap.put("timestamp", timeStamp);
            signMap.put("package", "Sign=WXPay");
            /*添加返回值参数*/
            payMap.put("appid", appId);// 公众账号ID
            payMap.put("partnerid", mchId);// 商户号
            payMap.put("noncestr", nonceStr);// 随机字符串
            payMap.put("prepayid", prepayId);// 预支付交易会话标识
            payMap.put("ordercode", out_trade_no);// 订单编号
            payMap.put("tradetype", resp.get("trade_type").toString());// 交易类型
            payMap.put("package", "Sign=WXPay");
            /*二签*/
            payMap.put("sign", PayForUtil.generateSignature(signMap, key, "HMAC-SHA256"));// 微信签名
            payMap.put("timestamp", timeStamp);
            return payMap;
        }
        return null;
    }

    /**
     * 微信支付之后的回调方法
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public Object wechatNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 读取参数
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        inputStream.close();
        Map<String, String> m = new HashMap<String, String>();
        m = XMLUtil.doXMLParse(sb.toString());
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        Iterator<String> it = m.keySet().iterator();
        while (it.hasNext()) {
            String parameter = it.next();
            String parameterValue = m.get(parameter);
            String v = "";
            if (null != parameterValue) {
                v = parameterValue.trim();
            }
            packageParams.put(parameter, v);
        }
        String key = WXPayConfig.APIKEY; // key
        if (PayForUtil.isTenpaySign("UTF-8", packageParams, key)) {
            String resXml = "";
            if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            } else {
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        } else {
            System.err.println("通知签名验证失败");
        }
        return packageParams;
    }

}
