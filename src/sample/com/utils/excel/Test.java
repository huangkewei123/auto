package sample.com.utils.excel;

import java.util.List;
import java.util.Map;


/**
 * @version 1.0.0
 * @author  cnblogs-WindsJune
 * @date    2018年9月23日 上午1:16:34
 *
 */
public class Test {
    public static void main(String[] args) {
        String filePath="F:\\公司资料\\机器人\\测试资料\\112.xlsx";

        try {
            ReadExcelUtil readExcelUtil = new ReadExcelUtil(filePath);
            List<Map<String ,String >> list = readExcelUtil.getObjectsList();
            for (Map<String , String> object:list){

                System.out.println(object.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}