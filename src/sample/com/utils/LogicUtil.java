package sample.com.utils;

import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;

public class LogicUtil {
    /**
     * 获取当前行的脚本函数类型
     * @param lineText
     * @return
     */
    /*public static String getType(String lineText){
        switch (){

        }
    }*/

    /**
     * 是否是逻辑语句
     * @param lineText
     * @return
     */
    public static String getLogicStr(String lineText) {
        //如果包含括号，则进入以下逻辑
        if(lineText.trim().equals(RobotConstants.ENDIF_TAG))
            return RobotConstants.ENDIF_TAG;
        if(lineText.trim().equals(RobotConstants.ELSE_TAG))
            return RobotConstants.ELSE_TAG;
        if(lineText.trim().equals(RobotConstants.END_WHILE_TAG))
            return RobotConstants.END_WHILE_TAG;
        String str = StringUtils.subStartTagBefore(lineText, RobotConstants.PARAM_START_TAG).trim();
        //如果在(之前是if，则认定此语句为if判断语句
        if(str.equals(RobotConstants.IF_TAG)){
            return RobotConstants.IF_TAG;
        }else if(str.equals(RobotConstants.ELIF_TAG)){//将括号中的判断值取出转化为java中的判断语句
            return RobotConstants.ELIF_TAG;
        }else if(str.equals(RobotConstants.WHILE_TAG)){//将括号中的判断值取出转化为java中的判断语句
            return RobotConstants.WHILE_TAG;
        }else
            return RobotConstants.NORMAL_TAG;
    }

    /**
     * 脚本中的函数判断,是属于什么类型
     * @param lineText      脚本行数据
     * @param currentLine   当前行数
     * @return
     */
    public static boolean isLogicalJudgment(String lineText,Integer currentLine ,String logicFlag) throws SubException {
        //如果不包含括号，则进入以下逻辑
        //因为endif/edl/else不包含括号，且一行中只可以有三个中的一个
        if(!lineText.contains(RobotConstants.PARAM_START_TAG)){
            //如果不等于endif/edl/else，这三个，那就报错，因为不符合定义的语法
            if(!lineText.trim().equals(RobotConstants.ENDIF_TAG)
                    && !lineText.trim().equals(RobotConstants.ELSE_TAG)
                    && !lineText.trim().equals(RobotConstants.END_WHILE_TAG)){
                throw new SubException("请检查当前行是否错误，除endif/else/edl外，其他方法都需加上()，错误行为：" + currentLine);
            }else {
                return true;
            }
        }else if(lineText.contains(RobotConstants.PARAM_START_TAG) && StringUtils.isNotEmpty(logicFlag) && (logicFlag.equals(RobotConstants.IF_TAG) || logicFlag.equals(RobotConstants.ELIF_TAG) || logicFlag.equals(RobotConstants.WHILE_TAG))){
            //判断字符串是否是if/elif/while
            //如果if标签为空，代表当前行脚本是普通函数
            if(!LogicUtil.getLogicStr(lineText).equals(RobotConstants.NORMAL_TAG)){
                return true;
            }else {
                return false;
            }
        }else {
            if(RobotConstants.NORMAL_TAG.equals(logicFlag))
                //如果if标签为空，代表当前行脚本是普通函数
                if(StringUtils.isNotEmpty(LogicUtil.getLogicStr(lineText))){
                    return true;
                }else {
                    return false;
                }
            else
                //检查逻辑体是否符合格式，正确格式必须带有缩进
                if(!lineText.contains("\t")) {
                    throw new SubException("请检查当前行是否符合逻辑体规范，逻辑体必须包含缩进，当前行为：第" + currentLine + "行");
                }
        }
        return false;
    }

    public static void main(String[] args) {
        getLogicStr("input(阿斯蒂芬)");
        getLogicStr("if (阿斯蒂芬):");
        getLogicStr("elif( 阿斯蒂芬):");
        getLogicStr("while ( 阿斯蒂芬):");
        getLogicStr("endif( 阿斯蒂芬):");
    }
}
