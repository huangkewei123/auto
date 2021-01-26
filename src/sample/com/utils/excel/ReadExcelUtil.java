package sample.com.utils.excel;

import com.google.common.collect.Maps;
import sample.com.utils.LoggerUtils;
import sample.com.utils.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @comments :
 */

public class ReadExcelUtil {

    private static XSSFFormulaEvaluator evaluator;
    //存放属性集
    private  Map<Integer,String []> fieldsMap=new HashMap<>();
    //存放解析后的对象List
    private List<Map<String ,String >> objectsList = new ArrayList<>();
    //Excel文件路径
    private  String path =null;
    //获取解析后的对象集
    public List<Map<String ,String >> getObjectsList() {
        return this.objectsList;
    }

    public ReadExcelUtil(String path) throws IOException {
        this.path = path;
        readExcel();
    }

    /**
     * 添加Object到List中
     * @param object
     * @return
     */
    public boolean addListObject(Map<String ,String > object){
        return  this.objectsList.add(object);
    }

    /**
     * 读取excel,判断是xls结尾(2010之前)；还是xlsx结尾(2010以后)的Excel
     *
     * @return
     * @throws IOException
     */
    public boolean readExcel() throws IOException {
        if (StringUtils.isEmpty(path)) {
            return false;
        } else {
            // 截取后缀名，判断是xls还是xlsx
            String postfix = path.substring(path.lastIndexOf(".") + 1);
            if (!StringUtils.isEmpty(postfix)) {
                if (Common.OFFICE_EXCEL_2003_POSTFIX_xls.equals(postfix)) {
                    return readXls();
                } else if (Common.OFFICE_EXCEL_2010_POSTFIX_xlsx.equals(postfix)) {
                    return readXlsx();
                }
            } else {
                LoggerUtils.error(this.getClass() ,"文件后缀名有误！");
                throw new IOException("文件后缀名有误！" + "[" + path + "]");
            }
        }
        return false;
    }

    /**
     * 读取xls(2010)之后的Excel
     *
     * @return
     * @throws IOException
     */
    public  boolean readXlsx() throws IOException{
        File file = new File(path);
        InputStream is = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        evaluator = new XSSFFormulaEvaluator(xssfWorkbook);
        // 遍历sheet页
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // 循环行
            for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                Row xssfRow = xssfSheet.getRow(rowNum);
                Map<String, String> rowValue = getStringStringMap(numSheet, rowNum, xssfRow);
                if (rowValue == null) continue;
                this.addListObject(rowValue);
//                System.out.println(rowValue.toString());
            }
        }
        return true;
    }

    /**
     * 读取xls(2010)之前的Excel
     *
     * @return
     * @throws IOException
     */
    public boolean readXls() throws IOException, ResolveFileException {
        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 遍历sheet页
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                Map<String, String> rowValue = getStringStringMap(numSheet, rowNum, hssfRow);
                if (rowValue == null) continue;
                this.addListObject(rowValue);
            }
        }
        return true;
    }

    /**
     * xlsx -根据数据类型，获取单元格的值
     * @param xssfRow
     * @return
     */
    @SuppressWarnings({ "static-access" })
    private static String getValue(Cell xssfRow) {
        String value=null;
        try {
            if (xssfRow.getCellType() == CellType.BOOLEAN) {
                // 返回布尔类型的值
                value=String.valueOf(xssfRow.getBooleanCellValue()).replace(" ","");
            } else if (xssfRow.getCellType() == CellType.NUMERIC) {
                value = dateAndNumFormatToString(xssfRow);
            } else if(xssfRow.getCellType() == CellType.FORMULA){
                try {
                    //拿出公司计算之后的字符串值，如果报错则转换为日期格式
                    value = String.valueOf(xssfRow.getRichStringCellValue());
                } catch (IllegalStateException e) {
                    value = dateAndNumFormatToString(xssfRow);
                }
            }else {
                // 返回字符串类型的值
                value= String.valueOf(xssfRow.getStringCellValue()).replace(" ","");
            }
        } catch (Exception e) {
            //单元格为空，不处理
            value=null;
            LoggerUtils.error(ReadExcelUtil.class ,"单元格为空！" , e);
        }
        return value;
    }

    /**
     * 将数字类型、日期类型转换成String类型
     * @param cell
     * @return
     */
    private static String dateAndNumFormatToString(Cell cell) {
        String value;
        if (DateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
            SimpleDateFormat sdf = null;
            if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                sdf = new SimpleDateFormat("HH:mm");
            } else {// 日期
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            }
            Date date = cell.getDateCellValue();
            value = sdf.format(date);
        } else if (cell.getCellStyle().getDataFormat() == 58) {
            // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            double values = cell.getNumericCellValue();
            Date date = DateUtil
                    .getJavaDate(values);
            value = sdf.format(date);
        } else {
            double values = cell.getNumericCellValue();
            CellStyle style = cell.getCellStyle();
            DecimalFormat format = new DecimalFormat();
            String temp = style.getDataFormatString();
            // 单元格设置成常规
            if (temp.equals("General")) {
                format.applyPattern("0.##");
            }
            value = format.format(values);
        }
        return value;
    }

    /**
     * xlsx Excel文件类型获取属性（2010之后）
     * @param row
     * @param cloumns
     * @return String[]
     */
    private  static String[] getFields(Row row,int cloumns){
        String [] fields=new String[cloumns];
        int i=0;
        try {
            while (i<cloumns){
                Cell field=row.getCell(i);
                String value=getValue(field);
                fields[i]=value.trim();
                i++;
            }
        }catch ( Exception e ){
            throw  new ResolveFileException("获取属性集失败！");
        }
        return  fields;
    }

    private Map<String, String> getStringStringMap(int numSheet, int rowNum, Row row) {
        String[] fields;
        int cloumns=row.getLastCellNum();
        int i=0;
        //获取第一行的所有属性
        if (rowNum == 0){
            //获取属性字段
            fields=getFields(row,cloumns);
            fieldsMap.put(numSheet,fields);
            return null;
        }

        //遍历数据,反射set值
        String [] tableHead = fieldsMap.get(0);
        Map<String , String > rowValue = Maps.newHashMap();
        while (i<cloumns){
            Cell field=row.getCell(i);
            String value=getValue(field);
            rowValue.put(tableHead[i] , value);
            i++;
        }
        return rowValue;
    }
}