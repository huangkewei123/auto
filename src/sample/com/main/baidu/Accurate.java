package sample.com.main.baidu;


import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import sample.com.main.baidu.utils.Base64Util;
import sample.com.main.baidu.utils.FileUtil;
import sample.com.main.baidu.utils.GsonUtils;
import sample.com.main.baidu.utils.HttpUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * 通用文字识别（高精度含位置版）
 */
public class Accurate {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * 下载
     */
    public static Map<String , Integer> accurate(String filePath , String ocrText) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate";
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();//"[调用鉴权接口获取的token]";

            String result = HttpUtil.post(url, accessToken, param);

            Map responseMap = GsonUtils.fromJson(result , new TypeToken<Map>() {}.getType());
            ArrayList a = (ArrayList) responseMap.get("words_result");
            Double top = 0.0;
            Double left = 0.0;
            Map<String , Integer> resultMap = Maps.newHashMap();
            for (int i = 0 ; i < a.size() ; i++) {
                Object o = a.get(i);
                LinkedTreeMap tree = (LinkedTreeMap) o;
                String words = tree.get("words").toString();//解析的中文
                if(ocrText.equals(words)){
                    System.out.println("文字匹配成功" + words);
                    LinkedTreeMap locations = (LinkedTreeMap) tree.get("location");//中文坐标
                    top = (Double) locations.get("top");
                    left = (Double) locations.get("left");
                    int y = top.intValue();
                    int  x = left.intValue();
                    resultMap.put("x",x);
                    resultMap.put("y",y);
                    System.out.println("x ss" + x);
                    System.out.println("y ss" + y);
                }
            }
            return resultMap;
//            System.out.println(result);
//            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Accurate.accurate("F:\\image.jpg" , "确定");
    }
}