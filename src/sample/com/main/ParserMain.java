package sample.com.main;


import com.google.common.collect.Maps;
import com.google.gson.Gson;
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
                case RobotConstants.GOTO:
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
        RobotConstants.resultList.clear();
        try {
            String lineText = null;     //读出来的脚本行
            Integer currentLine = 1;    //当前行
            String logicFlag = null;    //逻辑标签
            Integer pLine = 0;          //语句的父层级行号
            Integer level = 1;          //语句所属层级
            Map<Integer , Integer > level_pid = Maps.newHashMap();
            level_pid.put(1 , 0);
            while((lineText = br.readLine()) != null){
                //判断是否是注释行,注释行跳过
                lineText = getNonAnnotated(lineText);

                lineText = lineText.trim();
                Entity en = new Entity();
                en.setType(lineText);
                en.setLevel(level);
                en.setPline(pLine);
                en.setLine(currentLine);

                //判断是否为空行，空行跳过
                if(lineText.equals(RobotConstants.COMMENT) || lineText.equals(RobotConstants.BLANK_LINE)){
                    currentLine++;
                    RobotConstants.resultList.add(en);
                    continue;
                }
                logicFlag = LogicUtil.getLogicType(lineText);
                en.setType(logicFlag);

                //逻辑语句封装到list中
                if(logicFlag.equals(RobotConstants.IF_TAG) || logicFlag.equals(RobotConstants.WHILE_TAG)){
                    en.setAttribution(logicFlag);
                    en.setHaveSub(RobotConstants.TRUE);
                    en.setHandleName(StringUtils.subStartTagBefore(lineText, RobotConstants.PARAM_START_TAG).trim());
                    en.setParameter(StringUtils.getParamValue(lineText , RobotConstants.PARAM_START_TAG , RobotConstants.PARAM_END_TAG).trim());

                    RobotConstants.resultList.add(en);
                    pLine = currentLine;
                    level = level + 1;
                    level_pid.put(level , pLine);
                } else if(logicFlag.equals(RobotConstants.ENDIF_TAG) || logicFlag.equals(RobotConstants.END_WHILE_TAG)){
                    pLine = level_pid.get(level);
                    en.setPline(pLine);
                    level = level - 1;
                    en.setLevel(level);
                    level_pid.put(level , pLine);
                    RobotConstants.resultList.add(en);
                } else if(logicFlag.equals(RobotConstants.ELSE_TAG)){
                    pLine = level_pid.get(level);
                    en.setPline(pLine);
                    en.setLevel(level);
                    en.setHandleName(logicFlag);
                    RobotConstants.resultList.add(en);
                }else if(logicFlag.equals(RobotConstants.ELIF_TAG)){
                    en.setHaveSub(RobotConstants.TRUE);
                    pLine = level_pid.get(level);
                    en.setPline(pLine);
                    en.setHandleName(StringUtils.subStartTagBefore(lineText, RobotConstants.PARAM_START_TAG).trim());
                    en.setParameter(StringUtils.getParamValue(lineText , RobotConstants.PARAM_START_TAG , RobotConstants.PARAM_END_TAG).trim());
                    RobotConstants.resultList.add(en);
                    pLine = currentLine;
                    level = level + 1;
                    level_pid.put(level , pLine);
                }else{
                    en.setHaveSub(RobotConstants.FALSE);
                    pLine = level_pid.get(level);
                    en.setPline(pLine);
                    en.setHandleName(StringUtils.subStartTagBefore(lineText, RobotConstants.PARAM_START_TAG).trim());
                    en.setParameter(StringUtils.getParamValue(lineText , RobotConstants.PARAM_START_TAG , RobotConstants.PARAM_END_TAG).trim());
                    RobotConstants.resultList.add(en);
                }
                currentLine++;
            }
            for (Entity e : RobotConstants.resultList ) {
                System.out.println(e.toString());
            }
        } finally {
            isr.close();
            br.close();
            fis.close();
        }

        return RobotConstants.resultList;
    }

    /*private static List<Entity> getScriptList(String lineText, List<Entity> resultList, Integer currentLine, Integer level ,String logicFlag ,Integer pLine) {
        Entity en = new Entity();
        //TODO 首先判断当前行是什么函数
        logicFlag = LogicUtil.getLogicType(lineText);
        en.setType(logicFlag);
        en.setLevel(level);
        en.setPline(pLine);
        en.setLine(currentLine);

        level = level++;
        if(LogicUtil.getLogic(lineText)){
            //逻辑语句封装到list中
            if(logicFlag.equals(RobotConstants.IF_TAG) || logicFlag.equals(RobotConstants.WHILE_TAG)){
                List<Entity> subList = new ArrayList<>();
                en.setAttribution(logicFlag);
                en.setHaveSub(RobotConstants.TRUE);
                subList.add(en);
                getScriptList(lineText , subList , currentLine + 1 ,level , logicFlag ,currentLine);
            } else if(logicFlag.equals(RobotConstants.END_WHILE_TAG) || logicFlag.equals(RobotConstants.END_WHILE_TAG)){
                en.setHaveSub(RobotConstants.FALSE);
                return resultList;
            } else {

            }

            logicalPackage(lineText, resultList, currentLine,logicFlag);
            currentLine++;
        }
        return resultList;
    }*/

    /**
     * 注释/空行判断,如果是注释或者空行，则返回对应的类型
     * @param lineText
     * @return
     */
    private static String getNonAnnotated(String lineText) {
        if(StringUtils.isEmpty(lineText.trim())){
            return RobotConstants.BLANK_LINE;
        }
        //两条斜杠代表注释，拿到行数据后首先将斜杠后的字符全部清除
        if(lineText.contains("//")){
            if(lineText.indexOf("//") == 0)
                return RobotConstants.COMMENT;
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
    /*private static int getCurrentLine(String lineText, List<Entity> resultList, int currentLine ,String logicFlag)  {
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
    }*/

    /**
     * 获取函数处于的行
     * 并将拆解开后的函数存入list中
     * @param lineText      脚本文件中当前行书
     * @return
     * @throws SubException
     */
    private static Map<String ,String > getHandle(String lineText) throws SubException {
        String handleName;
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
    /*private static void logicalPackage(String lineText , List<Entity> upperLayerList , Integer currentLine ,String logicFlag) {
        //首先进行if语句的语法判断
        //if语句的语法为        if(表达式):
        //如果是if语句，则最后以冒号结尾
        Map result = Maps.newHashMap();
        if (lineText.endsWith(":")) {
            //取出括号中的表达式
            String expression = StringUtils.getParamValue(lineText, RobotConstants.PARAM_START_TAG, RobotConstants.PARAM_END_TAG);
            // TODO 判断if中是正常表达式，还是自定义的函数
            *//*
                得到的表达式中是否包含括号，如果有括号，则取出括号前的字符
                例如methodA();  则取出methodA判断是否是系统中定义的方法
            *//*
            result.put(logicFlag.trim(), expression);
            upperLayerList.add(result);
        }else{
            result.put(lineText.trim(), null);
            upperLayerList.add(result);
        }
    }*/

    /**
     * 脚本执行方法
     * @param dataMap
     * @param scriptList    脚本集,脚本的所有数据都在list中，list中的map为已被分解的方法名和参数
     */
    public static void action(Map<String, String> dataMap, List<Entity> scriptList) throws SubException {
        String logicFlag = null;
        //因为else没有判断体，用来单独判断是否进入else
        boolean elseFleg = true;
        //行数
        int line = 0;
        Map<Integer ,Boolean> flagMap = Maps.newHashMap();
        while (line < scriptList.size()) {
            Entity handleEntity = scriptList.get(line);
            String type = handleEntity.getType();
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

            //跳过空行和注释行
            if(type.equals(RobotConstants.BLANK_LINE)
                    || type.equals(RobotConstants.COMMENT)){
                line++;
                continue;
            }

            //如果类型为nomal，并且方法名为gotoLine则取出相关参数，跳转到相应行执行
            String handleName = handleEntity.getHandleName();
            if(type.equals(RobotConstants.NORMAL_TAG) && handleName.equals(RobotConstants.GOTO)){
                try {
                    line = Integer.parseInt(handleEntity.getParameter()) - 1;
                    continue;
                } catch (NumberFormatException e) {
                    LoggerUtils.error(ParserMain.class, "gotoLine方法的参数有误，参数只允许为数字", e);
                    throw new SubException("gotoLine方法的参数有误，参数只允许为数字");
                }
            }
            //拿出和方法名对应的参数个数及参数每个参数的类型.class
            System.out.println("读取动作 ：" + handleName);
            flagMap = getLogicFlagAndInvokeHandleMethod(flagMap , dataMap, handleEntity);
            line++;
            //判断当前属于什么函数
//            LogicUtil.getLogicStr(handleName);
//            if()
            //如果当前逻辑包装体不为空，则只进入对应的逻辑分支
            /*if(StringUtils.isNotEmpty(currentLogicBody)){
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
            }*/
        }
    }

    /**
     * 根据逻辑标签，选择执行的方法,并返回接下来需要执行的逻辑
     * @param dataMap       excel中读取的数据
     * @param handleEntity    脚本当前行
//     * @param logicFlag     逻辑标签
//     * @param handleName    操作名称
//     * @param parameter        参数
     * @return
     * @throws SubException
     */
    private static Map<Integer , Boolean > getLogicFlagAndInvokeHandleMethod(Map<Integer , Boolean > flagMap , Map<String, String> dataMap, Entity handleEntity ) throws SubException {
        String handleName = handleEntity.getHandleName();
        String parameter = handleEntity.getParameter();
        String type = handleEntity.getType();
        boolean logicFlag = false;
        Integer pline = 0;
        //如果读取的动作为if/elif则进行一系列处理，然后再放入下一个逻辑
        if(type.equals(RobotConstants.IF_TAG) || type.equals(RobotConstants.ELIF_TAG) || type.equals(RobotConstants.WHILE_TAG)) {
            //当type为elif的时候，查看是否进入过之前的判断体，如果进入过，那么flagMap中会有相应的值
            if(type.equals(RobotConstants.ELIF_TAG)){
                Boolean isContinue = flagMap.get(handleEntity.getPline());
                if(isContinue)
                    return flagMap;
            }
            /*得到的表达式中是否包含括号，如果有括号，则取出括号前的字符
            例如methodA();  则取出methodA判断是否是系统中定义的方法*/
            if (parameter.contains(RobotConstants.PARAM_START_TAG)) {
                String tempHandleName = parameter.substring(0, parameter.indexOf(RobotConstants.PARAM_START_TAG));
                //methodA是否是方法，不是则用正常判断表达式的值，如是，则调用方法，返回调用结果值
                if (parserHandle(tempHandleName)) {
                    Map<String ,String > subHandleMap = getHandle(parameter);
                    for ( String subHandleName : subHandleMap.keySet()){
                        //获取逻辑体中的表达式结果
                        boolean invokResult = invokMethod(subHandleName , subHandleMap.get(subHandleName) , handleEntity.getLine() , dataMap);
                        //判断逻辑体中的表达式结果
                        if (invokResult){
                            if(type.equals(RobotConstants.IF_TAG) || type.equals(RobotConstants.WHILE_TAG))
                                flagMap.put(handleEntity.getLine() , true);
                            else
                                flagMap.put(handleEntity.getPline() , true);
                        }
                    }
                } else {
                    throw new SubException("未找到对应的函数，请仔细检查，错误的函数名为：" + tempHandleName);
                }
            } else{
                //当if中为寻常表达式时，使用正常的方法获取表达式对比后的布尔值
                boolean result = ExpressionChanged.isEnable(parameter, null, null);
                if(result) {
                    if(type.equals(RobotConstants.IF_TAG) || type.equals(RobotConstants.WHILE_TAG))
                        flagMap.put(handleEntity.getLine() , true);
                    else
                        flagMap.put(handleEntity.getPline() , true);
                }
            }
        } else if(type.equals(RobotConstants.ELSE_TAG) ){
            //当elseFleg为false时，说明已经有函数进入过if/elif了，此时则不会进入else
            Boolean isContinue = flagMap.get(handleEntity.getPline());
            if(isContinue)
                return flagMap;
        } else if(type.equals(RobotConstants.NORMAL_TAG)) {
            invokMethod(handleEntity.getHandleName() , handleEntity.getParameter(), handleEntity.getLine(), dataMap);
        }
        return flagMap;
    }

    /**
     * 调用方法
     * @param dataMap          excel数据集
     * @return
     * @throws SubException
     */
    private static boolean invokMethod(String handleName ,String params , Integer line,Map<String, String> dataMap) throws SubException {
        ReflectUtil.notNull(handleName, "handleName must not be null");
        //是否翻转判断结果
        boolean isFlip = false;
        if(params.contains("!")){
            params = params.replace("!" , "");
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
                String error = "第" + line + "个函数出错，方法名为" + handleName + "，参数为" + params + "，请参照脚本手册检查。\n";
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
