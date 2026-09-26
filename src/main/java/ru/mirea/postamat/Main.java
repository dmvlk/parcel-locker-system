package ru.mirea.postamat;

import ru.mirea.postamat.exception.BusinessException;
import ru.mirea.postamat.exception.NotFoundException;
import ru.mirea.postamat.model.Cell;
import ru.mirea.postamat.model.CellStatus;
import ru.mirea.postamat.model.Parcel;
import ru.mirea.postamat.model.SizeType;
import ru.mirea.postamat.model.User;
import ru.mirea.postamat.repository.CellRepo;
import ru.mirea.postamat.repository.CellRepoImpl;
import ru.mirea.postamat.repository.ParcelRepo;
import ru.mirea.postamat.repository.ParcelRepoImpl;
import ru.mirea.postamat.repository.UserRepo;
import ru.mirea.postamat.repository.UserRepoImpl;
import ru.mirea.postamat.service.CellService;
import ru.mirea.postamat.service.ParcelService;
import ru.mirea.postamat.service.UserService;
import ru.mirea.postamat.util.ExcelExport;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final UserRepo user_repo = new UserRepoImpl();
    private static final CellRepo cell_repo = new CellRepoImpl();
    private static final ParcelRepo parcel_repo = new ParcelRepoImpl();

    private static final UserService user_service = new UserService(user_repo);
    private static final CellService cell_service = new CellService(cell_repo);
    private static final ParcelService parcel_service =
            new ParcelService(parcel_repo, cell_repo, user_repo);

    public static void main(String[] args) {
        System.out.println("Система управления постаматами запущена.");

        while (true) {
            print_main_menu();
            int choice = read_int("Выберите действие: ");

            try {
                switch (choice) {
                    case 1 -> parcels_menu();
                    case 2 -> users_menu();
                    case 3 -> cells_menu();
                    case 4 -> search_menu();
                    case 5 -> filter_menu();
                    case 6 -> sort_menu();
                    case 7 -> stats_menu();
                    case 8 -> export_menu();
                    case 0 -> {
                        System.out.println("Выход.");
                        return;
                    }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (NotFoundException | BusinessException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
    }

    private static void print_main_menu() {
        System.out.println();
        System.out.println("1. Отправить/получить посылку");
        System.out.println("2. Пользователи");
        System.out.println("3. Ячейки");
        System.out.println("4. Поиск");
        System.out.println("5. Фильтрация");
        System.out.println("6. Сортировка");
        System.out.println("7. Статистика");
        System.out.println("8. Экспорт в Excel");
        System.out.println("0. Выход");
    }


    private static void users_menu() {
        while (true) {
            System.out.println();
            System.out.println("1. Показать всех");
            System.out.println("2. Найти по ID");
            System.out.println("3. Добавить");
            System.out.println("4. Изменить");
            System.out.println("5. Удалить");
            System.out.println("0. Назад");

            int choice = read_int("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> print_users(user_service.get_all());
                    case 2 -> {
                        int id = read_int("Введите ID: ");
                        print_users(List.of(user_service.get_by_id(id)));
                    }
                    case 3 -> {
                        String full_name = read_line("ФИО: ");
                        String phone = read_line("Телефон: ");
                        String email = read_line("Email: ");
                        user_service.create(full_name, phone, email);
                        System.out.println("Пользователь добавлен.");
                    }
                    case 4 -> {
                        int id = read_int("ID пользователя: ");
                        String full_name = read_line("Новое ФИО: ");
                        String phone = read_line("Новый телефон: ");
                        String email = read_line("Новый email: ");
                        user_service.update(id, full_name, phone, email);
                        System.out.println("Обновлено.");
                    }
                    case 5 -> {
                        int id = read_int("ID для удаления: ");
                        user_service.delete(id);
                        System.out.println("Удалено.");
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (NotFoundException | BusinessException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
    }

    private static void cells_menu() {
        while (true) {
            System.out.println();
            System.out.println("1. Показать все");
            System.out.println("2. Найти по ID");
            System.out.println("3. Добавить");
            System.out.println("4. Изменить");
            System.out.println("5. Удалить");
            System.out.println("0. Назад");

            int choice = read_int("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> print_cells(cell_service.get_all());
                    case 2 -> {
                        int id = read_int("Введите ID: ");
                        print_cells(List.of(cell_service.get_by_id(id)));
                    }
                    case 3 -> {
                        String address = read_line("Адрес постамата: ");
                        int number = read_int("Номер ячейки: ");
                        SizeType size = read_size_type();
                        CellStatus status = read_cell_status();
                        cell_service.create(address, number, size, status);
                        System.out.println("Ячейка добавлена.");
                    }
                    case 4 -> {
                        int id = read_int("ID ячейки: ");
                        String address = read_line("Новый адрес: ");
                        int number = read_int("Новый номер: ");
                        SizeType size = read_size_type();
                        CellStatus status = read_cell_status();
                        cell_service.update(id, address, number, size, status);
                        System.out.println("Обновлено.");
                    }
                    case 5 -> {
                        int id = read_int("ID для удаления: ");
                        cell_service.delete(id);
                        System.out.println("Удалено.");
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (NotFoundException | BusinessException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
    }

    private static void parcels_menu() {
        while (true) {
            System.out.println();
            System.out.println("1. Отправить посылку");
            System.out.println("2. Получить посылку");
            System.out.println("0. Назад");

            int choice = read_int("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> {
                        String address = choose_address();
                        String phone = read_line("Телефон получателя: ");
                        SizeType size = read_size_type();
                        String code = parcel_service.put_parcel(phone, size, address);
                        System.out.println("Посылка создана. Код получения: " + code);
                    }
                    case 2 -> {
                        String code = read_line("Код получения: ");
                        Parcel parcel = parcel_service.take_parcel(code);
                        System.out.println("Посылка выдана. ID: " + parcel.get_id());
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (NotFoundException | BusinessException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
    }

    private static void search_menu() {
        while (true) {
            System.out.println();
            System.out.println("1. Ячейки по адресу постамата");
            System.out.println("2. Ячейки по номеру");
            System.out.println("3. Пользователи по телефону");
            System.out.println("0. Назад");

            int choice = read_int("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> {
                        String addr = read_line("Адрес: ");
                        print_cells(cell_service.search_by_address(addr));
                    }
                    case 2 -> {
                        int num = read_int("Номер: ");
                        print_cells(cell_service.search_by_cell_number(num));
                    }
                    case 3 -> {
                        String phone = read_line("Телефон: ");
                        print_users(user_service.search_by_phone(phone));
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void filter_menu() {
        while (true) {
            System.out.println();
            System.out.println("1. По статусу ячейки");
            System.out.println("2. По размеру ячейки");
            System.out.println("0. Назад");

            int choice = read_int("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> {
                        CellStatus s = read_cell_status();
                        print_cells(cell_service.filter_by_status(s));
                    }
                    case 2 -> {
                        SizeType s = read_size_type();
                        print_cells(cell_service.filter_by_size(s));
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void sort_menu() {
        while (true) {
            System.out.println();
            System.out.println("1. По адресу постамата");
            System.out.println("2. По номеру ячейки");
            System.out.println("0. Назад");

            int choice = read_int("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> print_cells(cell_service.sort_by_address());
                    case 2 -> print_cells(cell_service.sort_by_cell_number());
                    case 0 -> { return; }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void stats_menu() {
        try {
            int[] cells = cell_service.get_stats();
            int users = user_service.get_all().size();

            System.out.println();
            System.out.println("Всего пользователей: " + users);
            System.out.println("Всего ячеек: " + cells[0]);
            System.out.println("Свободных: " + cells[1]);
            System.out.println("Занятых: " + cells[2]);
            System.out.println("Не рабочих: " + cells[3]);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void export_menu() {
        try {
            String file = "cells.xlsx";
            ExcelExport.export_cells(cell_service.get_all(), file);
            System.out.println("Экспортировано в " + file);
        } catch (Exception e) {
            System.out.println("Ошибка экспорта: " + e.getMessage());
        }
    }

    private static void print_users(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("Пусто.");
            return;
        }
        System.out.printf("%-5s %-30s %-18s %-25s%n", "ID", "ФИО", "Телефон", "Email");
        for (User u : users) {
            System.out.printf("%-5d %-30s %-18s %-25s%n",
                    u.get_id(), u.get_full_name(), u.get_phone(),
                    u.get_email() == null ? "" : u.get_email());
        }
    }

    private static void print_cells(List<Cell> cells) {
        if (cells.isEmpty()) {
            System.out.println("Пусто.");
            return;
        }
        System.out.printf("%-5s %-20s %-8s %-8s %-12s%n",
                "ID", "Адрес", "Номер", "Размер", "Статус");
        for (Cell c : cells) {
            System.out.printf("%-5d %-20s %-8d %-8s %-12s%n",
                    c.get_id(),
                    c.get_locker_address(),
                    c.get_cell_number(),
                    c.get_size_type(),
                    c.get_status());
        }
    }

    private static int read_int(String prompt) {
        System.out.print(prompt);
        String line = sc.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: нужно целое число.");
            return -1;
        }
    }

    private static String read_line(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static SizeType read_size_type() {
        while (true) {
            System.out.print("Размер (SMALL/MEDIUM/LARGE): ");
            String s = sc.nextLine().trim().toUpperCase();
            try {
                return SizeType.valueOf(s);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: введите SMALL, MEDIUM или LARGE.");
            }
        }
    }

    private static CellStatus read_cell_status() {
        while (true) {
            System.out.print("Статус (FREE/OCCUPIED/NOT_WORKING): ");
            String s = sc.nextLine().trim().toUpperCase();
            try {
                return CellStatus.valueOf(s);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: введите FREE, OCCUPIED или NOT_WORKING.");
            }
        }
    }

    private static String choose_address() {
        List<String> addresses = cell_service.get_all_addresses();
        if (addresses.isEmpty()) {
            throw new BusinessException("Нет ни одного постамата в системе");
        }

        System.out.println("Выберите постамат:");
        for (int i = 0; i < addresses.size(); i++) {
            System.out.println((i + 1) + ". " + addresses.get(i));
        }

        while (true) {
            int choice = read_int("Номер постамата: ");
            if (choice >= 1 && choice <= addresses.size()) {
                return addresses.get(choice - 1);
            }
            System.out.println("Ошибка: введите число от 1 до " + addresses.size());
        }
    }

}