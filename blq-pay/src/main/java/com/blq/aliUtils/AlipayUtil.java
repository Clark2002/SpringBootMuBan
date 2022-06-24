package com.blq.aliUtils;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Component
public class AlipayUtil {
    // 1.电脑网站支付产品alipay.trade.page.pay接口中，product_code为：FAST_INSTANT_TRADE_PAY
    // 2.手机网站支付产品alipay.trade.wap.pay接口中，product_code为：QUICK_WAP_WAY
    // 3.当面付条码支付产品alipay.trade.pay接口中，product_code为：FACE_TO_FACE_PAYMENT
    // 4.APP支付产品alipay.trade.app.pay接口中，product_code为：QUICK_MSECURITY_PAY

    /**
     * 获取AlipayClient对象
     *
     * @return
     */
    private static AlipayClient getAlipayClient() {
        AlipayConfig alipayConfig = new AlipayConfig();
        AlipayClient alipayClient =
                new DefaultAlipayClient(alipayConfig.getGatewayUrl(), alipayConfig.getApp_id(), alipayConfig.getMerchant_private_key(),
                        "JSON", alipayConfig.getCharset(), alipayConfig.getAlipay_public_key(), alipayConfig.getSign_type()); // 获得初始化的AlipayClient
        return alipayClient;

    }

    /**
     * 扫码支付
     * 验签通过返回QrResponse
     * 失败打印日志信息
     *
     * @param model
     * @return
     */
    public static Map<String, Object> qrcodePay(AlipayTradePrecreateModel model) {
        AlipayClient alipayClient = getAlipayClient();
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setBizModel(model);
        request.setNotifyUrl(new AlipayConfig().getNotify_url_pay());
        AlipayTradePrecreateResponse alipayTradePrecreateResponse = null;
        try {
            alipayTradePrecreateResponse = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        Map<String, Object> qrResponse = JSON.parseObject(alipayTradePrecreateResponse.getBody(), Map.class);
        System.err.println("qrResponse：" + qrResponse.toString());
        Map<String, Object> qrResponse1 = JSON.parseObject(String.valueOf(qrResponse.get("alipay_trade_precreate_response")), Map.class);
        return qrResponse1;
    }

    /**
     * app支付
     * 验签通过返回QrResponse
     *
     * @return
     */
    public static String appPay(AlipayTradeAppPayModel model, String url) {
        // 实例化客户端
        AlipayClient alipayClient = getAlipayClient();
        // 实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(url);
        try {
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());
            return response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 支付的回调
     *
     * @param request
     * @return
     */

    public static Map<String, String> alipayNotify(HttpServletRequest request) {
        Map<String, String> map = AlipayUtils.ParamstoMap(request);
        AlipayConfig alipayConfig = new AlipayConfig();
        try {
            boolean flag = AlipaySignature.rsaCheckV1(map, alipayConfig.getAlipay_public_key(), alipayConfig.getCharset(),
                    alipayConfig.getSign_type());
            String json = JSON.toJSONString(map);
            if (flag) {
                map.put("errorCode", "0");
                return map;
            } else {
                map.put("errorCode", "1");
                return map;
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            map.put("errorCode", "1");
            return map;
        }
    }

}
