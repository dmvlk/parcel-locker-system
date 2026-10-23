package ru.mirea.postamat.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.mirea.postamat.model.Cell;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelExport {

    private ExcelExport() {}

    public static void exportCells(List<Cell> cells, String lockerAddress) throws Exception {
        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(lockerAddress)) {

            Sheet sheet = wb.createSheet("Ячейки");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Адрес");
            header.createCell(2).setCellValue("Номер");
            header.createCell(3).setCellValue("Размер");
            header.createCell(4).setCellValue("Статус");

            int rowNum = 1;
            for (Cell c : cells) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getLockerAddress());
                row.createCell(2).setCellValue(c.getCellNumber());
                row.createCell(3).setCellValue(c.getSizeType().name());
                row.createCell(4).setCellValue(c.getStatus().name());
            }

            wb.write(out);
        }
    }
}