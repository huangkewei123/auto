package sample.com.main;


import com.google.common.collect.Maps;
import sample.com.constants.ExceptionConstants;
import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import sample.com.main.baidu.utils.FileUtil;
import sample.com.main.controller.service.HandleService;
import sample.com.utils.*;
import sample.com.utils.ThreadConinfguration.CallableTask;
import sample.com.utils.ThreadConinfguration.ThreadConfiguration;
import sample.com.constants.Entity;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 解析脚本、调用动作的主类
 */
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
                case RobotConstants.DELETE:
                    result = true;
                    break;
                case RobotConstants.SEARCH:
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
    public static List<Entity> readScriptForList(String filePath) throws SubException, IOException {
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

        try {
            String lineText = null;
            //操作名称
            List<Entity> resultList = new ArrayList<Entity>();
            int currentLine = 1;
            String logicFlag = null;
            while((lineText = br.readLine()) != null){
                //判断是否为空行，空行跳过
                if(StringUtils.isEmpty(lineText.trim()))
                    continue;
                //判断是否是注释行,注释行跳过
                lineText = getNonAnnotated(lineText);
                //判断是否为空行，空行跳过
                if(StringUtils.isEmpty(lineText))
                    continue;

                //TODO 首先判断当前行是什么函数
                if(isLogicalJudgment(lineText , currentLine ,logicFlag)){
                    //逻辑语句封装到list中
                    logicFlag = LogicUtil.getLogicStr(lineText);
                    logicalPackage(lineText , resultList , currentLine ,logicFlag);
                    currentLine++;
                }



                //如果当前行为逻辑控制语句，则进入
                /*if(isLogicalJudgment(lineText , currentLine ,logicFlag)){
                    //逻辑语句封装到list中
                    logicFlag = LogicUtil.getLogicStr(lineText);
                    logicalPackage(lineText , resultList , currentLine ,logicFlag);
                    currentLine++;
                }else{
                    //检查脚本每行是否是以分号结尾
                    if(!lineText.endsWith(";")){
                        LoggerUtils.error(FileUtil.class , "当前行：" + currentLine + "，" + ExceptionConstants.LAST_CHART_SEMICOLON);
                        throw new SubException("当前行：" + currentLine + "，" + ExceptionConstants.LAST_CHART_SEMICOLON);
                    }
                    currentLine = getCurrentLine(lineText, resultList, currentLine , logicFlag);
                }*/
            }
        } finally {
            isr.close();
            br.close();
            fis.close();
        }

        return resultList;
    }

    /**
     * 注释判断
     * @param lineText
     * @return
     */
    private static String getNonAnnotated(String lineText) {
        //两条斜杠代表注释，拿到行数据后首先将斜杠后的字符全部清除
        if(lineText.contains("//")){
            if(lineText.indexOf("//") == 0)
                return null;
            else
                lineText = lineText.substring(0, lineText.indexOf("//"));
        }
        return lineText;
    }

    /**
     * 获取函数处于的行
     * 并将拆解开后的函数存入list中
     * @param lineText      脚本文件中当前行书
     * @param resultList    存储脚本结构的list
     * @param currentLine   当前行
     * @param logicFlag     逻辑标签，标签值为if或者elif,用于标记当前行的函数是否属于逻辑判断体
     * @return
     * @throws SubException
     */
    private static int getCurrentLine(String lineText, List<Entity> resultList, int currentLine ,String logicFlag)  {
        String handleName;
        int strCount;
        if (StringUtils.isNotEmpty(lineText.trim())){
            //创建结果map，最终结果处理为Map<String , Object[]>
            Map result = Maps.newHashMap();
            //兼容支持在同一行内编辑多个函数，以分号分割
            strCount = getFunctionCount(lineText, ";");
            if(strCount >= 1){
                String [] textArr = lineText.split(";");
                //循环取出函数并放入resultList里
                for (String text : textArr) {
                    int index = text.indexOf(RobotConstants.PARAM_START_TAG);
                    handleName = text.substring(0 , index);
                    if(StringUtils.isNotEmpty(logicFlag))
                        //如果逻辑标记不为空，则在函数前加入其逻辑标签
                        result.put( logicFlag + "-" +handleName.trim() , StringUtils.getParamValue(text , RobotConstants.PARAM_START_TAG , RobotConstants.PARAM_END_TAG));
                    else
                        //逻辑标签为空则略过
                        result.put(handleName , StringUtils.getParamValue(text , RobotConstants.PARAM_START_TAG , RobotConstants.PARAM_END_TAG));
                    resultList.add(result);
                    currentLine++;
                }
            }

        }
        return currentLine;
    }

    /**
     * 获取函数处于的行
     * 并将拆解开后的函数存入list中
     * @param lineText      脚本文件中当前行书
     * @return
     * @throws SubException
     */
    private static Map<String ,String > getHandle(String lineText) throws SubException {
        String handleName;
        int strCount;
        if (StringUtils.isNotEmpty(lineText.trim())){
            //创建结果map，最终结果处理为Map<String , Object[]>
            Map result = Maps.newHashMap();
            //兼容支持在同一行内编辑多个函数，以分号分割
                //循环取出函数并放入resultList里
            int index = lineText.indexOf(RobotConstants.PARAM_START_TAG);
            handleName = lineText.substring(0 , index);
            result.put(handleName , StringUtils.getParamValue(lineText , RobotConstants.PARAM_START_TAG , RobotConstants.PARAM_END_TAG));
            return result;
        }
        throw new SubException("判断体中的函数格式有误，请检查是否符合规范");
    }

    /**
     * 兼容支持在同一行内编辑多个函数，以分号分割
     */
    public static int getFunctionCount(String mainStr,String subStr){
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



    /**
     * 将if函数封装
     * @param lineText
     */
    private static void logicalPackage(String lineText , List<Entity> upperLayerList , Integer currentLine ,String logicFlag) {
        //首先进行if语句的语法判断
        //if语句的语法为        if(表达式):
        //如果是if语句，则最后以冒号结尾
        Map result = Maps.newHashMap();
        if (lineText.endsWith(":")) {
            //取出括号中的表达式
            String expression = StringUtils.getParamValue(lineText, RobotConstants.PARAM_START_TAG, RobotConstants.PARAM_END_TAG);
            // TODO 判断if中是正常表达式，还是自定义的函数
            /*
                得到的表达式中是否包含括号，如果有括号，则取出括号前的字符
                例如methodA();  则取出methodA判断是否是系统中定义的方法
            */
            result.put(logicFlag.trim(), expression);
            upperLayerList.add(result);
        }else{
            result.put(lineText.trim(), null);
            upperLayerList.add(result);
        }
    }

    /**
     *
     * @param dataMap
     * @param scriptList    脚本集,脚本的所有数据都在list中，list中的map为已被分解的方法名和参数
     */
    public static void action(Map<String, String> dataMap, List<Entity> scriptList) throws SubException {
        String logicFlag = null;
        //因为else没有判断体，用来单独判断是否进入else
        boolean elseFleg = true;
        for (int i = 0; i < scriptList.size(); i++) {
            Entity handleMap = scriptList.get(i);
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

            //拿出和方法名对应的参数个数及参数每个参数的类型.class
            String handleName = handleMap.getHandleName();
            String values = handleMap.getHandleName();
            System.out.println("读取动作 ：" + handleName);
            //如果
            if()
            //如果当前逻辑包装体不为空，则只进入对应的逻辑分支
            if(StringUtils.isNotEmpty(currentLogicBody)){
                elseFleg = false;
                //判断函数是否包含逻辑包装体，包含说明是子逻辑块，进入执行，否则跳过
                if(handleName.contains(currentLogicBody)){
                    //将包装体替换
                    handleName = handleName.replace(currentLogicBody , "");
                }else{
                    logicFlag = null;
                }
            }else {
                //如果逻辑包装体为空，则判断即将执行的函数是否包含包装体，如包含则略过，因为上一层的逻辑判断结果为false
                if(handleName.contains(RobotConstants.IF_TAG_HANDLE)
                        || handleName.contains(RobotConstants.ELIF_TAG_HANDLE)
                        || handleName.contains(RobotConstants.ELSE_TAG_HANDLE))
                    continue;
            }
            logicFlag = getLogicFlagAndInvokeHandleMethod(dataMap, i, logicFlag, handleName, values , elseFleg);

        }
    }

    /**
     * 根据逻辑标签，选择执行的方法,并返回接下来需要执行的逻辑
     * @param dataMap       excel中读取的数据
     * @param scriptLine    脚本当前行
     * @param logicFlag     逻辑标签
     * @param handleName    操作名称
     * @param parameter        参数
     * @return
     * @throws SubException
     */
    private static String getLogicFlagAndInvokeHandleMethod(Map<String, String> dataMap, int scriptLine, String logicFlag, String handleName, String parameter ,boolean elseFleg ) throws SubException {
        //如果读取的动作为if/elif则进行一系列处理，然后再放入下一个逻辑
        if(handleName.equals(RobotConstants.IF_TAG) || handleName.equals(RobotConstants.ELIF_TAG)) {
            /*
                得到的表达式中是否包含括号，如果有括号，则取出括号前的字符
                例如methodA();  则取出methodA判断是否是系统中定义的方法
            */
            if (parameter.contains(RobotConstants.PARAM_START_TAG)) {
                String tempHandleName = parameter.substring(0, parameter.indexOf(RobotConstants.PARAM_START_TAG));
                //methodA是否是方法，不是则用正常判断表达式的值，如是，则调用方法，返回调用结果值
                if (parserHandle(tempHandleName)) {
                    Map<String ,String > subHandleMap = getHandle(parameter);
                    for ( String subHandleName : subHandleMap.keySet()){
                        //获取逻辑体中的表达式结果
                        boolean invokResult = invokMethod(subHandleMap.get(subHandleName) , subHandleName , dataMap, scriptLine);
                        //判断逻辑体中的表达式结果
                        if (invokResult){
                            logicFlag = handleName;
                        }
                    }
                } else {
                    throw new SubException("未找到对应的函数，请仔细检查，错误的函数名为：" + tempHandleName);
                }
            } else{
                //当if中为寻常表达式时，使用正常的方法获取表达式对比后的布尔值
                boolean result = ExpressionChanged.isEnable(parameter, null, null);
                if(result) {
                    logicFlag = handleName;
                }
            }
        } else if(handleName.equals(RobotConstants.ELSE_TAG) ){
            //当elseFleg为false时，说明已经有函数进入过if/elif了，此时则不会进入else
            if(elseFleg)
                if(StringUtils.isEmpty(logicFlag)){
                    logicFlag = handleName;
                }else{
                    logicFlag = null;
                }
        } else {
            invokMethod(parameter, handleName, dataMap, scriptLine);
        }
        return logicFlag;
    }

    /**
     * 调用方法
     * @param params           脚本行方法参数
     * @param handleName       脚本函数名称
     * @param dataMap          excel数据集
     * @param lineCount        当前行
     * @return
     * @throws SubException
     */
    private static boolean invokMethod(String params , String handleName , Map<String, String> dataMap , int lineCount) throws SubException {
        ReflectUtil.notNull(handleName, "handleName must not be null");
        boolean isFlip = false;
        if(handleName.contains("!")){
            handleName = handleName.replace("!" , "");
            isFlip = true;
        }
        //根据handleName进行判断动作是否为定义动作，如果不是则抛出异常
        //不用try catch捕捉异常，百度搜索更优雅的方法
        boolean isHandleName = ParserMain.parserHandle(handleName);
//                Class [] clazzParams = null;
        String [] paramArr = null;
        if(StringUtils.isNotEmpty(params)){
            paramArr = params.split(RobotConstants.SPILIT_SYMBOL);
//                    clazzParams = new Class [paramArr.length];
            for (int j = 0 ; j < paramArr.length ; j++ ){
//                        clazzParams[j] = paramArr[j].getClass();
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
                if(isFlip){
                    System.out.println("调用方法成功返回---"+ !(boolean) f.get());
                    return !(boolean) f.get();
                }else{
                    System.out.println("调用方法成功返回---"+ (boolean) f.get());
                    return (boolean) f.get();
                }
            } catch (InterruptedException e) {
                String error = "第" + lineCount + "个函数出错，方法名为" + handleName + "，参数为" + params + "，请参照脚本手册检查。\n";
                LoggerUtils.error(ParserMain.class ,error , e);
                throw new SubException(error , e);
            } catch (ExecutionException e) {
                String error = "程序出错，请联系开发人员进行处理。\n";
                LoggerUtils.error(ParserMain.class ,error , e);
                throw new SubException(error , e);
            }
        }
        LoggerUtils.info(ParserMain.class , "未找到方法:" + handleName);
        return RobotConstants.FALSE;
    }

    public static void main(String[] args) throws AWTException, IOException, SubException {
        List<Entity> list = readScriptForList("G:\\逻辑脚本.txt");
        /*for ( Map<String ,String > map : list) {
            for (String a : map.keySet()) {
                System.out.println(a + " -------------- " + map.get(a));
            }

        }*/
        
    }
}
