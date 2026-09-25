package ru.university.postamat.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.university.postamat.exception.BusinessException;
import ru.university.postamat.model.DeliveryRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Экспорт данных: основной формат — Excel (.xlsx, Apache POI), дополнительный — CSV. */
public class ExportService {

    private static final Path EXPORT_DIR = Path.of("export");
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final String[] HEADERS = {
            "Трек-номер", "Получатель", "Постамат", "Товар", "Вес, кг", "Габарит",
            "Статус", "Код получения", "Создана", "Выдана"
    };

    /** Excel с двумя листами: «Заявки» и «Статистика». */
    public Path exportToExcel(List<DeliveryRequest> requests, List<String> statisticsLines) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Заявки");
            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                header.createCell(i).setCellValue(HEADERS[i]);
            }
            int rowNum = 1;
            for (DeliveryRequest r : requests) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getTrackingNumber());
                row.createCell(1).setCellValue(r.getUserFullName());
                row.createCell(2).setCellValue(r.getPostamatAddress());
                row.createCell(3).setCellValue(r.getItemDescription());
                row.createCell(4).setCellValue(r.getWeightKg().doubleValue());
                row.createCell(5).setCellValue(r.getSize().name());
                row.createCell(6).setCellValue(r.getStatus().getDisplayName());
                row.createCell(7).setCellValue(r.getPickupCode() == null ? "" : r.getPickupCode());
                row.createCell(8).setCellValue(r.getCreatedAt().format(DT));
                row.createCell(9).setCellValue(r.getDeliveredAt() == null ? "" : r.getDeliveredAt().format(DT));
            }
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            Sheet statsSheet = workbook.createSheet("Статистика");
            int i = 0;
            for (String line : statisticsLines) {
                statsSheet.createRow(i++).createCell(0).setCellValue(line);
            }
            statsSheet.autoSizeColumn(0);

            Files.createDirectories(EXPORT_DIR);
            Path file = EXPORT_DIR.resolve("postamat_export_" + LocalDateTime.now().format(FILE_TS) + ".xlsx");
            try (OutputStream out = Files.newOutputStream(file)) {
                workbook.write(out);
            }
            return file;
        } catch (IOException e) {
            throw new BusinessException("Не удалось записать Excel-файл: " + e.getMessage());
        }
    }

    /** CSV с разделителем «;» и экранированием кавычек. */
    public Path exportToCsv(List<DeliveryRequest> requests) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(";", HEADERS)).append("\n");
        for (DeliveryRequest r : requests) {
            sb.append(csv(r.getTrackingNumber())).append(';')
                    .append(csv(r.getUserFullName())).append(';')
                    .append(csv(r.getPostamatAddress())).append(';')
                    .append(csv(r.getItemDescription())).append(';')
                    .append(r.getWeightKg()).append(';')
                    .append(r.getSize()).append(';')
                    .append(csv(r.getStatus().getDisplayName())).append(';')
                    .append(r.getPickupCode() == null ? "" : r.getPickupCode()).append(';')
                    .append(r.getCreatedAt().format(DT)).append(';')
                    .append(r.getDeliveredAt() == null ? "" : r.getDeliveredAt().format(DT))
                    .append('\n');
        }
        try {
            Files.createDirectories(EXPORT_DIR);
            Path file = EXPORT_DIR.resolve("postamat_export_" + LocalDateTime.now().format(FILE_TS) + ".csv");
            Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
            return file;
        } catch (IOException e) {
            throw new BusinessException("Не удалось записать CSV-файл: " + e.getMessage());
        }
    }

    private static String csv(String value) {
        if (value == null) return "";
        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }
}