/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.JDBCUtils;

/**
 *
 * @author duyplus
 */
public class Excel {

    public static Connection conn = null;
    public static PreparedStatement ps = null; // Câu lệnh SQL được biên dịch trước
    public static Statement st = null; // Thi hành câu lệnh tùy ý tại thời điểm chạy
    public static ResultSet rs = null; // Trả về kết quả truy vấn

    public void exStudent() {
        JFileChooser fc = new JFileChooser("D:\\");
        fc.setDialogTitle("Lưu bảng sinh viên");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xls", "xlsx", "xlsm");
        fc.setFileFilter(filter);
        int choose = fc.showSaveDialog(null);
        System.out.println("Selecting a location...");
        if (choose == JFileChooser.APPROVE_OPTION) {
            try {
                conn = JDBCUtils.getConnection();
                st = conn.createStatement();
                rs = st.executeQuery("SELECT * FROM STUDENTS");
                XSSFWorkbook wb = new XSSFWorkbook();
                XSSFSheet sheet = wb.createSheet("LIST STUDENTS");
                // Creating a font
                XSSFCellStyle cs = wb.createCellStyle();
                XSSFFont font = wb.createFont();
                font.setFontHeightInPoints((short) 12);
                font.setColor(IndexedColors.YELLOW.getIndex());
                font.setBold(true);
                cs.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cs.setAlignment(HorizontalAlignment.CENTER);
                cs.setFont(font);
                // Set width columns
                sheet.setColumnWidth(1, 20 * 256);
                sheet.setColumnWidth(2, 20 * 256);
                sheet.setColumnWidth(3, 15 * 256);
                sheet.setColumnWidth(4, 12 * 256);
                sheet.setColumnWidth(5, 20 * 256);
                // Set title columns
                XSSFRow title = sheet.createRow((short) 0);
                // Create cells
                String[] columns = {"MÃ SV", "HỌ VÀ TÊN", "EMAIL", "SỐ ĐT", "GIỚI TÍNH", "ĐỊA CHỈ", "HÌNH"};
                for (int i = 0; i < columns.length; i++) {
                    XSSFCell cell = title.createCell(i);
                    cell.setCellValue(columns[i]);
                    // Setting cell style
                    cell.setCellStyle(cs);
                }
                int index = 1;
                while (rs.next()) {
                    XSSFRow row = sheet.createRow((short) index);
                    row.createCell((short) 0).setCellValue(rs.getString(1));
                    row.createCell((short) 1).setCellValue(rs.getString(2));
                    row.createCell((short) 2).setCellValue(rs.getString(3));
                    row.createCell((short) 3).setCellValue(rs.getString(4));
                    row.createCell((short) 4).setCellValue(rs.getBoolean(5));
                    row.createCell((short) 5).setCellValue(rs.getString(6));
                    row.createCell((short) 6).setCellValue(rs.getString(7));
                    index++;
                }
                FileOutputStream fos = new FileOutputStream(fc.getSelectedFile() + ".xlsx");
                wb.write(fos);
                JOptionPane.showMessageDialog(null, "Lưu dữ liệu bảng sinh viên thành công!");
                System.out.println("Save successfully!");
                fos.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void exGrade() {
        JFileChooser fc = new JFileChooser("D:\\");
        fc.setDialogTitle("Lưu bảng điểm sinh viên");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xls", "xlsx", "xlsm");
        fc.setFileFilter(filter);
        int choose = fc.showSaveDialog(null);
        System.out.println("Selecting a location...");
        if (choose == JFileChooser.APPROVE_OPTION) {
            try {
                conn = JDBCUtils.getConnection();
                st = conn.createStatement();
                String sql = "SELECT STUDENTS.MASV AS MASV,HOTEN,TIENGANH,TINHOC,GDTC,(TIENGANH+TINHOC+GDTC)/3 AS TBM "
                    + " FROM STUDENTS, GRADES WHERE STUDENTS.MASV = GRADES.MASV "
                    + " ORDER BY TBM DESC";
                rs = st.executeQuery(sql);
                XSSFWorkbook wb = new XSSFWorkbook();
                XSSFSheet sheet = wb.createSheet("LIST GRADES");
                // Creating a font
                XSSFCellStyle cs = wb.createCellStyle();
                XSSFFont font = wb.createFont();
                font.setFontHeightInPoints((short) 12);
                font.setColor(IndexedColors.YELLOW.getIndex());
                font.setBold(true);
                cs.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cs.setAlignment(HorizontalAlignment.CENTER);
                cs.setFont(font);
                // Set width columns
                sheet.setColumnWidth(1, 20 * 256);
                sheet.setColumnWidth(2, 15 * 256);
                sheet.setColumnWidth(3, 15 * 256);
                sheet.setColumnWidth(4, 15 * 256);
                sheet.setColumnWidth(5, 20 * 256);
                // Set title columns
                XSSFRow title = sheet.createRow((short) 0);
                // Create cells
                String[] columns = {"MÃ SV", "HỌ VÀ TÊN", "TIẾNG ANH", "TIN HỌC", "GDTC", "ĐIỂM TB"};
                for (int i = 0; i < columns.length; i++) {
                    XSSFCell cell = title.createCell(i);
                    cell.setCellValue(columns[i]);
                    // Setting cell style
                    cell.setCellStyle(cs);
                }
                int index = 1;
                while (rs.next()) {
                    XSSFRow row = sheet.createRow((short) index);
                    row.createCell((short) 0).setCellValue(rs.getString(1));
                    row.createCell((short) 1).setCellValue(rs.getString(2));
                    row.createCell((short) 2).setCellValue(rs.getDouble(3));
                    row.createCell((short) 3).setCellValue(rs.getDouble(4));
                    row.createCell((short) 4).setCellValue(rs.getDouble(5));
                    row.createCell((short) 5).setCellValue(rs.getDouble(6));
                    index++;
                }
                FileOutputStream fos1 = new FileOutputStream(fc.getSelectedFile() + ".xlsx");
                wb.write(fos1);
                JOptionPane.showMessageDialog(null, "Lưu dữ liệu bảng điểm thành công!");
                System.out.println("Save successfully!");
                fos1.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
