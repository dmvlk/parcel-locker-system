package ru.university.postamat;

import ru.university.postamat.exception.BusinessException;
import ru.university.postamat.model.*;
import ru.university.postamat.service.DeliveryRequestService;
import ru.university.postamat.service.ExportService;
import ru.university.postamat.service.PostamatService;
import ru.university.postamat.service.UserService;
import ru.university.postamat.util.DemoDataManager;
import ru.university.postamat.util.DatabaseManager;
import ru.university.postamat.util.InputHelper;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final UserService userService = new UserService();
    private static final PostamatService postamatService = new PostamatService();
    private static final DeliveryRequestService requestService = new DeliveryRequestService();
    private static final ExportService exportService = new ExportService();

    public static void main(String[] args) {
        DatabaseManager.checkConnection();
        run();
    }

    private static void run() {
        System.out.println("===============================================================");
        System.out.println("   СЕТЬ ПОСТАМАТОВ - консольная информационная система");
        System.out.println("===============================================================");
        while (true) {
            System.out.println("\n=================== ГЛАВНОЕ МЕНЮ ===================");
            System.out.println(" 1. Заявки на доставку (основная сущность)");
            System.out.println(" 2. Пользователи");
            System.out.println(" 3. Постаматы");
            System.out.println(" 4. Статистика");
            System.out.println(" 5. Экспорт данных");
            System.out.println(" 6. Восстановить демо-данные");
            System.out.println(" 0. Выход");
            int choice = InputHelper.readInt("Выберите действие: ", 0, 6);
            try {
                if (choice == 1) showRequestsMenu();
                else if (choice == 2) showUsersMenu();
                else if (choice == 3) showPostamatsMenu();
                else if (choice == 4) showStatistics();
                else if (choice == 5) showExportMenu();
                else if (choice == 6) restoreDemoData();
                else {
                    System.out.println("Работа завершена. До свидания!");
                    return;
                }
            } catch (BusinessException e) {
                System.out.println("ОШИБКА: " + e.getMessage());
            }
        }
    }

    // ==================== МЕНЮ ЗАЯВОК ====================

    private static void showRequestsMenu() {
        while (true) {
            System.out.println("\n============ ЗАЯВКИ НА ДОСТАВКУ ============");
            System.out.println(" 1. Показать все заявки");
            System.out.println(" 2. Найти по id");
            System.out.println(" 3. Найти по трек-номеру");
            System.out.println(" 4. Создать заявку");
            System.out.println(" 5. Изменить заявку");
            System.out.println(" 6. Изменить статус заявки");
            System.out.println(" 7. Удалить заявку");
            System.out.println(" 8. Поиск по заявкам");
            System.out.println(" 9. Фильтрация заявок");
            System.out.println("10. Сортировка заявок");
            System.out.println(" 0. Назад");
            int choice = InputHelper.readInt("Выберите: ", 0, 10);
            if (choice == 0) return;
            try {
                switch (choice) {
                    case 1: printRequests(requestService.findAll()); break;
                    case 2: printRequestDetails(requestService.getById(InputHelper.readLong("id заявки: "))); break;
                    case 3: printRequestDetails(requestService.getByTrackingNumber(
                            InputHelper.readNonEmpty("Трек-номер (например PM-000001): ").toUpperCase())); break;
                    case 4: createRequest(); break;
                    case 5: updateRequest(); break;
                    case 6: changeRequestStatus(); break;
                    case 7: deleteRequest(); break;
                    case 8: searchRequests(); break;
                    case 9: filterRequests(); break;
                    case 10: sortRequests(); break;
                }
            } catch (BusinessException e) {
                System.out.println("ОШИБКА: " + e.getMessage());
            }
            InputHelper.pause();
        }
    }

    private static void createRequest() {
        System.out.println("--- Создание заявки ---");
        DeliveryRequest request = new DeliveryRequest();
        request.setUserId(chooseUser());
        request.setPostamatId(choosePostamat());
        request.setItemDescription(InputHelper.readNonEmpty("Описание товара: "));
        request.setWeightKg(InputHelper.readWeight("Вес, кг (0 < вес <= 30): "));
        request.setSize(chooseSize());
        DeliveryRequest created = requestService.create(request);
        System.out.println("Заявка создана. Трек-номер: " + created.getTrackingNumber());
    }

    private static void updateRequest() {
        long id = InputHelper.readLong("id заявки для изменения: ");
        DeliveryRequest request = requestService.getById(id);
        printRequestDetails(request);
        System.out.println("Введите новые значения (редактировать можно только заявку в статусе \"Создана\"):");
        request.setUserId(chooseUser());
        request.setPostamatId(choosePostamat());
        request.setItemDescription(InputHelper.readNonEmpty("Описание товара: "));
        request.setWeightKg(InputHelper.readWeight("Вес, кг: "));
        request.setSize(chooseSize());
        requestService.update(request);
        System.out.println("Заявка обновлена.");
    }

    private static void changeRequestStatus() {
        long id = InputHelper.readLong("id заявки: ");
        DeliveryRequest request = requestService.getById(id);
        printRequestDetails(request);

        DeliveryStatus current = request.getStatus();
        if (current.isFinal()) {
            System.out.println("Заявка в конечном статусе, переходы невозможны.");
            return;
        }

        // показываем только допустимые переходы (бизнес-правило 4)
        List<DeliveryStatus> allowed = new ArrayList<>();
        for (DeliveryStatus s : DeliveryStatus.values()) {
            if (current.canTransitionTo(s)) {
                allowed.add(s);
            }
        }
        System.out.println("Допустимые переходы из статуса \"" + current.getDisplayName() + "\":");
        for (int i = 0; i < allowed.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + allowed.get(i).getDisplayName());
        }
        DeliveryStatus newStatus = allowed.get(InputHelper.readInt("Выберите: ", 1, allowed.size()) - 1);

        String code = null;
        if (newStatus == DeliveryStatus.DELIVERED) {
            code = InputHelper.readNonEmpty("Введите код получения: ");
        }

        DeliveryRequest updated = requestService.changeStatus(id, newStatus, code);
        System.out.println("Новый статус: " + updated.getStatus().getDisplayName());
        if (newStatus == DeliveryStatus.IN_POSTAMAT) {
            System.out.println("Код получения для клиента: " + updated.getPickupCode());
        }
    }

    private static void deleteRequest() {
        long id = InputHelper.readLong("id заявки: ");
        DeliveryRequest request = requestService.getById(id);
        printRequestDetails(request);
        if (InputHelper.readConfirm("Удалить заявку " + request.getTrackingNumber() + "?")) {
            requestService.delete(id);
            System.out.println("Заявка удалена.");
        }
    }

    private static void searchRequests() {
        System.out.println("Поиск по:");
        System.out.println("1. трек-номеру");
        System.out.println("2. описанию товара");
        System.out.println("3. ФИО получателя");
        System.out.println("4. адресу постамата");
        int choice = InputHelper.readInt("Выберите: ", 1, 4);
        String query = InputHelper.readNonEmpty("Введите текст для поиска: ");
        List<DeliveryRequest> result;
        switch (choice) {
            case 1: result = requestService.search("tracking", query); break;
            case 2: result = requestService.search("item", query); break;
            case 3: result = requestService.search("user", query); break;
            default: result = requestService.search("postamat", query); break;
        }
        printRequests(result);
    }

    private static void filterRequests() {
        List<DeliveryRequest> all = requestService.findAll();
        List<DeliveryRequest> result = new ArrayList<>();
        System.out.println("Фильтр:");
        System.out.println("1. по статусу");
        System.out.println("2. по постамату");
        System.out.println("3. по получателю");
        System.out.println("4. по дате создания (период)");
        System.out.println("5. по весу (от-до)");
        int choice = InputHelper.readInt("Выберите: ", 1, 5);
        switch (choice) {
            case 1: {
                DeliveryStatus status = chooseStatus();
                for (DeliveryRequest r : all) {
                    if (r.getStatus() == status) result.add(r);
                }
                break;
            }
            case 2: {
                long postamatId = choosePostamat();
                for (DeliveryRequest r : all) {
                    if (r.getPostamatId() == postamatId) result.add(r);
                }
                break;
            }
            case 3: {
                long userId = chooseUser();
                for (DeliveryRequest r : all) {
                    if (r.getUserId() == userId) result.add(r);
                }
                break;
            }
            case 4: {
                LocalDate from = InputHelper.readDate("Начало периода (ДД.ММ.ГГГГ): ");
                LocalDate to = InputHelper.readDate("Конец периода (ДД.ММ.ГГГГ): ");
                for (DeliveryRequest r : all) {
                    LocalDate d = r.getCreatedAt().toLocalDate();
                    if (!d.isBefore(from) && !d.isAfter(to)) result.add(r);
                }
                break;
            }
            default: {
                BigDecimal from = InputHelper.readWeight("Вес от, кг: ");
                BigDecimal to = InputHelper.readWeight("Вес до, кг: ");
                for (DeliveryRequest r : all) {
                    if (r.getWeightKg().compareTo(from) >= 0 && r.getWeightKg().compareTo(to) <= 0) result.add(r);
                }
                break;
            }
        }
        printRequests(result);
    }

    private static void sortRequests() {
        // сортировка через Stream API (по примеру из методички)
        List<DeliveryRequest> all = requestService.findAll();
        System.out.println("Сортировка:");
        System.out.println("1. по дате создания (старые сверху)");
        System.out.println("2. по дате создания (новые сверху)");
        System.out.println("3. по статусу");
        System.out.println("4. по весу (тяжелые сверху)");
        System.out.println("5. по ФИО получателя");
        int choice = InputHelper.readInt("Выберите: ", 1, 5);
        List<DeliveryRequest> result;
        switch (choice) {
            case 1:
                result = all.stream().sorted(Comparator.comparing(DeliveryRequest::getCreatedAt))
                        .collect(java.util.stream.Collectors.toList());
                break;
            case 2:
                result = all.stream().sorted(Comparator.comparing(DeliveryRequest::getCreatedAt).reversed())
                        .collect(java.util.stream.Collectors.toList());
                break;
            case 3:
                result = all.stream().sorted(Comparator.comparing(r -> r.getStatus().ordinal()))
                        .collect(java.util.stream.Collectors.toList());
                break;
            case 4:
                result = all.stream().sorted(Comparator.comparing(DeliveryRequest::getWeightKg).reversed())
                        .collect(java.util.stream.Collectors.toList());
                break;
            default:
                result = all.stream().sorted(Comparator.comparing(DeliveryRequest::getUserFullName))
                        .collect(java.util.stream.Collectors.toList());
                break;
        }
        printRequests(result);
    }

    // ==================== МЕНЮ ПОЛЬЗОВАТЕЛЕЙ ====================

    private static void showUsersMenu() {
        while (true) {
            System.out.println("\n============ ПОЛЬЗОВАТЕЛИ ============");
            System.out.println("1. Показать всех");
            System.out.println("2. Найти по id");
            System.out.println("3. Создать");
            System.out.println("4. Изменить");
            System.out.println("5. Удалить");
            System.out.println("0. Назад");
            int choice = InputHelper.readInt("Выберите: ", 0, 5);
            if (choice == 0) return;
            try {
                switch (choice) {
                    case 1: printUsers(); break;
                    case 2: System.out.println(userService.getById(InputHelper.readLong("id пользователя: "))); break;
                    case 3: {
                        User user = new User(
                                InputHelper.readNonEmpty("ФИО: "),
                                InputHelper.readNonEmpty("Телефон: "),
                                InputHelper.readNonEmpty("Email: "));
                        User created = userService.create(user);
                        System.out.println("Создан пользователь id=" + created.getId());
                        break;
                    }
                    case 4: {
                        User user = userService.getById(InputHelper.readLong("id пользователя: "));
                        System.out.println("Текущие данные: " + user);
                        user.setFullName(InputHelper.readNonEmpty("Новое ФИО: "));
                        user.setPhone(InputHelper.readNonEmpty("Новый телефон: "));
                        user.setEmail(InputHelper.readNonEmpty("Новый email: "));
                        userService.update(user);
                        System.out.println("Пользователь обновлен.");
                        break;
                    }
                    case 5: {
                        long id = InputHelper.readLong("id пользователя: ");
                        User user = userService.getById(id);
                        if (InputHelper.readConfirm("Удалить пользователя " + user.getFullName() + "?")) {
                            userService.delete(id);
                            System.out.println("Пользователь удален.");
                        }
                        break;
                    }
                }
            } catch (BusinessException e) {
                System.out.println("ОШИБКА: " + e.getMessage());
            }
            InputHelper.pause();
        }
    }

    private static void printUsers() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            System.out.println("Пользователей нет.");
            return;
        }
        System.out.printf("%-4s %-30s %-20s %-28s %-14s%n", "id", "ФИО", "Телефон", "Email", "Регистрация");
        System.out.println("-".repeat(98));
        for (User u : users) {
            System.out.printf("%-4d %-30s %-20s %-28s %-14s%n",
                    u.getId(), u.getFullName(), u.getPhone(), u.getEmail(),
                    u.getRegisteredAt().format(DATE));
        }
        System.out.println("Итого: " + users.size());
    }

    // ==================== МЕНЮ ПОСТАМАТОВ ====================

    private static void showPostamatsMenu() {
        while (true) {
            System.out.println("\n============ ПОСТАМАТЫ ============");
            System.out.println("1. Показать все");
            System.out.println("2. Найти по id");
            System.out.println("3. Создать");
            System.out.println("4. Изменить");
            System.out.println("5. Удалить");
            System.out.println("6. Изменить статус");
            System.out.println("0. Назад");
            int choice = InputHelper.readInt("Выберите: ", 0, 6);
            if (choice == 0) return;
            try {
                switch (choice) {
                    case 1: printPostamats(); break;
                    case 2: System.out.println(postamatService.getById(InputHelper.readLong("id постамата: "))); break;
                    case 3: {
                        String address = InputHelper.readNonEmpty("Адрес: ");
                        int capacity = InputHelper.readInt("Количество ячеек (1-1000): ", 1, 1000);
                        Postamat created = postamatService.create(new Postamat(address, capacity));
                        System.out.println("Постамат создан, id=" + created.getId());
                        break;
                    }
                    case 4: {
                        Postamat postamat = postamatService.getById(InputHelper.readLong("id постамата: "));
                        System.out.println("Текущие данные: " + postamat);
                        postamat.setAddress(InputHelper.readNonEmpty("Новый адрес: "));
                        postamat.setCapacity(InputHelper.readInt("Новое количество ячеек (1-1000): ", 1, 1000));
                        postamatService.update(postamat);
                        System.out.println("Постамат обновлен (свободных ячеек: " + postamat.getFreeCells() + ").");
                        break;
                    }
                    case 5: {
                        long id = InputHelper.readLong("id постамата: ");
                        if (InputHelper.readConfirm("Удалить постамат id=" + id + "?")) {
                            postamatService.delete(id);
                            System.out.println("Постамат удален.");
                        }
                        break;
                    }
                    case 6: {
                        Postamat postamat = postamatService.getById(InputHelper.readLong("id постамата: "));
                        System.out.println("Текущий статус: " + postamat.getStatus().getDisplayName());
                        PostamatStatus[] statuses = PostamatStatus.values();
                        for (int i = 0; i < statuses.length; i++) {
                            System.out.println("  " + (i + 1) + ". " + statuses[i].getDisplayName());
                        }
                        postamat.setStatus(statuses[InputHelper.readInt("Выберите: ", 1, statuses.length) - 1]);
                        postamatService.update(postamat);
                        System.out.println("Статус обновлен.");
                        break;
                    }
                }
            } catch (BusinessException e) {
                System.out.println("ОШИБКА: " + e.getMessage());
            }
            InputHelper.pause();
        }
    }

    private static void printPostamats() {
        List<Postamat> postamats = postamatService.findAll();
        if (postamats.isEmpty()) {
            System.out.println("Постаматов нет.");
            return;
        }
        System.out.printf("%-4s %-42s %-9s %-8s %-14s%n", "id", "Адрес", "Свободно", "Всего", "Статус");
        System.out.println("-".repeat(80));
        for (Postamat p : postamats) {
            System.out.printf("%-4d %-42s %-9d %-8d %-14s%n",
                    p.getId(), cut(p.getAddress(), 42), p.getFreeCells(), p.getCapacity(),
                    p.getStatus().getDisplayName());
        }
    }

    // ==================== СТАТИСТИКА И ЭКСПОРТ ====================

    private static void showStatistics() {
        List<DeliveryRequest> requests = requestService.findAll();
        List<Postamat> postamats = postamatService.findAll();

        System.out.println("======================================================================");
        System.out.println("               СТАТИСТИКА СИСТЕМЫ \"СЕТЬ ПОСТАМАТОВ\"");
        System.out.println("======================================================================");
        System.out.println("Всего пользователей:  " + userService.findAll().size());

        int activePostamats = 0;
        for (Postamat p : postamats) {
            if (p.getStatus() == PostamatStatus.ACTIVE) activePostamats++;
        }
        System.out.println("Всего постаматов:     " + postamats.size() + " (активных: " + activePostamats + ")");
        System.out.println("Всего заявок:         " + requests.size());
        System.out.println("------------------------------------------------------------------");
        System.out.println("Заявки по статусам:");
        for (DeliveryStatus s : DeliveryStatus.values()) {
            int count = 0;
            for (DeliveryRequest r : requests) {
                if (r.getStatus() == s) count++;
            }
            System.out.printf("   %-26s %d%n", s.getDisplayName() + ":", count);
        }
        System.out.println("------------------------------------------------------------------");

        // средний вес
        double sumWeight = 0;
        for (DeliveryRequest r : requests) {
            sumWeight += r.getWeightKg().doubleValue();
        }
        if (!requests.isEmpty()) {
            System.out.printf("Средний вес посылки:   %.2f кг%n", sumWeight / requests.size());
        }

        // доля доставленных и отмененных
        int delivered = 0;
        int canceled = 0;
        long sumDeliveryMinutes = 0;
        for (DeliveryRequest r : requests) {
            if (r.getStatus() == DeliveryStatus.DELIVERED) {
                delivered++;
                if (r.getDeliveredAt() != null) {
                    sumDeliveryMinutes += java.time.temporal.ChronoUnit.MINUTES.between(
                            r.getCreatedAt(), r.getDeliveredAt());
                }
            }
            if (r.getStatus() == DeliveryStatus.CANCELED) canceled++;
        }
        if (!requests.isEmpty()) {
            System.out.printf("Доля доставленных:     %.1f%%%n", delivered * 100.0 / requests.size());
            System.out.printf("Доля отмененных:       %.1f%%%n", canceled * 100.0 / requests.size());
            if (delivered > 0) {
                System.out.printf("Средний срок доставки: %.1f дн.%n",
                        sumDeliveryMinutes / 60.0 / 24.0 / delivered);
            }
        }

        // самый загруженный постамат (по доле занятых ячеек)
        Postamat busiest = null;
        double bestLoad = -1;
        for (Postamat p : postamats) {
            double load = (double) (p.getCapacity() - p.getFreeCells()) / p.getCapacity();
            if (load > bestLoad) {
                bestLoad = load;
                busiest = p;
            }
        }
        if (busiest != null) {
            System.out.printf("Самый загруженный постамат: id=%d, %s (занято %.0f%% ячеек)%n",
                    busiest.getId(), busiest.getAddress(), bestLoad * 100);
        }

        // топ-3 получателя по числу заявок
        Map<String, Integer> countByUser = new HashMap<>();
        for (DeliveryRequest r : requests) {
            Integer count = countByUser.get(r.getUserFullName());
            if (count == null) count = 0;
            countByUser.put(r.getUserFullName(), count + 1);
        }
        List<Map.Entry<String, Integer>> top = new ArrayList<>(countByUser.entrySet());
        top.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        System.out.print("Топ-3 получателя: ");
        for (int i = 0; i < Math.min(3, top.size()); i++) {
            System.out.print((i + 1) + ") " + top.get(i).getKey() + " - " + top.get(i).getValue() + " заявок ");
        }
        System.out.println();
        System.out.println("======================================================================");
        InputHelper.pause();
    }

    private static void showExportMenu() {
        while (true) {
            System.out.println("\n============ ЭКСПОРТ ДАННЫХ ============");
            System.out.println("1. Экспорт в Excel (.xlsx)");
            System.out.println("2. Экспорт в CSV");
            System.out.println("0. Назад");
            int choice = InputHelper.readInt("Выберите: ", 0, 2);
            if (choice == 0) return;
            try {
                Path file;
                if (choice == 1) {
                    file = exportService.exportToExcel(requestService.findAll(), null);
                } else {
                    file = exportService.exportToCsv(requestService.findAll());
                }
                System.out.println("Файл сохранен: " + file.toAbsolutePath());
            } catch (BusinessException e) {
                System.out.println("ОШИБКА: " + e.getMessage());
            }
            InputHelper.pause();
        }
    }

    private static void restoreDemoData() {
        System.out.println("ВНИМАНИЕ: все данные будут удалены и заменены демонстрационными.");
        if (InputHelper.readConfirm("Продолжить?")) {
            DemoDataManager.restore();
            System.out.println("Демо-данные восстановлены: 6 пользователей, 4 постамата, 15 заявок.");
        }
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private static long chooseUser() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            throw new BusinessException("Сначала создайте хотя бы одного пользователя");
        }
        System.out.println("Выберите получателя:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + users.get(i).getFullName() + " (" + users.get(i).getPhone() + ")");
        }
        return users.get(InputHelper.readInt("Номер: ", 1, users.size()) - 1).getId();
    }

    private static long choosePostamat() {
        List<Postamat> postamats = postamatService.findAll();
        if (postamats.isEmpty()) {
            throw new BusinessException("Сначала создайте хотя бы один постамат");
        }
        System.out.println("Выберите постамат:");
        for (int i = 0; i < postamats.size(); i++) {
            Postamat p = postamats.get(i);
            System.out.println("  " + (i + 1) + ". " + p.getAddress() + " | свободно: " + p.getFreeCells()
                    + " из " + p.getCapacity() + " | " + p.getStatus().getDisplayName());
        }
        return postamats.get(InputHelper.readInt("Номер: ", 1, postamats.size()) - 1).getId();
    }

    private static DeliverySize chooseSize() {
        DeliverySize[] sizes = DeliverySize.values();
        System.out.println("Габарит посылки:");
        for (int i = 0; i < sizes.length; i++) {
            System.out.println("  " + (i + 1) + ". " + sizes[i].getDisplayName());
        }
        return sizes[InputHelper.readInt("Выберите: ", 1, sizes.length) - 1];
    }

    private static DeliveryStatus chooseStatus() {
        DeliveryStatus[] statuses = DeliveryStatus.values();
        System.out.println("Статус:");
        for (int i = 0; i < statuses.length; i++) {
            System.out.println("  " + (i + 1) + ". " + statuses[i].getDisplayName());
        }
        return statuses[InputHelper.readInt("Выберите: ", 1, statuses.length) - 1];
    }

    private static void printRequests(List<DeliveryRequest> list) {
        if (list.isEmpty()) {
            System.out.println("Заявок не найдено.");
            return;
        }
        System.out.println("-".repeat(142));
        System.out.printf("%-3s %-10s %-22s %-26s %-30s %-7s %-7s %-7s %-16s%n",
                "id", "Трек", "Статус", "Получатель", "Постамат", "Вес", "Габарит", "Код", "Создана");
        System.out.println("-".repeat(142));
        for (DeliveryRequest r : list) {
            System.out.printf("%-3d %-10s %-22s %-26s %-30s %-7s %-7s %-7s %-16s%n",
                    r.getId(), r.getTrackingNumber(), r.getStatus().getDisplayName(),
                    cut(r.getUserFullName(), 26), cut(r.getPostamatAddress(), 30),
                    r.getWeightKg(), r.getSize(),
                    r.getPickupCode() == null ? "-" : r.getPickupCode(),
                    r.getCreatedAt().format(DATE_TIME));
        }
        System.out.println("-".repeat(142));
        System.out.println("Итого: " + list.size());
    }

    private static void printRequestDetails(DeliveryRequest r) {
        System.out.println("=".repeat(70));
        System.out.println("Заявка " + r.getTrackingNumber() + " (id=" + r.getId() + ")");
        System.out.println("Статус:        " + r.getStatus().getDisplayName());
        System.out.println("Получатель:    " + r.getUserFullName() + " (id=" + r.getUserId() + ")");
        System.out.println("Постамат:      " + r.getPostamatAddress() + " (id=" + r.getPostamatId() + ")");
        System.out.println("Товар:         " + r.getItemDescription());
        System.out.println("Вес:           " + r.getWeightKg() + " кг, габарит: " + r.getSize());
        System.out.println("Код получения: " + (r.getPickupCode() == null ? "-" : r.getPickupCode()));
        System.out.println("Создана:       " + r.getCreatedAt().format(DATE_TIME));
        System.out.println("Выдана:        " + (r.getDeliveredAt() == null ? "-" : r.getDeliveredAt().format(DATE_TIME)));
        System.out.println("=".repeat(70));
    }

    private static String cut(String s, int len) {
        if (s.length() <= len) return s;
        return s.substring(0, len - 1) + "…";
    }
}