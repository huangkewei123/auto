package sample.test;

import com.google.common.collect.Maps;
import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import sample.com.main.baidu.utils.Base64Util;
import sample.com.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws SubException {
//        String param = "E:\\文档\\项目资料\\智能机器人\\{{测试层级}}\\测试资料\\{{案码}}{{被告姓名}}";
        String param = "{{被告姓名22}}";
        Map dataMap = Maps.newHashMap();
        dataMap.put("案码","1");
        dataMap.put("被告姓名","王一博");
        dataMap.put("测试层级","测试一下");


        /*if(param.contains(RobotConstants.VAR_START_TAG) && param.contains(RobotConstants.VAR_END_TAG)){
            String arr[] = param.split("/");
            for (int i = 0 ; i < arr.length ; i++ ) {
                String p = arr[i];
                if(param.contains(RobotConstants.VAR_START_TAG) && param.contains(RobotConstants.VAR_END_TAG)) {
                    if()
                    int start = p.indexOf(RobotConstants.VAR_START_TAG);
                    int end = p.indexOf(RobotConstants.VAR_END_TAG);
                    p = p.substring(start , end);
                    //3、在读取操作文件时，将数据与操作文件特定事件的参数做绑定
                    param = StringUtils.getParamValue(param, RobotConstants.VAR_START_TAG, RobotConstants.VAR_END_TAG);
                    //将{{A}}处理成A
                    paramArr[j] = dataMap.get(param);
                }
            }

            System.out.println(param);
        }*/
        /*List<String> list = new ArrayList<String>();
        list = charCount(param,list , dataMap);
        for (String a : list ) {
            System.out.println(a);
        }*/
        param = charCount(param , dataMap);
        System.out.println(param);

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
