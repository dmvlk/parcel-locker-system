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

    public static void export_cells(List<Cell> cells, String file_path) throws Exception {
        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file_path)) {

            Sheet sheet = wb.createSheet("Ячейки");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Адрес");
            header.createCell(2).setCellValue("Номер");
            header.createCell(3).setCellValue("Размер");
            header.createCell(4).setCellValue("Статус");

            int row_num = 1;
            for (Cell c : cells) {
                Row row = sheet.createRow(row_num++);
                row.createCell(0).setCellValue(c.get_id());
                row.createCell(1).setCellValue(c.get_locker_address());
                row.createCell(2).setCellValue(c.get_cell_number());
                row.createCell(3).setCellValue(c.get_size_type().name());
                row.createCell(4).setCellValue(c.get_status().name());
            }

            wb.write(out);
        }
    }
}