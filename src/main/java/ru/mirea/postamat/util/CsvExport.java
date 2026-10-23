package ru.mirea.postamat.util;

import ru.mirea.postamat.model.Cell;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class CsvExport {
    public static void exportCells(List<Cell> cells, String path) throws IOException {
        try (Writer w = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), StandardCharsets.UTF_8))) {
            w.write("ID;Адрес;Номер;Размер;Статус\n");
            for (Cell c : cells) {
                w.write(c.getId() + ";" + esc(c.getLockerAddress()) + ";"
                        + c.getCellNumber() + ";" + c.getSizeType() + ";"
                        + c.getStatus() + "\n");
            }
        }
    }
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace(";", ",").replace("\n", " ");
    }
    private CsvExport() {}
}