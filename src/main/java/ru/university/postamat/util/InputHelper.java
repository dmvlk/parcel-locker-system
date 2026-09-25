package ru.university.postamat.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Ввод данных с консоли с проверками, чтобы программа не падала при неправильном вводе. */
public class InputHelper {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // целое число в диапазоне от min до max (для меню)
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Ошибка: введите число от " + min + " до " + max);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: \"" + line + "\" - не целое число");
            }
        }
    }

    public static long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: \"" + line + "\" - не целое число");
            }
        }
    }

    // непустая строка
    public static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Ошибка: строка не может быть пустой");
        }
    }

    // вес: положительное число, запятую заменяем на точку
    public static BigDecimal readWeight(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim().replace(',', '.');
            try {
                BigDecimal weight = new BigDecimal(line);
                if (weight.signum() > 0) {
                    return weight;
                }
                System.out.println("Ошибка: вес должен быть больше нуля");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: \"" + line + "\" - не число, пример: 2.5");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return LocalDate.parse(line, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: дата вводится в формате ДД.ММ.ГГГГ, например 05.02.2026");
            }
        }
    }

    public static boolean readConfirm(String prompt) {
        while (true) {
            System.out.print(prompt + " (да/нет): ");
            String line = SCANNER.nextLine().trim().toLowerCase();
            if (line.equals("да")) return true;
            if (line.equals("нет")) return false;
            System.out.println("Ошибка: введите \"да\" или \"нет\"");
        }
    }

    public static void pause() {
        System.out.print("\nНажмите Enter, чтобы продолжить...");
        SCANNER.nextLine();
    }
}