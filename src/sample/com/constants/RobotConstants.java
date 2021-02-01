package sample.com.constants;

import sample.com.utils.ThreadConinfguration.Configuration;

import java.math.BigDecimal;

public class RobotConstants {

    /**
    鼠标定位
     */
    public final static String MOUSE_LOCATION = "mouseLocation";

    /**
     * 鼠标偏移
     */
    public final static String MOUSE_OFFSET = "mouseOffset";

    /**
     * 以鼠标当前位置为基础偏移
     */
    public final static String LOCAL_MOUSE_OFFSET = "localMouseOffset";
    /**
     * 截图局部
     */
    public final static String CUT_PART = "cutPart";


    /**
     * 鼠标点击
     */
    public final static String MOUSE_CLICK = "mouseClick";

    /**
     * 鼠标锁定
     */
    public final static String MOUSE_HOLD = "mouseHold";

    /**
     * 鼠标释放
     */
    public final static String MOUSE_FREE = "mouseFree";
    /**
     * 输入文字
     */
    public final static String MOUSE_TEXT = "inputText";

    /**
     * 截屏
     */
    public final static String CUT = "cut";

    /**
     * 等待、休眠
     */
    public final static String WAIT = "wait";

    /**
     * 需要循环判断的文字
     */
    public final static String EACH_CUT = "eachCut";

    /**
     * 需要循环判断的文字
     */
    public final static String STOP = "stop";
    /**
     * 暂停
     */
    public final static String PAUSE = "pause";

    /**
     * 截图、鼠标定位、点击三合一操作
     */
    public final static String MOUSELOCANDCLICK = "mouseLocAndClick";

    /**
     * 鼠标滚轮转动
     */
    public final static String MOUSEWHEEL = "mouseWheel";

    /**
     * 选择小程序中的表盘日期
     */
    public final static String SELECTDATE = "selectDate";

    /**
     * 快捷键组合键
     */
    public final static String COMBHOTKEY = "combHotkey";
    /**
     * 全选快捷键
     */
    public final static String SELECTALL = "selectAll";
    /**
     * 全选快捷键
     */
    public final static String MOUSELOCATIONXY = "mouselocationXY";
    /**
     * 移动鼠标并且点击鼠标
     */
    public final static String MOUSEMOVEANDCLICK = "mouseMoveAndClick";

    /**
     * 移动鼠标并且点击鼠标
     */
    public final static String ENTER = "enter";

    /**
     * 删除操作
     */
    public final static String DELETE = "delete";


    /**
     * 自动截图的地址，始终只存在一张截图
     */
    public final static String IMAGE_PATH = "c:\\image.png";

    /**
     *
     */
    public final static boolean TRUE = true;
    public final static boolean FALSE = false;

    /**
     * 等待时长最大值,10分钟
     */
    public final static Integer WAIT_TIME = 600000;

    /**
     * 0
     */
    public final static Integer ZERO = 0;

    /**
     * 日期之间的间隔像素点
     */
    public final static Integer MOVE_PX = 35;

    /**
     * 循环截图模块的循环次数
     */
    public final static Integer LOOP_COUNT = 5;

    /**
     * 偏移方向
     */
    public final static String UP = "上";
    public final static String DOWN = "下";
    public final static String LEFT = "左";
    public final static String RIGHT = "右";

    /**
     * 变量的固定开闭标签
     */
    public final static String VAR_START_TAG = "{{";
    public final static String VAR_END_TAG = "}}";

    /**
     * 变量的固定开闭标签
     */
    public final static String PARAM_START_TAG = "(";
    public final static String PARAM_END_TAG = ")";

    /**
     * spilit分隔符
     */
    public final static String SPILIT_SYMBOL = ",";

    /**
     * 控制程序是否暂停/启动/停止的变量
     */
    public static String OPERATING_VAR = "stop";


    //------------------------------------快捷键组合所需变量
    /**
     * ctrl
     */
    public final static String CTRL = "ctrl";

    /**
     * alt
     */
    public final static String ALT = "alt";

    /**
     * shift
     */
    public final static String SHIFT = "shift";

    /**
     * wind
     */
    public final static String WIND = "wind";




    /**
     * 返回偏移方位--反方向
     */
    public static String getDirection(String direction){
        switch (direction){
            case UP :
                return DOWN;
            case DOWN :
                return UP;
            case LEFT :
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        return null;
    }

    //ocr开关，1：百度ocr  2：肖立ocr
    public static final Integer OCR_SWITCH = Configuration.getInstance().getIntValue("ocr_switch");

    //捕捉脚本动作的controller
    public static final String HANDLE_CONTROLL = Configuration.getInstance().getValue("handle_controll");

    //百度用户id
    public static final String CLIENT_ID = Configuration.getInstance().getValue("client_id");

    //百度授权码
    public static final String CLIENT_SECRET = Configuration.getInstance().getValue("client_secret");

    //用于代理中将毫秒转换成秒的常量
    public static final BigDecimal MS = new BigDecimal("1000");

}
