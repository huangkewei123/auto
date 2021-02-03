package sample.test;

import com.google.common.collect.Maps;
import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import sample.com.main.baidu.utils.Base64Util;
import sample.com.main.controller.proxy.ProxyFactory;
import sample.com.main.controller.service.HandleService;
import sample.com.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws SubException {

    }


    public static List<String> charCount(String str,List<String> list ){
        if(str.contains(RobotConstants.VAR_START_TAG)){
            int x = str.indexOf(RobotConstants.VAR_START_TAG) + RobotConstants.VAR_START_TAG.length();
            int y = str.indexOf(RobotConstants.VAR_END_TAG);
            list.add(str.substring(x,y));
            str = str.replace(str.substring(str.indexOf(RobotConstants.VAR_START_TAG),y + RobotConstants.VAR_END_TAG.length()),"");
            charCount(str , list );
        }
        return list;
    }

    public static String charCount(String str, Map<String , String> dataMap) throws SubException {
        String variable = null;
        if(str.contains(RobotConstants.VAR_START_TAG)){
            int x = str.indexOf(RobotConstants.VAR_START_TAG) + RobotConstants.VAR_START_TAG.length();
            int y = str.indexOf(RobotConstants.VAR_END_TAG);
            try {
                variable = str.substring(x,y);
                str = str.replace(str.substring(str.indexOf(RobotConstants.VAR_START_TAG),y + RobotConstants.VAR_END_TAG.length()),dataMap.get(variable));
                str = charCount(str  , dataMap);
            } catch (NullPointerException e) {

                throw new SubException("您填写的变量不存在，请仔细查阅表头是否包含此变量" + variable);
            }
        }
        return str;
    }

    public void test(){

//        Base64Util.generateImage(a,"image01.png");
    }
}
