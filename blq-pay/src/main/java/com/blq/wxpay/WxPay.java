package com.blq.wxpay;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blq.common.utils.ResultMsg;
import com.blq.utils.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

/**
 * description: WxPay <br>
 * date: 2022/6/24 15:48 <br>
 * author: Blq <br>
 * version: 1.0 <br>
 */
public class WxPay {
    @Resource
    private WxPayService wxPayService;

     /**
      * @title payOrder
      * @description 小程序支付
      * @author Blq
      * @updateTime 2022/6/24 15:53
      * @throws
      */
    public ResultMsg payOrder (JSONObject jsonObject) {
        try {
            // 信息处理
            String payAllNumber = jsonObject.getString("payAllNumber");
            // 支付
            SortedMap<String, Object> parameters = new TreeMap<>();
            parameters.put("appid", WXPayConfig.WXAPPID);
            parameters.put("mch_id", WXPayConfig.WXMCHID);
            parameters.put("nonce_str", wxPayService.getNonceStr());
            parameters.put("body", "订单支付");
            SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0,0);
            parameters.put("out_trade_no", String.valueOf(snowflakeIdWorker.nextId()));
            parameters.put(
                    "total_fee",
                    new BigDecimal( Double.parseDouble(payAllNumber) * 100)
                            .setScale(0, BigDecimal.ROUND_HALF_UP));
            parameters.put("spbill_create_ip", "");
            parameters.put("notify_url", WXPayConfig.WECHAT_NOTIFY_URL_ORDER);
            parameters.put("trade_type", "JSAPI");
            parameters.put("openid", jsonObject.getString("payOpenId"));
            parameters.put("sign", wxPayService.createSign(parameters, WXPayConfig.APIKEY));
            String result =
                    wxPayService.postData(
                            WeixinUtil.unifiedorder,
                            wxPayService.getRequestXml(parameters));
            SortedMap<String, Object> map = XmlUtil.doXMLParse(result);
            System.err.println("====================================================");
            System.err.println(JSONArray.toJSONString(map));
            System.err.println("====================================================");
            String prepay_id = "";
            if ("SUCCESS".equals(map.get("return_code").toString())
                    && "SUCCESS".equals(map.get("result_code").toString())) {
                prepay_id = map.get("prepay_id").toString();
                if (prepay_id != null && prepay_id.length() > 10) {
                    String jsParam =
                            wxPayService.createPackageValue(
                                    WXPayConfig.APPID, WXPayConfig.APIKEY, prepay_id);
                    JSONObject object = JSONObject.parseObject(jsParam, JSONObject.class);
                    System.err.println("====================================================");
                    System.err.println(JSONArray.toJSONString(object));
                    System.err.println("====================================================");
                    return ResultMsg.OBJECT_SUCCESS.setNewData(object);
                }
            }
            return ResultMsg.OBJECT_ERROR;
        }catch (Exception e){
            e.printStackTrace();
            return ResultMsg.OBJECT_ERROR;
        }
    }

     /**
      * @title wxOrderNotifyUrl
      * @description 小程序支付回调地址
      * @author Blq
      * @updateTime 2022/6/24 15:54
      * @throws
      */
    public String wxOrderNotifyUrl (HttpServletRequest request, HttpServletResponse response) {
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
            Map<String, Object> m = new HashMap<String, Object>();
            m = XmlUtil.doXMLParse(sb.toString());
            Map<String, String> packageParams = new HashMap<>();
            Iterator<String> it = m.keySet().iterator();
            while (it.hasNext()) {
                String parameter = it.next();
                String parameterValue = (String) m.get(parameter);
                String v = "";
                if (null != parameterValue) {
                    v = parameterValue.trim();
                }
                packageParams.put(parameter, v);
            }
            String key = WXPayConfig.APIKEY;
            if (PayForUtil.isSignatureValid(packageParams, key, "MD5")) {
                String resXml = "";
                if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
                    // 订单编号
                    String code = packageParams.get("out_trade_no").toString();
                    // TODO消息通知

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
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }
}
