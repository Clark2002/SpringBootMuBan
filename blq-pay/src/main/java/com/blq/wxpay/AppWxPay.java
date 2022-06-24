package com.blq.wxpay;

import com.alibaba.fastjson.JSONObject;
import com.blq.common.utils.DateUtils;
import com.blq.utils.PayForUtil;
import com.blq.utils.WXPay;
import com.blq.utils.WXPayConfig;
import com.blq.wxPayUtil.XMLUtil;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;

/**
 * description: AppWxPay <br>
 * date: 2022/6/24 16:18 <br>
 * author: Blq <br>
 * version: 1.0 <br>
 */
public class AppWxPay {

     /**
      * @title appWxPay
      * @description 微信APP支付
      * @author Blq
      * @updateTime 2022/6/24 16:30
      * @throws
      */
    @SneakyThrows
    public Map<String, String> appWxPay (JSONObject jsonObject) {
        Map<String, String> map =
                WXPay.getAppPay(
                        new BigDecimal(jsonObject.getDouble("actualPrice") * 100)
                                .setScale(0, BigDecimal.ROUND_HALF_UP)
                                .toString(),
                        "订单支付",
                        jsonObject.getString("orderCode"),
                        "",
                        5,
                        WXPayConfig.WECHAT_NOTIFY_URL_ORDER_APP);
        System.err.println("支付返回结果：" + map.toString());
        return map;
    }

     /**
      * @title wxOrderNotifyUrl
      * @description 微信App支付回调
      * @author Blq
      * @updateTime 2022/6/24 16:34
      * @throws
      */
    public String wxOrderNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        try {
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
            Map<String, String> packageParams = new HashMap<>();
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
            String key = WXPayConfig.APIKEY;
            // 判断签名是否正确
            if (PayForUtil.isSignatureValid(packageParams, key, "MD5")) {
                String resXml = "";
                if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
                    System.err.println(packageParams.toString());
                    String code = packageParams.get("out_trade_no").toString();
                    //  TODO 业务逻辑
                    resXml =
                            "<xml>"
                                    + "<return_code><![CDATA[SUCCESS]]></return_code>"
                                    + "<return_msg><![CDATA[OK]]></return_msg>"
                                    + "</xml> ";
                } else {
                    resXml =
                            "<xml>"
                                    + "<return_code><![CDATA[FAIL]]></return_code>"
                                    + "<return_msg><![CDATA[报文为空]]></return_msg>"
                                    + "</xml> ";
                }
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
            } else {
                System.out.println("通知签名验证失败");
                return "fail";
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
