package sample.com.main.tessract;


import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import sample.com.main.baidu.AuthService;
import sample.com.main.baidu.utils.Base64Util;
import sample.com.main.baidu.utils.FileUtil;
import sample.com.main.baidu.utils.GsonUtils;
import sample.com.main.baidu.utils.HttpUtil;
import sample.com.utils.LoggerUtils;

import java.awt.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

public class BaiduGeneral {

    /**
     * 百度带位置的请求地址
     */
    private final static String URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general";

    private final static String HIGH_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate";
    /**
    * 重要提示代码中所需工具类
    * FileUtil,Base64Util,HttpUtil,GsonUtils请从
    * 下载
    */

    public static Map<String , Integer> general(String filePath , String ocrText , String highDefinition) {
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam ;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();//"[调用鉴权接口获取的token]";
            String responseStr = null;
            if(highDefinition.equals("1"))
                responseStr = HttpUtil.post(URL, accessToken, param);
            else if(highDefinition.equals("2"))
                responseStr = HttpUtil.post(HIGH_URL, accessToken, param);
            else {
                LoggerUtils.error(BaiduGeneral.class , "图片识别错误，请检查脚本参数,标清为1，高清为2");
                throw new SubException("图片识别错误，请检查脚本参数,标清为1，高清为2");
            }

            Map responseMap = GsonUtils.fromJson(responseStr , new TypeToken<Map>() {}.getType());
            ArrayList resultList = (ArrayList) responseMap.get("words_result");
            Double top = 0.0;
            Double left = 0.0;
            Map<String , Integer> resultMap = Maps.newHashMap();
            for (int i = 0 ; i < resultList.size() ; i++) {
                Object o = resultList.get(i);
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
        } catch (Exception e) {
            LoggerUtils.error(BaiduGeneral.class , e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Map> imgOcrGetResultStr(String filePath ,  String highDefinition){
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam ;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();//"[调用鉴权接口获取的token]";
            String responseStr = null;
            if(highDefinition.equals("1"))
                responseStr = HttpUtil.post(URL, accessToken, param);
            else if(highDefinition.equals("2"))
                responseStr = HttpUtil.post(HIGH_URL, accessToken, param);
            else {
                LoggerUtils.error(BaiduGeneral.class , "图片识别错误，请检查脚本参数,标清为1，高清为2");
                throw new SubException("图片识别错误，请检查脚本参数,标清为1，高清为2");
            }

            Map responseMap = GsonUtils.fromJson(responseStr , new TypeToken<Map>() {}.getType());
            ArrayList resultList = (ArrayList) responseMap.get("words_result");
            return resultList;
        } catch (Exception e) {
            LoggerUtils.error(BaiduGeneral.class , e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws AWTException, SubException {
//        HandleService service = new HandleService();
//        service.cut();
        BaiduGeneral.general(RobotConstants.IMAGE_PATH, "1942年"  , "2");
    }
}
