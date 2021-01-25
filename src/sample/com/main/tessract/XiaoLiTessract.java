package sample.com.main.tessract;

import sample.com.exception.SubException;
import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import sample.com.main.baidu.utils.Base64Util;
import sample.com.main.baidu.utils.FileUtil;
import sample.com.main.baidu.utils.GsonUtils;
import sample.com.main.baidu.utils.HttpUtil;
import sample.com.utils.LoggerUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class XiaoLiTessract {

    /**
     * ocr带位置的请求地址
     */
    private final static String URL = "http://42.192.222.234:10080/ocr";

    public static Map<String , Integer> general(String filePath , String ocrText ) {
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);

            String param = "{\"image\":\"" + imgStr + "\"}" ;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String responseStr = HttpUtil.post(URL, null, param);

            Map responseMap = GsonUtils.fromJson(responseStr , new TypeToken<Map>() {}.getType());
            ArrayList resultList = (ArrayList) responseMap.get("words_result");
            //resultList内层嵌套的arrayList
            ArrayList innerList = new ArrayList();
            Map<String , Integer> resultMap = Maps.newHashMap();
            for (int i = 0 ; i < resultList.size() ; i++) {
                innerList = (ArrayList)resultList.get(i);
                LinkedTreeMap tree = (LinkedTreeMap) innerList.get(0);
                String words = tree.get("words").toString();//解析的中文
                if(ocrText.equals(words)){
                    System.out.println("文字匹配成功---->:" + words);
                    LinkedTreeMap locations = (LinkedTreeMap) tree.get("location");//中文坐标
                    Integer top = Integer.parseInt(locations.get("top").toString());
                    Integer left = Integer.parseInt(locations.get("left").toString());
                    resultMap.put("x",top);
                    resultMap.put("y",left);
                }
            }
            return resultMap;
        } catch (Exception e) {
            LoggerUtils.error(BaiduGeneral.class , e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Map> imgOcrGetResultStr(String filePath){
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String param = "{\"image\":\"" + imgStr + "\"}" ;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            //String accessToken = AuthService.getAuth();//"[调用鉴权接口获取的token]";
            String responseStr = HttpUtil.post(URL, null, param);
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
        XiaoLiTessract.general("E:\\文档\\项目资料\\智能机器人\\衡阳诉讼系统截图\\image10.png", "盗剑瓶2难岛" );
//        XiaoLiTessract.imgOcrGetResultStr("E:\\文档\\项目资料\\智能机器人\\衡阳诉讼系统截图\\image10.png");
    }
}
