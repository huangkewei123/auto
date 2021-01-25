package sample.com.main.controller;

import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import sample.com.main.controller.proxy.ProxyFactory;
import sample.com.main.controller.service.HandleService;
import sample.com.utils.LoggerUtils;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * 列举所有的操作方法，只用于和脚本文件做匹配
 * 真正的程序操作在service
 */
public class HandleController {

    private HandleService proxy = null;

    /**
     * 构造函数里，设置动态的service代理
     */
    public HandleController(){
        try {
            HandleService handle = HandleService.getSingleton();
            //代理对象
            proxy = (HandleService)new ProxyFactory(handle).getProxyInstance();
        } catch (AWTException e) {
            LoggerUtils.error(HandleController.class , e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 鼠标定位
     * @param text
     * @return
     */
    public boolean mouseLocation(String text  ,String highDefinition){
        boolean location = false;
        try {
            location = proxy.mouseLocation(text , highDefinition);
        } catch (SubException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * 截图、鼠标定位、点击三合一操作
     * @param text
     * @return
     */
    public boolean mouseLocAndClick(String text ,String highDefinition) throws SubException, AWTException {
        proxy.cut();
        //定位传入文字的位置
        proxy.mouseLocation(text ,highDefinition);
        //单击鼠标
        proxy.mouseClick(1);
        return RobotConstants.TRUE;
    }

    /**
     * 鼠标定位偏移
     * @param text          文字
     * @param offset        偏移量
     * @param direction     方向----->上、下、左、右
     * @return
     */
    public boolean mouseOffset(String text , String offset , String direction ,String highDefinition) throws SubException, AWTException {
        proxy.mouseOffset(text , Integer.parseInt(offset) , direction , highDefinition);
        return RobotConstants.TRUE;
    }

    /**
     * 在当前的鼠标位置上做偏移
     * @param offset
     * @param direction
     * @return
     */
    public boolean localMouseOffset( String offset , String direction ) {
        proxy.mouseOffset( Integer.parseInt(offset) , direction);
        return RobotConstants.TRUE;
    }

    /**
     * 鼠标滚轮转动
     * @param wheelAmt 数字格式，向上滚动为负数，向下滚动为正数。数字代表滚轮滚动的凹槽个数
     * @return
     */
    public boolean mouseWheel(String wheelAmt) throws SubException {
        return proxy.mouseWheel(wheelAmt);
    }

    /**
     * 鼠标点击
     * @param hitCount  点击次数
     * @return
     */
    public boolean mouseClick(String hitCount) throws SubException {
        proxy.mouseClick(Integer.parseInt(hitCount));
        return RobotConstants.TRUE;
    }


    /**
     * 锁定鼠标
     * @return
     */
    public boolean mouseHold() {
        proxy.mousePress();
        return RobotConstants.TRUE;
    }

    /**
     * 释放鼠标
     * @return
     */
    public boolean mouseFree() {
        proxy.mouseRelease();
        return RobotConstants.TRUE;
    }

    /**
     * 输入文字
     * @param text
     * @return
     */
    public boolean inputText(String text) {
        proxy.copyText(text);
        return RobotConstants.TRUE;
    }

    /**
     * 全屏截图
     * @return
     */
    public boolean cut() throws SubException {
        proxy.cut();
        return RobotConstants.TRUE;
    }

    /**
     * 局部截图，结束点的坐标值
     * @param x         横坐标
     * @param y         纵坐标
     * @param width     宽
     * @param height    高
     * @return
     */
    public boolean cutPart(String x , String y , String width , String height) throws SubException {
        proxy.cut(Integer.parseInt(x) , Integer.parseInt(y) ,Double.parseDouble(width) , Double.parseDouble(height));
        return RobotConstants.TRUE;
    }

    /**
     * 等待
     * 不得超过10分钟，1秒 = 1*1000
     * @param second   需要延长的时长（秒）
     * @return
     */
    public boolean wait(String second) throws SubException {
        proxy.delay(Integer.parseInt(second));
        return RobotConstants.TRUE;
    }

    /**
     * 每隔一段时间则截图、判断
     * @param text      需要判断的文字
     * @param second    每此截图之间间隔时长（秒）
     * @return
     */
    public boolean eachCut(String text , String second,String highDefinition) throws SubException, AWTException {
        return proxy.eachCut(text , Integer.parseInt(second) , highDefinition);
    }

    /**
     * 停止
     * @return
     */
    public boolean stop()  {
        return proxy.stop();
    }

    /**
     * 暂停
     * @return
     */
    public boolean pause() {
        proxy.pause();
        return RobotConstants.TRUE;
    }

    /**
     * 选择小程序中的表盘日期
     * @param date      表盘最终选定的日期
     * @return
     */
    public boolean selectDate(String date) throws SubException, AWTException {
        return proxy.selectDate(date , null);
    }

    /**
     * 全选快捷键
     * @return
     */
    public boolean selectAll() {
        return proxy.keyPressWithCtrl(KeyEvent.VK_A);
    }

    public static void main(String[] args) throws SubException, InterruptedException {
        HandleController h = new HandleController();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        h.selectAll();
//        h.cut("754" , "154" , "414" , "736");
//        h.cutPart("0" , "0" , "414" , "736");
//        h.mouseLocation("确定");
//        h.mouseClick("1");
//        h.wait("5");
//        h.cut();
//        h.mouseLocation("审判立案");
//        h.mouseClick("1");
//        h.mouseLocation("确定");
//        h.mouseClick("1");
//        h.wait("5");
//        h.cut();

//        h.localMouseOffset("10","上");

    }
}
