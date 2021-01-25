/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package sample.com.main.baidu.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Json工具类.
 */
public class GsonUtils {
    private static Gson gson = new GsonBuilder().create();

    public static String toJson(Object value) {
        return gson.toJson(value);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonParseException {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) throws JsonParseException {
        return (T) gson.fromJson(json, typeOfT);
    }


    public static void main(String[] args) {
        String json = "{'words_result':[{'words':' Eile Edit View Navigate Code Analyze Refactor Build Run Tools VCS Window Help','location':{'top':19,'left':2,'width':582,'height':24}},{'words':' automation','location':{'top':48,'left':6,'width':125,'height':19}},{'words':' ava) test baidu)G General','location':{'top':47,'left':241,'width':218,'height':19}},{'words':' GeneralBasic\'> C. I Git:√α','location':{'top':46,'left':1393,'width':522,'height':23}},{'words':' C OcrTestjava x ParserMainjava x','location':{'top':77,'left':556,'width':254,'height':19}},{'words':' automation C: Users\\Administrator ldeaProjects\\','location':{'top':104,'left':37,'width':293,'height':16}},{'words':'通用文字识别','location':{'top':115,'left':471,'width':84,'height':15}},{'words':' s GeneralBasic','location':{'top':157,'left':533,'width':115,'height':15}},{'words':'中国移动微法院','location':{'top':165,'left':905,'width':111,'height':16}},{'words':' private static String path OcrTest','location':{'top':177,'left':484,'width':257,'height':16}},{'words':' v java','location':{'top':188,'left':53,'width':64,'height':16}},{'words':' V.I co','location':{'top':210,'left':72,'width':54,'height':12}},{'words':'指尖司法','location':{'top':232,'left':869,'width':58,'height':16}},{'words':'击','location':{'top':216,'left':933,'width':42,'height':39}},{'words':'黑龙江移动微法院','location':{'top':295,'left':820,'width':277,'height':38}},{'words':' baidu','location':{'top':334,'left':109,'width':55,'height':15}},{'words':' .I robo','location':{'top':356,'left':108,'width':51,'height':14}},{'words':'.I tessractTest','location':{'top':378,'left':90,'width':109,'height':12}},{'words':' public static string generalBasicO)(','location':{'top':372,'left':483,'width':265,'height':23}},{'words':' Imager','location':{'top':396,'left':125,'width':70,'height':19}},{'words':' C ImagelOHel','location':{'top':418,'left':125,'width':93,'height':17}},{'words':' String url','location':{'top':417,'left':512,'width':74,'height':14}},{'words':'欢迎您,黄轲玮已认证','location':{'top':416,'left':778,'width':188,'height':18}},{'words':' C OcrTest','location':{'top':462,'left':130,'width':63,'height':12}},{'words':'本地文件路径','location':{'top':454,'left':562,'width':82,'height':17}},{'words':' ring fi','location':{'top':477,'left':555,'width':53,'height':16}},{'words':' I imgData = FileUtil','location':{'top':496,'left':569,'width':149,'height':16}},{'words':' Base64uti, en','location':{'top':517,'left':654,'width':92,'height':11}},{'words':'20170409092833472jpg','location':{'top':523,'left':110,'width':161,'height':17}},{'words':' ring imgParam URLEncode','location':{'top':537,'left':555,'width':179,'height':16}},{'words':'我要立案','location':{'top':527,'left':786,'width':73,'height':20}},{'words':' >. lib','location':{'top':608,'left':72,'width':54,'height':14}},{'words':'注意这里仅为了简化编码每一次','location':{'top':615,'left':561,'width':187,'height':16}},{'words':'端可自行缓存,过期后重新获取','location':{'top':614,'left':1166,'width':186,'height':17}},{'words':' Ig accessToken Authser','location':{'top':637,'left':575,'width':160,'height':15}},{'words':'诉前调解','location':{'top':632,'left':787,'width':72,'height':19}},{'words':'手机阅卷','location':{'top':633,'left':925,'width':72,'height':18}},{'words':'计算工具','location':{'top':633,'left':1068,'width':67,'height':18}},{'words':' GeneralBasic','location':{'top':714,'left':67,'width':79,'height':12}},{'words':'Q-我的案','location':{'top':742,'left':68,'width':63,'height':17}},{'words':'智能问答','location':{'top':736,'left':786,'width':73,'height':20}},{'words':'法规查询','location':{'top':736,'left':924,'width':73,'height':20}},{'words':'法院导航','location':{'top':736,'left':1062,'width':73,'height':20}},{'words':'系统开发使用文当doax\' \'words\':\'欢迎您,黄轲玮已认\',wrds\':\'x \'words:电脑管家易云首乐打码具n\',wrd:\'管理平台, words'\'\'万案19\',\'words\' navicat xe Pycharm Co?米客服中心, I\'words\': mmunity E.a版, \' words\'\'图 Twords\'演的案件','location':{'top':901,'left':179,'width':1740,'height':26}},{'words':' n 1s 428 ms(2 minutes a','location':{'top':1023,'left':198,'width':152,'height':14}},{'words':'中°,田','location':{'top':1015,'left':1144,'width':177,'height':27}},{'words':' 5 CRLF UTF-8 4 spaces Git: master','location':{'top':1019,'left':1596,'width':290,'height':19}},{'words':'java项目中cas','location':{'top':1052,'left':850,'width':100,'height':16}},{'words':'机器人解析器基…','location':{'top':1052,'left':1008,'width':100,'height':14}},{'words':'黑龙江移动微法院64','location':{'top':1038,'left':1434,'width':348,'height':35}}],'log_id':1325626970314113024,'words_result_num':50}";

    }
}
