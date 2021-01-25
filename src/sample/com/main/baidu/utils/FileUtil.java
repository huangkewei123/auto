package sample.com.main.baidu.utils;

import sample.com.constants.ExceptionConstants;
import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;
import com.google.common.collect.Maps;
import sample.com.utils.LoggerUtils;
import sample.com.utils.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件读取工具类
 */
public class FileUtil {



    /**
     * 读取文件内容，作为字符串返回
     */
    public static String readFileAsString(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } 

        if (file.length() > 1024 * 1024 * 1024) {
            throw new IOException("File is too large");
        } 

        StringBuilder sb = new StringBuilder((int) (file.length()));
        // 创建字节输入流  
        FileInputStream fis = new FileInputStream(filePath);  
        // 创建一个长度为10240的Buffer
        byte[] bbuf = new byte[10240];  
        // 用于保存实际读取的字节数  
        int hasRead = 0;  
        while ( (hasRead = fis.read(bbuf)) > 0 ) {  
            sb.append(new String(bbuf, 0, hasRead));  
        }  
        fis.close();  
        return sb.toString();
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                bos.close();
            }
        }
    }

    /**
     * 读取文件内容，作为字符串返回
     */
    public static List<Map> readFileAsMap(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            LoggerUtils.error(FileUtil.class,ExceptionConstants.PATH_EXISTS + filePath);
            throw new SubException(filePath);
        }
        if (file.length() > 1024 * 1024 * 1024) {
            LoggerUtils.error(FileUtil.class,"File is too large");
            throw new SubException("File is too large");
        }
        //获得文件读取流
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis,"GBK");
        BufferedReader br = new BufferedReader(isr);
        //每行的文字
        String lineText = null;
        //操作名称
        String handleName = null;
        List<Map> resultList = new ArrayList<Map>();
        while((lineText = br.readLine()) != null){
            //脚本中不允许存在空行命令
            if (StringUtils.isEmpty(lineText)){
                LoggerUtils.error(FileUtil.class , ExceptionConstants.SCRIPT_SOME_ROWS_NULL);
                throw new SubException(ExceptionConstants.SCRIPT_SOME_ROWS_NULL);
            }
            //创建结果map，最终结果处理为Map<String , Object[]>
            Map result = Maps.newHashMap();
            int index = lineText.indexOf("(");
            handleName = lineText.substring(0 , index);
            result.put(handleName , StringUtils.getParamValue(lineText , "(" , ")"));
            resultList.add(result);
        }
        return resultList;
    }

    public static void main(String[] args) throws Exception {
        List<Map> a = readFileAsMap(RobotConstants.IMAGE_PATH);
        for ( int i = 0 ; i < a.size() ; i++){
            String json = GsonUtils.toJson(a.get(i));
        }
    }
}
