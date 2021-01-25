package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import sample.com.constants.RobotConstants;
import sample.com.utils.LoggerUtils;
import sample.utils.StageController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static String mainViewID = "sample";
    public static String mainViewRes = "../sample.fxml";

    public static String secondID = "second";
    public static String secondRes = "second/second.fxml";

    private StageController stageController;

    @Override
    public void start(Stage primaryStage) throws IOException {
        //新建一个StageController控制器
//        stageController = new StageController();
//
//        //将主舞台交给控制器处理
//        stageController.setPrimaryStage("primaryStage", primaryStage);
//
//        //加载一个舞台
//        stageController.loadStage(mainViewID, mainViewRes);
////        stageController.loadStage(secondID, secondRes);
//
//        //显示MainView舞台
//        stageController.setStage(mainViewID);

        RobotConstants.OPERATING_VAR = "start";
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("智能机器人");
        primaryStage.setScene(new Scene(root, 768, 599));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            LoggerUtils.info(Main.class,"窗口关闭");
            System.exit(0);
        });


    }


    public static void main(String[] args) {
        launch(args);
    }
}
