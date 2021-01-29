package sample.com.main;


import com.google.common.collect.Maps;
import sample.com.constants.ExceptionConstants;
import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import sample.com.main.baidu.utils.FileUtil;
import sample.com.main.controller.proxy.ProxyFactory;
import sample.com.main.controller.service.HandleService;
import sample.com.utils.LoggerUtils;
import sample.com.utils.ReflectUtil;
import sample.com.utils.StringUtils;
import sample.com.utils.ThreadConinfguration.CallableTask;
import sample.com.utils.ThreadConinfguration.ThreadConfiguration;
import sample.com.utils.excel.ReadExcelUtil;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ParserMain {

    /**
     * 每次将需要的操作传入方法
     * 用于解析脚本中的步骤
     * 脚本逐行接收（一行脚本调用一次）
     * 如果不是已定义的函数，则返回false
     * @param handleName 脚本中指令名称
     * @return；
     */
    public static boolean parserHandle(String handleName){
        ReflectUtil.notNull(handleName, "handleName must not be null");
        boolean result = false;
        if (StringUtils.isNotEmpty(handleName)) {
            switch (handleName) {
                default:
                    result = false;
                case RobotConstants.MOUSE_LOCATION:
                    result = true;
                    break;
                case RobotConstants.MOUSE_CLICK:
                    result = true;
                    break;
                case RobotConstants.CUT:
                    result = true;
                    break;
                case RobotConstants.MOUSE_FREE:
                    result = true;
                    break;
                case RobotConstants.MOUSE_HOLD:
                    result = true;
                    break;
                case RobotConstants.MOUSE_OFFSET:
                    result = true;
                    break;
                case RobotConstants.LOCAL_MOUSE_OFFSET:
                    result = true;
                    break;
                case RobotConstants.CUT_PART:
                    result = true;
                    break;
                case RobotConstants.MOUSE_TEXT:
                    result = true;
                    break;
                case RobotConstants.WAIT:
                    result = true;
                    break;
                case RobotConstants.EACH_CUT:
                    result = true;
                    break;
                case RobotConstants.STOP:
                    result = true;
                    break;
                case RobotConstants.MOUSELOCANDCLICK:
                    result = true;
                    break;
                case RobotConstants.MOUSEWHEEL:
                    result = true;
                    break;
                case RobotConstants.PAUSE:
                    result = true;
                    break;
                case RobotConstants.SELECTDATE:
                    result = true;
                    break;
                case RobotConstants.COMBHOTKEY:
                    result = true;
                    break;
                case RobotConstants.SELECTALL:
                    result = true;
                    break;
                case RobotConstants.MOUSELOCATIONXY:
                    result = true;
                    break;
                case RobotConstants.MOUSEMOVEANDCLICK:
                    result = true;
                    break;
                case RobotConstants.ENTER:
                    result = true;
                    break;
            }
            return result;
        }
        return result;
    }


    /**
     * 读取脚本文件
     * 读取后封装至List<Map<String ,String>>里
     * 其中部分格式需转换，并拆分
     */
    public static List<Map> readScriptForList(String filePath) throws SubException, IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            LoggerUtils.error(FileUtil.class, ExceptionConstants.PATH_EXISTS + filePath);
            throw new SubException(filePath);
        }
        if (file.length() > 1024 * 1024 * 300) {
            LoggerUtils.error(FileUtil.class,ExceptionConstants.FILE_IS_TOO_LARGE);
            throw new SubException(ExceptionConstants.FILE_IS_TOO_LARGE);
        }
        //获得文件读取流
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis,"GBK");
        BufferedReader br = new BufferedReader(isr);
        //每行的文字
        String lineText = null;
        //操作名称
        String handleName = null;
        List<Map> resultList = new ArrayList<Map>();
        int strCount = 0;
        int currentLine = 1;
        while((lineText = br.readLine()) != null){
            //两条斜杠代表注释，拿到行数据后首先将斜杠后的字符全部清除
            if(lineText.contains("//")){
                lineText = lineText.substring(0,lineText.indexOf("//"));
            }
            //检查脚本每行是否是以分号结尾
            if(!lineText.endsWith(";")){
                LoggerUtils.error(FileUtil.class , "当前行：" + currentLine + "，" + ExceptionConstants.LAST_CHART_SEMICOLON);
                throw new SubException("当前行：" + currentLine + "，" + ExceptionConstants.LAST_CHART_SEMICOLON);
            };
            //脚本中不允许存在空行命令
            if (StringUtils.isEmpty(lineText)){
                LoggerUtils.error(FileUtil.class , "当前行：" + currentLine + "，" + ExceptionConstants.SCRIPT_SOME_ROWS_NULL);
                throw new SubException("当前行：" + currentLine + "，" + ExceptionConstants.SCRIPT_SOME_ROWS_NULL);
            }

            //创建结果map，最终结果处理为Map<String , Object[]>
            Map result = Maps.newHashMap();

            //兼容支持在同一行内编辑多个函数，以分号分割
            strCount = getCount(lineText , ";");
            if(strCount >= 1){
                String [] textArr = lineText.split(";");
                //循环取出函数并放入resultList里
                for (String text : textArr) {
                    int index = text.indexOf(RobotConstants.PARAM_START_TAG);
                    handleName = text.substring(0 , index);
                    result.put(handleName , StringUtils.getParamValue(text , RobotConstants.PARAM_START_TAG , RobotConstants.PARAM_END_TAG));
                    resultList.add(result);
                    currentLine++;
                }
            }else if(strCount == 0){
                LoggerUtils.error(FileUtil.class , "当前行：" + currentLine + "，" + ExceptionConstants.NO_SEMICOLON);
                throw new SubException("当前行：" + currentLine + "，" + ExceptionConstants.NO_SEMICOLON);
            }
//            isr.close();
//            br.close();
//            fis.close();
        }
        return resultList;
    }

    /**
     *
     * @param dataMap
     * @param scriptList    脚本集,脚本的所有数据都在list中，list中的map为已被分解的方法名和参数
     */
    public static void action(Map<String, String> dataMap, List<Map> scriptList) throws SubException {
        Map<String , String > handleMap;
        for (int i = 0; i < scriptList.size(); i++) {
            handleMap = scriptList.get(i);
            //如果暂停变量为初始值，则可通过，初始值为false
            //点击暂停键后变量修改为true，为true时则死循环作为暂停
            while (RobotConstants.OPERATING_VAR.equals("pause")){
                LoggerUtils.warning(ParserMain.class,"程序已暂停....\n");
                try {
                    HandleService.getRobot().delay(3000);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
            //如变量变成stop
            //则退出循环，结束运行
            if(RobotConstants.OPERATING_VAR.equals("stop")){
                LoggerUtils.warning(ParserMain.class,"程序停止中....\n");
                break;
            }
            for (String handleName : handleMap.keySet()){
                System.out.println("读取动作 ：" + handleName);
                //根据handleName进行判断动作是否为定义动作，如果不是则抛出异常
                //不用try catch捕捉异常，百度搜索更优雅的方法
                boolean isHandleName = ParserMain.parserHandle(handleName);
                //拿出和方法名对应的参数个数及参数每个参数的类型.class
                String values = handleMap.get(handleName);
                Class [] clazzParams = null;
                String [] paramArr = null;
                if(StringUtils.isNotEmpty(values)){
                    paramArr = values.split(RobotConstants.SPILIT_SYMBOL);
                    clazzParams = new Class [paramArr.length];
                    for (int j = 0 ; j < paramArr.length ; j++ ){
                        clazzParams[j] = paramArr[j].getClass();
                        //将{{参数}}与excel表中的数据绑定
                        String param = paramArr[j];
                        if(param.contains(RobotConstants.VAR_START_TAG) && param.contains(RobotConstants.VAR_END_TAG)){
                            //3、在读取操作文件时，将数据与操作文件特定事件的参数做绑定
//                            System.out.println(param);
                            LoggerUtils.info(ParserMain.class , param);
                            //将{{A}}处理成A
                            paramArr[j] = StringUtils.variableChange(param , dataMap);
//                            param = StringUtils.getParamValue(param , RobotConstants.VAR_START_TAG , RobotConstants.VAR_END_TAG);
                            //将{{A}}处理成A
//                            paramArr[j] = dataMap.get(param);
                        }
                    }
                }
                //handleName通过后，想办法找到对应的方法，并将参数传入执行。
                if (isHandleName){
//                    System.out.println("找到方法 ：" + handleName);
                    Future<Object> f = ThreadConfiguration.THREAD_POOL.submit(new CallableTask("sample.com.main.controller.HandleController", handleName, paramArr));
                    try {
                        System.out.println("调用方法成功返回---"+ f.get());
                    } catch (InterruptedException e) {
                        String error = "第" + i + "个函数出错，方法名为" + handleName + "，参数为" + values + "，请参照脚本手册检查。\n";
                        LoggerUtils.error(ParserMain.class ,error , e);
                        throw new SubException(error , e);
                    } catch (ExecutionException e) {
                        String error = "程序出错，请联系开发人员进行处理。\n";
                        LoggerUtils.error(ParserMain.class ,error , e);
                        throw new SubException(error , e);
                    }
                }
            }
        }
    }

    public static void start() throws InterruptedException, IOException, SubException {
        String dataFieldText = "G:\\job\\1_黑龙江50件要素清单表完整版文书生成.xlsx";
        String scriptFieldText = "G:\\job\\被告信息填写.txt";
        String delay = "1";
        ProxyFactory.nomalDelay = delay;
        if(StringUtils.isEmpty(scriptFieldText) || StringUtils.isEmpty(dataFieldText)){
            System.out.println("未选择案件信息表或执行脚本，请选择完成后开始执行");
            LoggerUtils.error(ParserMain.class,"未选择案件信息表或执行脚本，请选择完成后开始执行");
        }
        System.out.println("准备开始执行");
        Thread.sleep(5000);

        //键盘操作后再做下一步循环
        Scanner sc = new Scanner(System.in);
        //1、读取数据表格
        ReadExcelUtil readExcelUtil=new ReadExcelUtil(dataFieldText);
        System.out.println("读取案件信息表数据");
        List<Map<String ,String >> list = readExcelUtil.getObjectsList();
        //每条数据都需循环
        for (Map<String ,String > dataMap : list) {
            //2、读取操作文件
            List<Map> result = ParserMain.readScriptForList(scriptFieldText);
            ParserMain.action(dataMap, result);
//                String str = sc.nextLine();
//                System.out.println(str);
        }
    }

    public static int getCount(String mainStr,String subStr){
        int minLength=mainStr.length();
        int subLength=subStr.length();
        int count=0;
        int index=0;

        if(minLength>=subLength){
            while ((index=mainStr.indexOf(subStr,index))!=-1){
                count++;
                index+=subLength;
            }
            return count;
        }
        return -1;
    }

    public static void main(String[] args) throws AWTException {
        /*try {
            ParserMain.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SubException e) {
            e.printStackTrace();
        }*/
        //ParserMain.action("我要立案");
        String a = "阿斯顿发斯蒂芬;//asdfasdfasdfasd";
        a = a.substring(0,a.indexOf("//"));
        System.out.println(a);
        System.out.println(a.endsWith(";"));
    }
}
