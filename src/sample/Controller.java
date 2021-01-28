package sample;

import sample.com.constants.HotKeyConstants;
import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import sample.com.main.ParserMain;
import sample.com.main.controller.HandleController;
import sample.com.main.controller.proxy.ProxyFactory;
import sample.com.main.controller.service.HandleService;
import com.melloware.jintellitype.JIntellitype;
import sample.utils.ControlledStage;
import sample.utils.StageController;
import sample.com.utils.LoggerUtils;
import sample.com.utils.StringUtils;
import sample.com.utils.ThreadConinfguration.ThreadConfiguration;
import sample.com.utils.excel.ReadExcelUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements ControlledStage, Initializable  {

    @FXML
    public TextField dataField; //信息表录入框

    @FXML
    public TextField scriptField;   //脚本路径框

    @FXML
    public TextField normalDelay;   //标准延迟输入框

    @FXML
    public TextArea TextArea;   //控制台

    @FXML
    public Button dataButton;   //导入表格按钮

    @FXML
    public Button scriptButton; //导入脚本按钮

    @FXML
    public Button start;        //开始按钮

    @FXML
    public Button pause;        //暂停按钮

    @FXML
    public Button stop;         //停止按钮

    private StageController myController;

    Stage selectFile = new Stage();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pause.setDisable(true);
        this.setStageController(myController);
        // 添加热键监听器
        this.register();
        // 第二步：添加热键监听器
        this.addListener();
    }

    @Override
    public void setStageController(StageController stageController) {
        this.myController = stageController;
    }

    public void goToSecond(){
        myController.setStage(Main.secondID , Main.mainViewID);
    }

    /**
     * 获取数据文件路径
     */
    public void getDataFilePath(){
        //文件选择器
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择Excel文件");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        //设置被选文件后缀
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Excel", "*.xlsx"), new FileChooser.ExtensionFilter("XLS", "*.xls"), new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
        File file = fileChooser.showOpenDialog(selectFile);
        if(file!=null){
            dataField.setText(file.getAbsolutePath());
        }
    }

    /**
     * 获取脚本文件路径
     */
    public void getScriptFilePath(){
        //文件选择器
        FileChooser script = new FileChooser();
        script.setTitle("选择脚本文件");
        script.setInitialDirectory(new File(System.getProperty("user.home")));
        //设置被选文件后缀
        script.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt", "*.txt"));
        File file = script.showOpenDialog(selectFile);
        if(file!=null){
            scriptField.setText(file.getAbsolutePath());
        }
    }

    /**
     * 切换页面
     */
    public void change(){
        goToSecond();
    }

    /**
     * 获取脚本文件路径
     */
    public void start() {

        if(RobotConstants.OPERATING_VAR.equals("pause")){
            pause.setDisable(false);
            start.setDisable(true);
            RobotConstants.OPERATING_VAR = "start";
        }else
            ThreadConfiguration.THREAD_POOL.execute(new Runnable() {
                @Override
                public void run() {
//                    dataField.setText("E:\\文档\\项目资料\\智能机器人\\测试资料\\2_平顶山要素表.xlsx");
                    String dataFieldText = dataField.getText();
                    String scriptFieldText = scriptField.getText();

                    String delay = normalDelay.getText();
                    ProxyFactory.nomalDelay = delay;
                    if(StringUtils.isEmpty(scriptFieldText) || StringUtils.isEmpty(dataFieldText)){
                        Platform.runLater(() -> TextArea.appendText("未选择案件信息表或执行脚本，请选择完成后开始执行\n"));
                        LoggerUtils.error(this.getClass(),"未选择案件信息表或执行脚本，请选择完成后开始执行");
                        return ;
                    }
                    //文本框和按钮失效
                    dataField.setDisable(true);
                    scriptField.setDisable(true);
                    dataButton.setDisable(true);
                    scriptButton.setDisable(true);
                    pause.setDisable(false);
                    start.setDisable(true);

                    // 1 在事件源对象注册 source.setOnXEventType(listener)
                    try {
                        TextArea.appendText("准备开始执行\n");
                        Thread.sleep(3000);

                        //键盘操作后再做下一步循环
//                        Scanner sc = new Scanner(System.in);
                        //1、读取数据表格
                        ReadExcelUtil readExcelUtil = new ReadExcelUtil(dataFieldText);
                        TextArea.appendText("读取案件信息表数据\n");
                        List<Map<String ,String >> list = readExcelUtil.getObjectsList();
                        //循环excel表格中的所有数据
                        int listIndex = 1;
                        for (Map<String ,String > dataMap : list) {
                            boolean isPause = RobotConstants.OPERATING_VAR.equals("pause");
                            if(isPause) {
                                Platform.runLater(() -> TextArea.appendText("程序已暂停\n"));
                                while (isPause) {
                                    try {
                                        HandleService.getRobot().delay(3000);
                                    } catch (AWTException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            //2、读取脚本文件，并返回脚本集，封为List
                            List<Map> result = ParserMain.readScriptForList(scriptFieldText);
                            Platform.runLater(() -> TextArea.appendText("脚本总共" + result.size() + "行\n"));
                            String execute_excel_line = "正在执行第" + listIndex + "条表格记录\n";
                            Platform.runLater(() -> TextArea.appendText(execute_excel_line));

                            ParserMain.action(dataMap, result);
                            //显示正在执行第几条记录
                            String execute_excel_line_finish = "第" + listIndex + "条记录执行完毕，正在执行下一条\n";
                            Platform.runLater(() -> TextArea.appendText(execute_excel_line_finish));
                            //停止操作，退出循环
                            if(RobotConstants.OPERATING_VAR.equals("stop")){
                                Platform.runLater(() -> TextArea.appendText("正在停止运行，请等待。\n"));
                                break;
                            }
                            listIndex++;
                            Platform.runLater(() -> TextArea.appendText("一条数据执行完毕，请点击开始继续运行。\n"));
                            pause();

//                            String str = sc.nextLine();

                        }
                    } catch (InterruptedException e) {
                        LoggerUtils.error(this.getClass() , "准备开始阶段出错\n" ,e);
                        Platform.runLater(() -> TextArea.appendText("准备开始阶段出错,请重试\n"));
                    } catch (IOException e) {
                        LoggerUtils.error(this.getClass() , "读取数据出错，请检查文件路径是否正确\n" , e );
                        Platform.runLater(() -> TextArea.appendText("读取数据出错，请检查文件路径是否正确\n"));
                    } catch (SubException e) {
                        LoggerUtils.error(this.getClass() , e.getMessage() , e);
                        TextArea.appendText(e.getMessage());
                        Platform.runLater(() -> TextArea.appendText(e.getMessage()));
                    } finally {
                        dataField.setDisable(false);
                        scriptField.setDisable(false);
                        dataButton.setDisable(false);
                        scriptButton.setDisable(false);
                        pause.setDisable(true);
                        start.setDisable(false);
                        RobotConstants.OPERATING_VAR = "start";
                        Platform.runLater(() -> TextArea.appendText("已结束。\n"));
                    }
                }
            });
    }

    public void pause(){
        if(RobotConstants.OPERATING_VAR.equals("start")){
            TextArea.appendText("当前程序已暂停，请点击“开始”恢复执行\n");
            RobotConstants.OPERATING_VAR = "pause";
            start.setDisable(false);
            pause.setDisable(true);
        }/*else{
            TextArea.appendText("当前程序未启动或已暂停，无法执行当前操作\n");
        }*/
    }

    /**
     * 停止线程池
     */
    public void stop(){
        RobotConstants.OPERATING_VAR = "stop";
        Platform.runLater(() -> TextArea.appendText("操作停止。\n"));
    }

    /**
     * 全局热键监听
     */
    public void addListener()
    {
        System.out.println("全局监听");
        JIntellitype.getInstance().addHotKeyListener(markCode -> {
            switch (markCode) {
                case HotKeyConstants.HOT_KEY_STOP:      //停止
                    this.stop();
//                    JOptionPane.showMessageDialog(null, "注册快捷键(Q):跳出弹框！", "提示消息", JOptionPane.WARNING_MESSAGE);
                    break;
                case HotKeyConstants.HOT_KEY_PAUSE:     //暂停
                    this.pause();
//                    JOptionPane.showMessageDialog(null, "暂停程序", "提示消息", JOptionPane.WARNING_MESSAGE);
                    break;
            }
        });
    }

    /**
     * 热键注册
     */
    public void register() {
        JIntellitype.getInstance().registerHotKey(HotKeyConstants.HOT_KEY_STOP, JIntellitype.MOD_ALT, (int) 'Q');
        JIntellitype.getInstance().registerHotKey(HotKeyConstants.HOT_KEY_PAUSE, JIntellitype.MOD_ALT, (int) 'D');
        LoggerUtils.info(Controller.class,"键盘监听register...");
    }

    /**
     * 热键解绑
     */
    public static void unregister() {
        JIntellitype.getInstance().unregisterHotKey(89);
        System.out.println("unregister...");
    }
}
