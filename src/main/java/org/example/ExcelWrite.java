package org.example;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelWrite {

    public static void main(String[] args) {

    }
    String path = "/Users/xuyaochen/FuRiIT/project/JavaNetty";
    @Test
    public void test() throws IOException {
        //1 创建一个 工作簿
        Workbook workbook = new HSSFWorkbook();
        //2 创建一个工作表
        Sheet sheet = workbook.createSheet("徐耀晨的测试");
        //3 创建一个行
        Row row1 = sheet.createRow(0);
        //4 创建一个单元格
        Cell cell = row1.createCell(0);
        cell.setCellValue("进入新增观众");

        //创建第二行
        Row row2 = sheet.createRow(1);
        row2.createCell(0);


        Cell cell12 = row1.createCell(2);
        String string = new DateTime().toString("yyyy-mm-dd");
        cell12.setCellValue(string);

        // 生成一张表 IO流 03的版本使用xls结尾
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(path + "xyc03.xls");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        workbook.write(fileOutputStream);

        //关闭流
        fileOutputStream.close();

        System.out.println("文件生成完毕");
    }
}
