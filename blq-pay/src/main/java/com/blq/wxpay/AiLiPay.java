package com.blq.wxpay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.blq.aliUtils.AlipayConfig;
import com.blq.aliUtils.AlipayUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * description: AiLiPay <br>
 * date: 2022/6/24 16:34 <br>
 * author: Blq <br>
 * version: 1.0 <br>
 */
public class AiLiPay {

     /**
      * @title aLiPay
      * @description 支付宝支付
      * @author Blq
      * @updateTime 2022/6/24 16:40
      * @throws
      */
    public String aLiPay (JSONObject jsonObject) {
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("订单支付");
        model.setSubject("订单支付");
        model.setTimeoutExpress("5m");
        model.setTotalAmount(jsonObject.getString("actualPrice"));
        model.setOutTradeNo(jsonObject.getString("orderCode"));
        String result =
                AlipayUtil.appPay(model, new AlipayConfig().getNotify_url_order());
        System.err.println("支付返回结果：" + result);
        return  result;
    }

     /**
      * @title aliOrderNotifyUrl
      * @description 支付宝回调
      * @author Blq
      * @updateTime 2022/6/24 16:44
      * @throws
      */
    public String aliOrderNotifyUrl(HttpServletRequest request) {
        Map<String, String> map = AlipayUtil.alipayNotify(request);
        System.err.println("回调信息：" + map.toString());
        if (map.get("errorCode").equals("0")) {
            String code = map.get("out_trade_no").toString();
            return "success";
        } else {
            return "fail";
        }
    }
}
