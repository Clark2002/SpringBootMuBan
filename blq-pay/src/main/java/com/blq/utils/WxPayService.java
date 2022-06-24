package com.blq.utils;


import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.*;

/**
 * description: WxPayService <br>
 * date: 2022/6/9 9:31 <br>
 * author: 18721 <br>
 * version: 1.0 <br>
 */
@Service
public class WxPayService {

    public static String setXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
    }

    /**
     * 发送请求，并返回微信端响应数据
     *
     * @param urlStr 请求的接口url
     * @param data   xml格式的请求参数
     * @return 微信端的响应数据，xml格式字符串
     */
    public String postData(String urlStr, String data) {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            if (data == null)
                data = "";
            writer.write(data);
            writer.flush();
            writer.close();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * 获得xml格式的请求参数
     *
     * @param parameters SortedMap
     * @return xml格式的请求参数
     */
    public String getRequestXml(SortedMap<String, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set<Map.Entry<String, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<String, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            Object ok = entry.getKey();
            String k = ok == null ? null : ok.toString();
            Object ov = entry.getValue();
            String v = ov == null ? null : ov.toString();
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 生成签名
     * 生成规则详见微信API
     *
     * @param packageParams 不包括签名项的请求参数
     * @param API_KEY       微信API秘钥
     * @return 请求所必须的签名
     */
    public String createSign(SortedMap<String, Object> packageParams, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Object>> es = packageParams.entrySet();
        Iterator<Map.Entry<String, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            Object ok = entry.getKey();
            String k = ok == null ? null : ok.toString();
            Object ov = entry.getValue();
            String v = ov == null ? null : ov.toString();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + API_KEY);
        String sign = Md5Util.MD5Encode(sb.toString(), "utf-8").toUpperCase();
        return sign;
    }

    /**
     * 验证是否签名正确,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     *
     * @param 编码格式，返回的数据，API KEY
     * @return boolean
     */
    public boolean isTenpaySign(String characterEncoding, SortedMap<String, Object> packageParams, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object ok = entry.getKey();
            String k = ok == null ? null : ok.toString();
            Object ov = entry.getValue();
            String v = ok == null ? null : ov.toString();
            if (!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }

        sb.append("key=" + API_KEY);

        //算出摘要
        String mysign = Md5Util.MD5Encode(sb.toString(), characterEncoding).toLowerCase();
        String tenpaySign = ((String) packageParams.get("sign")).toLowerCase();

        return tenpaySign.equals(mysign);
    }

    /**
     * 获得随机字符串（位数不限，最高32位）
     * 目的是让生成的签名不可预测
     *
     * @return
     */
    public String getNonceStr() {
        String timeStamp = String.valueOf((System.currentTimeMillis()));
        String randomStr = String.valueOf(new Random().nextInt(100000));
        String nonceStr = Md5Util.MD5Encode(timeStamp.concat(randomStr), "utf-8");
        int maxLength = nonceStr.length() > 32 ? 32 : nonceStr.length();
        return nonceStr.substring(0, maxLength);
    }

    /**
     * 将普通bean中的（属性名-值）转为SortedMap（键-值）
     * 目的是为其自然排序（应生成签名需要，微信官方demo中生成签名的方法未排序，导致签名失败）
     *
     * @param object
     * @return
     */
    public SortedMap<String, Object> toSortedMap(Object object) {
        SortedMap<String, Object> map = new TreeMap<String, Object>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            String fieldName = field.getName();
            String getMethodName = "get".concat(fieldName.substring(0, 1).toUpperCase()).concat(fieldName.substring(1));
            try {
                Method method = object.getClass().getMethod(getMethodName);
                obj = method.invoke(object);
                if (obj != null && !"sign".equals(fieldName)) {
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 封装接口支付参数
     *
     * @param appid
     * @param appKey
     * @param prepay_id
     * @return
     */
    public String createPackageValue(String appid, String appKey, String prepay_id) {
        // 封装调用支付接口所需参数
        SortedMap<String, Object> nativeObj = new TreeMap<>();
        nativeObj.put("appId", appid);// 应用id
        nativeObj.put("timeStamp", OrderUtil.GetTimestamp());// 时间戳
        // 生成随机数
        Random random = new Random();
        String randomStr = MD5.GetMD5String(String.valueOf(random.nextInt(10000)));
        nativeObj.put("nonceStr", Md5Util.MD5Encode(randomStr, "utf-8").toLowerCase());// 随机字符串
        nativeObj.put("package", "prepay_id=" + prepay_id);
        nativeObj.put("signType", "MD5");
        nativeObj.put("paySign", createSign(nativeObj, appKey));// 签名
        return JSONObject.fromObject(nativeObj).toString();
    }

    // 使用证书
    public String useCert(String certificatePath, String certificatePassword, String data, String interfaceUrl) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(certificatePath));
        try {
            keyStore.load(instream, certificatePassword.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, certificatePassword.toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            HttpPost httpPost = new HttpPost(interfaceUrl);
            httpPost.setEntity(new StringEntity(data, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                StringBuilder sb = new StringBuilder();
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String text;
                    while ((text = bufferedReader.readLine()) != null) {
                        if (!text.contains("<err_code_des>"))
                            sb = sb.append(text);
                    }
                }
                EntityUtils.consume(entity);
                return sb.toString();
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}
