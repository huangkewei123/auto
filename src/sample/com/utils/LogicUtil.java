package sample.com.utils;

import sample.com.constants.RobotConstants;

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

    public static void main(String[] args) {
        getLogicStr("input(阿斯蒂芬)");
        getLogicStr("if (阿斯蒂芬):");
        getLogicStr("elif( 阿斯蒂芬):");
        getLogicStr("while ( 阿斯蒂芬):");
        getLogicStr("endif( 阿斯蒂芬):");
    }
}
