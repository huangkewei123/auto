package sample.com.main.controller.service;

import sample.com.constants.ExceptionConstants;
import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;
import sample.com.main.controller.HandleController;
import sample.com.main.tessract.BaiduGeneral;
import sample.com.main.tessract.XiaoLiTessract;
import sample.com.utils.LoggerUtils;
import sample.com.utils.ReflectUtil;
import sample.com.utils.StringUtils;
import sample.com.utils.ThreadConinfguration.ThreadConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class HandleService {

    private volatile static Robot r;
    private volatile static HandleService handle;

    public HandleService () throws AWTException {
        HandleService.getRobot();
    }

    /**
     * 操作类单例
     * @return
     * @throws AWTException
     */
    public static HandleService getSingleton() throws AWTException {
        if (handle == null) {
            synchronized (HandleService.class) {
                if (handle == null) {
                    handle = new HandleService();
                }
            }
        }
        HandleService.getRobot();
        return handle;
    }

    /**
     * 机器人单例
     * @return
     * @throws AWTException
     */
    public static Robot getRobot() throws AWTException {
        if (r == null) {
            synchronized (Robot.class) {
                if (r == null) {
                    r = new Robot();
                }
            }
        }
        return r;
    }

    // shift+ 按键
    public boolean keyPressWithShift(Integer key) {
        r.keyPress(KeyEvent.VK_SHIFT);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_SHIFT);
        r.delay(100);
        return RobotConstants.TRUE;
    }

    // ctrl+ 按键
    public boolean keyPressWithCtrl(Integer key) {
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(key);
        r.delay(100);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(key);
        return RobotConstants.TRUE;
    }

    // alt+ 按键
    public boolean keyPressWithAlt(Integer key) {
        r.keyPress(KeyEvent.VK_ALT);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_ALT);
        return RobotConstants.TRUE;
    }

    // windows+ 按键
    public boolean keyPressWithWin(Integer key) {
        r.keyPress(KeyEvent.VK_WINDOWS);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_WINDOWS);
        return RobotConstants.TRUE;
    }

    //打印出字符串
    public boolean keyPressString(String str){
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();//获取剪切板
        Transferable tText = new StringSelection(str);
        clip.setContents(tText, null); //设置剪切板内容
        this.keyPressWithCtrl(KeyEvent.VK_V);//粘贴
        return RobotConstants.TRUE;
    }

    //打印出字符串
    public boolean enter(){
        r.keyPress(KeyEvent.VK_ENTER);//粘贴
        return RobotConstants.TRUE;
    }



    /**
     * 识别中文，并返回特定文字的坐标位置
     * @param imgPath
     * @param text
     * @param highDefinition 是否高清识别
     */
    private static Map<String ,Integer> imgOcr(String imgPath , String text , String highDefinition) throws AWTException, SubException {
        Map result = null;
        if(RobotConstants.OCR_SWITCH == 1){
            result = BaiduGeneral.general(imgPath,text,highDefinition  );
        }else{
            result = XiaoLiTessract.general(imgPath,text );
        }

        if(result.isEmpty()){
            for (int i = 0 ; i < 5 ; i++){
                if(result.isEmpty()) {
                    HandleService.getSingleton().cut();
                    if(RobotConstants.OCR_SWITCH == 1){
                        result = BaiduGeneral.general(imgPath,text,highDefinition  );
                    }else{
                        result = XiaoLiTessract.general(imgPath,text );
                    }
                    if( i == 4 ) {
                        LoggerUtils.info(HandleService.class , "自动循环识别文字5次，仍然无法识别，自动退出，识别文字为：" + text);
                        //System.exit(0);
                        RobotConstants.OPERATING_VAR = "stop";
                        break;
                    }
                }else {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 识别中文，并返回特定文字的坐标位置
     * @param imgPath
     * @param highDefinition 是否高清识别
     */
    private static ArrayList<Map> imgOcrGetResultStr(String imgPath , String highDefinition) throws AWTException, SubException {
        ArrayList resultList = null;
        if(RobotConstants.OCR_SWITCH == 1){
            resultList = BaiduGeneral.imgOcrGetResultStr(imgPath, highDefinition  );
        }else{
            resultList = XiaoLiTessract.imgOcrGetResultStr(imgPath );
        }
        if(resultList.isEmpty()){
            for (int i = 0 ; i < 5 ; i++){
                if(resultList.isEmpty()) {
                    HandleService.getSingleton().cut();
                    if(RobotConstants.OCR_SWITCH == 1){
                        resultList = BaiduGeneral.imgOcrGetResultStr(imgPath, highDefinition  );
                    }else{
                        resultList = XiaoLiTessract.imgOcrGetResultStr(imgPath );
                    }
                    if( i == 4 ) {
                        LoggerUtils.info(HandleService.class , "自动循环识别文字5次，仍然无法识别，自动退出");
//                        System.exit(0);
                        RobotConstants.OPERATING_VAR = "stop";
                        break;
                    }
                }else {
                    break;
                }
            }
        }
        return resultList;
//        return Accurate.accurate(imgPath , text);
    }

    /**
     * 移动鼠标至坐标
     * @param text
     * @param highDefinition 是否高清识别
     */
    public boolean mouseLocation(String text ,String highDefinition) throws SubException, AWTException {
        Map<String , Integer> map = this.imgOcr(RobotConstants.IMAGE_PATH,text , highDefinition);
        System.out.println("x轴：" + map.get("x"));
        System.out.println("x轴：" + map.get("y"));
        //如果没有识别到相同的内容则返回false
        if(null == map || RobotConstants.ZERO == map.size())
            return RobotConstants.FALSE;
        r.mouseMove(map.get("x") , map.get("y"));
        return RobotConstants.TRUE;
    }

    /**
     * 移动鼠标至坐标
     * @param x
     * @param y
     */
    public boolean mouseLocationXY(String x , String y){
        r.mouseMove(Integer.parseInt(x) , Integer.parseInt(y));
        return RobotConstants.TRUE;
    }
    /**
     * 移动鼠标至坐标
     * @param x
     * @param y
     */
    public boolean mouseMoveAndClick(String x , String y) throws SubException {
        r.mouseMove(Integer.parseInt(x) , Integer.parseInt(y));
        this.mouseClick(1);
        return RobotConstants.TRUE;
    }

    /**
     * 移动鼠标至坐标,但是加入了偏移量
     * 作为方法内部调用
     * @param text
     * @param highDefinition 是否高清识别
     */
    private boolean mouseLocation(String text ,Integer x ,Integer y ,String highDefinition) throws SubException, AWTException {
        Map<String , Integer> map = this.imgOcr(RobotConstants.IMAGE_PATH,text ,highDefinition);
        //如果没有识别到相同的内容则返回false
        if(null == map || RobotConstants.ZERO == map.size())
            return RobotConstants.FALSE;
        r.mouseMove(map.get("x") + x < 0 ? 0 : map.get("x") + x, map.get("y") + y < 0 ? 0 : map.get("y") + y);
        return RobotConstants.TRUE;
    }

    /**
     * 鼠标偏移
     * @param text
     * @param offset
     * @param direction
     * @param highDefinition 是否高清识别
     */
    public boolean mouseOffset(String text , Integer offset , String direction ,String highDefinition) throws AWTException, SubException {
        ReflectUtil.notNull(direction , "偏移方向不能为空");
        if(direction.equals("上")) {
            mouseLocation(text, 0, -offset , highDefinition);
        }else if(direction.equals("下")) {
            mouseLocation(text, 0, offset , highDefinition);
        }else if(direction.equals("左")) {
            mouseLocation(text, -offset , 0 ,highDefinition);
        }else if(direction.equals("右")) {
            mouseLocation(text, offset, 0 ,highDefinition);
        }
        return RobotConstants.TRUE;
    }

    /**
     * 鼠标偏移
     * @param offset
     * @param direction
     */
    public boolean mouseOffset(Integer offset , String direction){
        ReflectUtil.notNull(direction , "偏移方向不能为空");
        PointerInfo pointer = MouseInfo.getPointerInfo();
        Point point = pointer.getLocation();
        Double x = point.getX();
        Double y = point.getY();

//        System.out.println(x);
//        System.out.println(y);
        if(direction.equals("上")) {
            r.mouseMove(x.intValue(), y.intValue() - offset);
        }else if(direction.equals("下")) {
            r.mouseMove(x.intValue(), y.intValue() + offset);
        }else if(direction.equals("左")) {
            r.mouseMove( x.intValue() - offset , y.intValue());
        }else if(direction.equals("右")) {
            r.mouseMove( x.intValue() + offset, y.intValue());
        }
        return RobotConstants.TRUE;
    }


    //按下鼠标
    public boolean mousePress(){
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        return RobotConstants.TRUE;
    }

    //释放鼠标
    public boolean mouseRelease(){
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        return RobotConstants.TRUE;
    }

    private boolean mouseClick(){
        mousePress();
        mouseRelease();
        return RobotConstants.TRUE;
    }

    //按下鼠标
    public boolean mouseClick(Integer hitCount) throws SubException {
        int i = 0;
        //判断可能出现的错误
        if(null == hitCount || 0 >= hitCount ){
            LoggerUtils.error(this.getClass() , ExceptionConstants.HIT_COUNT_ZERO_EXC);
            throw new SubException(ExceptionConstants.HIT_COUNT_ZERO_EXC );
        }
        while (i < hitCount){
            mouseClick();
            i++;
        }
        return RobotConstants.TRUE;
    }

    /**
     * 输入文字
     * @param text
     * @return
     */
    public boolean copyText(String text){
        keyPressString(text); //输入字符串
        return RobotConstants.TRUE;
    }


    /**
     * 全屏截图
     * @return
     */
    public boolean cut() throws SubException {
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        Frame f = new Frame();
        Insets   screenInsets   =   Toolkit.getDefaultToolkit().getScreenInsets(f.getGraphicsConfiguration());
        cutImage(screenInsets.left ,screenInsets.top , screenSize.width   -   screenInsets.left   -   screenInsets.right , screenSize.height   -   screenInsets.top   -   screenInsets.bottom);
        return RobotConstants.TRUE;
    }

    /**
     * 局部截图,或许还需要其他的参数，比如宽、高
     * @param x
     * @param y
     * @param width
     * @param hight
     * @return
     */
    public boolean cut(Integer x, Integer y , double width , double hight) throws SubException {
        this.cutImage(x , y , width , hight);
        return RobotConstants.TRUE;
    }

    /**
     * 每隔一段时间则截图、判断
     * @param text      需要判断的文字
     * @param second    每此截图之间间隔时长（秒）
     * @param highDefinition 是否高清识别
     * @return
     */
    public boolean eachCut(String text , Integer second , String highDefinition) throws SubException, AWTException {
        //初始化变量为图片识别不成功
        boolean location = false;
        //最多只循环5次
        int loopCount = 5;
        int i = 0;
        //进入循环
        while (i < loopCount){
            //截图
            cut();
            //传入文字，进行判断、定位，返回真假值
            location = mouseLocation(text ,highDefinition);
            //成功定位，则返回真
            if(location)
                return RobotConstants.TRUE;
            else    //定位失败，则返回假，并等待一个second的时间
                delay(second);
            i++;
        }
        return RobotConstants.FALSE;
    }

    /**
     * 截图的执行方法
     * @param x
     * @param y
     * @param width
     * @param hight
     */
    private boolean cutImage(Integer x, Integer y , double width , double hight) throws SubException {
        try {
            //根据指定的区域抓取屏幕的指定区域，1300是长度，800是宽度。
            BufferedImage bi=r.createScreenCapture(new Rectangle(x,y,new Double(width).intValue() ,new Double(hight).intValue()));
            //把抓取到的内容写到一个jpg文件中
            ImageIO.write(bi, "png", new File(RobotConstants.IMAGE_PATH));
            return RobotConstants.TRUE;
        } catch (IOException e) {
            LoggerUtils.error(HandleController.class , e.getMessage() , e);
            throw new SubException(ExceptionConstants.DONT_CUT_IMAGE);
        }
    }

    /**
     * 鼠标滚轮转动
     * @param wheelAmt 数字格式，向上滚动为负数，向下滚动为正数。数字代表滚轮滚动的凹槽个数
     * @return
     */
    public boolean mouseWheel(String wheelAmt) throws SubException {
        wheelAmt = StringUtils.isEmpty(wheelAmt) ? "0" : wheelAmt;
        try {
            Integer amt = Integer.parseInt(wheelAmt);
            r.mouseWheel(amt);
        } catch (NumberFormatException e) {
            LoggerUtils.error(this.getClass() , "鼠标滚轮转动次数必须为数字" , e);
            throw new SubException("鼠标滚轮转动次数必须为数字" , e);
        }
        return RobotConstants.TRUE;
    }

    /**
     * 等待，最长不超过10分钟
     * @param second
     * @return
     */
    public boolean delay(Integer second) throws SubException {
        //计算需等待的时长，转化为毫秒
        Integer waitTime = second * 1000;
        if(waitTime <= RobotConstants.WAIT_TIME){
            r.delay(waitTime);
            return RobotConstants.TRUE;
        }
        LoggerUtils.error(this.getClass() , ExceptionConstants.WAIT_TIME_EXCEPTION + RobotConstants.WAIT_TIME);
        throw new SubException(ExceptionConstants.WAIT_TIME_EXCEPTION);
    }

    /**
     * 停止当前线程池所有任务
     * @return
     */
    public boolean stop(){
        ThreadConfiguration.THREAD_POOL.shutdown();
        return RobotConstants.TRUE;
    }

    /**
     * 停止当前线程池所有任务
     * @return
     */
    public boolean pause(){
        RobotConstants.OPERATING_VAR = "pause";
        return RobotConstants.TRUE;
    }

    /**
     * 根据表格中的日期在操作界面中自动选择对应日期
     * @param date      需要对正的日期，格式要求为YYYY-MM-DD
     * @param position
     * @return
     */
    public boolean selectDate(String date , String position) throws SubException, AWTException {
        String [] dateStrArr = StringUtils.dateFormat(date);
        Integer year = Integer.parseInt(dateStrArr[0]);
        Integer month = Integer.parseInt(dateStrArr[1]);
        Integer day = Integer.parseInt(dateStrArr[2]);
        //循环控制器
        boolean finalResult = true;

        while(finalResult) {
            //定义每一个数字间移动的像素值  35px
            //识别所有包含年、月、日的文字
            ArrayList<Map> resultList = imgOcrGetResultStr(RobotConstants.IMAGE_PATH, "2");
            Map locationMap = Maps.newHashMap();
            ArrayList<Integer> yearList = new ArrayList();
            ArrayList<Integer> monthList = new ArrayList();
            ArrayList<Integer> dayList = new ArrayList();
            for (int i = 0; i < resultList.size(); i++) {
                Object o = resultList.get(i);
                LinkedTreeMap tree = (LinkedTreeMap) o;
                String words = tree.get("words").toString();//解析的中文
                if (words.contains("年")) {
                    yearList.add(Integer.parseInt(words.replace("年", "")));
                    locationMap.put(words , tree.get("location"));//中文坐标)
                }
                if (words.contains("月")) {
                    monthList.add(Integer.parseInt(words.replace("月", "")));
                    locationMap.put(words , tree.get("location"));//中文坐标)
                }
                if (words.contains("日")) {
                    dayList.add(Integer.parseInt(words.replace("日", "")));
                    locationMap.put(words , tree.get("location"));//中文坐标)
                }
            }
            //将年排除后，分为3个数组做各自的排序。正序排列
            yearList.sort(Comparator.comparingInt(Integer::intValue));
            monthList.sort(Comparator.comparingInt(Integer::intValue));
            dayList.sort(Comparator.comparingInt(Integer::intValue));
            //排序之后，与当前的日期做比较，从年开始
            //则判断当前数字与第4位的大小，计算第4位与当前数字的差。
            Integer currentYear = yearList.get(4);
            Integer currentMonth = yearList.get(4);
            Integer currentDay = yearList.get(4);


            //当前数字大则往上滑动，如小则往下滑动，移完之后重新截图
            //循环判断，直至截图识别的数字排在第4位为止
            //年、月、日依次滑动
            boolean yearBoo = loopDialPlate(year, locationMap, currentYear , "年");
            boolean monthBoo = loopDialPlate(month, locationMap, currentMonth , "月");
            boolean dayBoo = loopDialPlate(day, locationMap, currentDay , "日");
            if(yearBoo && monthBoo &&dayBoo){
                //false则停止循环
                return false;
            }
            //截图、检查是否符合要求
            cut();
            //结束返回
        }
        return RobotConstants.TRUE;
    }

    /**
     * 删除
     * @return
     */
    public boolean delete(){
        r.keyPress(KeyEvent.VK_BACK_SPACE);
        r.keyRelease(KeyEvent.VK_BACK_SPACE);
        return RobotConstants.TRUE;
    }

    /**
     * 搜索当前图片中是否有需要的文字
     * @param text 需要搜索的文字
     * @return
     */
    public boolean search(String text){
        Map result = XiaoLiTessract.general(RobotConstants.IMAGE_PATH , text);
        //返回结果为空，则说明没搜到，返回false
        if(result.isEmpty()){
            return RobotConstants.FALSE;
        }
        return RobotConstants.TRUE;
    }

    /**
     * 循环波动表盘，滚动至目标值
     * @param count            所需判断的年份、月份、日期
     * @param locationMap    所有表盘中值得位置信息
     * @param currentYear   当前表盘中指向的值
     * @param key  取出位置信息的关键字
     */
    private boolean loopDialPlate(Integer count, Map locationMap, Integer currentYear ,String key) {
        Integer diff = 0;
        String direction = null;
        if (count != currentYear) {
            diff = count - currentYear;
            //获取所需移动的次数、及每次移动的像素
            if(diff < 0){
                direction = RobotConstants.UP;
            }else if(diff > 0 ){
                direction = RobotConstants.DOWN;
            }else if(diff == 0){
                return RobotConstants.TRUE;
            }
            diff = Math.abs(diff);
            //获取到了总共需要移动的像素
            //计算出每次可移动像素量
            Integer offset = RobotConstants.MOVE_PX * 4;  //每移动一个单位为35像素，而每翻一页年份都需要移动4格，所以为35 * 4
            //首次先要将鼠标定位至轮盘中间
            LinkedTreeMap locations = (LinkedTreeMap) locationMap.get(currentYear + key);
            mouseLocationXY(locations.get("top").toString() , locations.get("left").toString());


            int step = 1;
            for(int i = 0 ; i < diff ; i++){
                //按下鼠标
                mousePress();
                //重新
                mouseOffset(RobotConstants.MOVE_PX , direction);
                //释放鼠标
                mouseRelease();
                if(step >= 4){
                    mouseOffset(offset , RobotConstants.getDirection(direction));
                    step = 1;
                }
                step++;
            }
            return RobotConstants.FALSE;
        }else if(count == currentYear){
            return RobotConstants.TRUE;
        }
        return RobotConstants.FALSE;
    }

}
