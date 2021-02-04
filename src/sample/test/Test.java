package sample.test;

import com.google.common.collect.Maps;
import javafx.application.Platform;
import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import sample.com.main.ParserMain;
import sample.com.main.baidu.utils.Base64Util;
import sample.com.main.controller.proxy.ProxyFactory;
import sample.com.main.controller.service.HandleService;
import sample.com.utils.LoggerUtils;
import sample.com.utils.StringUtils;
import sample.com.utils.excel.ReadExcelUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws SubException {
        RobotConstants.OPERATING_VAR = "start";
        try {
//            String dataFieldText = "F:\\公司资料\\机器人\\测试资料\\112.xlsx";
            String dataFieldText = "E:\\文档\\项目资料\\智能机器人\\测试资料\\123.xlsx";
            //1、读取数据表格
            ReadExcelUtil readExcelUtil = new ReadExcelUtil(dataFieldText);
            List<Map<String ,String >> list = readExcelUtil.getObjectsList();
            //循环excel表格中的所有数据
            for (Map<String ,String > dataMap : list) {
                //2、读取脚本文件，并返回脚本集，封为List
                List<Map> result = ParserMain.readScriptForList("G:\\逻辑脚本.txt");
                ParserMain.action(dataMap, result);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
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
