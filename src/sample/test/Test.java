package sample.test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
        /*RobotConstants.OPERATING_VAR = "start";
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
        }*/
        /*List<Map> list = new ArrayList<>();
        for (int i = 0; i < 10 ; i++){
            test1(i , list , 0 ,i);
        }
        Gson g = new Gson();
        System.out.println(g.toJson(list));*/

        testIf();
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

    public static void test(){
        String a = "if-if-if-if-a";
        String c = a.replace("if-","");
        System.out.println(c);
//        Base64Util.generateImage(a,"image01.png");
    }

    public static List<Map> test1(int line , List<Map> list , int pid ,int els){
        Map map = Maps.newLinkedHashMap();
        map.put("line" , line);
        map.put("pid" , pid);
        if(els == 3 ){
            list.add(map);
            return list;
        }else if(els == 4 ){
            List<Map> sublist = new ArrayList<>();
            Map subMap = Maps.newLinkedHashMap();
            subMap.put("line" , line+1);
            subMap.put("pid" , pid);
            sublist.add(subMap);
            map.put("sublist",test1(line , sublist , line , 10));
            list.add(map);
            return list;
        }else if(els == 10 ){
            List<Map> sublist = new ArrayList<>();
            Map subMap = Maps.newLinkedHashMap();
            subMap.put("line" , line+1);
            subMap.put("pid" , pid);
            sublist.add(subMap);
            map.put("sublist",test1(line , sublist , line , 11));
            list.add(map);
            return list;
        }else{
            return list;
        }

    }

    public static void testIf(){
        if(1==1){
            System.out.println(1);
            if(3==3){
                System.out.println("1==>2层");
            }
        }else if(2==2){
            System.out.println(2);
        }else{
            System.out.println(3);
        }
    }


    /*public static Map package(){
        Entity en = new Entity();
        en.setLevel(1);
        en.setHandleName("inputText");
        en.setParameter("测试");
        en.setLine(1);
        en.setType("normal");

        List subList = new ArrayList();

        Entity subEn = new Entity();
        subEn.setLevel(1);
        subEn.setHandleName("inputText");
        subEn.setParameter("测试第一层");
        subEn.setLine(2);
        subEn.setType("if");
        subEn.setHaveSub(true);
        subList.add(subEn);

        Entity subEn = new Entity();
        subEn.setLevel(1);
        subEn.setHandleName("inputText");
        subEn.setParameter("测试第一层");
        subEn.setLine(2);
        subEn.setType("if");
        subEn.setHaveSub(true);
        subList.add(subEn);

        Entity subEn = new Entity();
        subEn.setLevel(1);
        subEn.setHandleName("inputText");
        subEn.setParameter("测试第一层");
        subEn.setLine(2);
        subEn.setType("if");
        subEn.setHaveSub(true);
        subList.add(subEn);


        Entity en1 = new Entity();
        en1.setLevel(1);
        en1.setHandleName("if");
        en1.setParameter("cut();");
        en1.setLine(2);
        en1.setType("if");
        en1.setHaveSub(true);
        en1.setSubList(subList);


    }*/
}
