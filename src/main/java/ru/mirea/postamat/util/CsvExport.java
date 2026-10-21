package ru.mirea.postamat.util;

import ru.mirea.postamat.model.Cell;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class CsvExport {
    public static void export_cells(List<Cell> cells, String path) throws IOException {
        try (Writer w = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), StandardCharsets.UTF_8))) {
            w.write("ID;Адрес;Номер;Размер;Статус\n");
            for (Cell c : cells) {
                w.write(c.get_id() + ";" + esc(c.get_locker_address()) + ";"
                        + c.get_cell_number() + ";" + c.get_size_type() + ";"
                        + c.get_status() + "\n");
            }
        }
    }
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace(";", ",").replace("\n", " ");
    }
    private CsvExport() {}
}