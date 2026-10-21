package ru.mirea.postamat.service;

import ru.mirea.postamat.model.*;
import ru.mirea.postamat.repository.*;
import java.util.*;
import java.util.stream.Collectors;

public class StatsService {
    private final UserRepo userRepo;
    private final CellRepo cellRepo;
    private final ParcelRepo parcelRepo;

    public StatsService(UserRepo u, CellRepo c, ParcelRepo p) {
        this.userRepo = u; this.cellRepo = c; this.parcelRepo = p;
    }

    public String buildReport() {
        List<User> users = userRepo.get_all();
        List<Cell> cells = cellRepo.get_all();
        List<Parcel> parcels = parcelRepo.get_all();

        long totalCells = cells.size();
        long free    = cells.stream().filter(c -> c.get_status() == CellStatus.FREE).count();
        long occupied= cells.stream().filter(c -> c.get_status() == CellStatus.OCCUPIED).count();
        long broken  = cells.stream().filter(c -> c.get_status() == CellStatus.NOT_WORKING).count();

        double occupancyPercent = totalCells == 0 ? 0 : 100.0 * occupied / totalCells;

        Map<String, Long> cellsByAddress = cells.stream().collect(Collectors.groupingBy(Cell::get_locker_address, Collectors.counting()));

        Map<SizeType, Long> cellsBySize = cells.stream().collect(Collectors.groupingBy(Cell::get_size_type, Collectors.counting()));

        long waiting = parcels.stream().filter(p -> p.get_status() == ParcelStatus.WAITING).count();
        long picked = parcels.stream().filter(p -> p.get_status() == ParcelStatus.PICKED_UP).count();

        Optional<String> maxAddress = cellsByAddress.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);

        StringBuilder sb = new StringBuilder();
        sb.append("1. Всего пользователей: ").append(users.size()).append('\n');
        sb.append("2. Всего ячеек: ").append(totalCells).append('\n');
        sb.append("3. Свободных / занятых / нерабочих: ").append(free).append(" / ").append(occupied).append(" / ").append(broken).append('\n');
        sb.append("4. Процент занятости: ").append(String.format("%.1f", occupancyPercent)).append("%\n");
        sb.append("5. Ячеек по адресам:\n");
        cellsByAddress.forEach((a, n) -> sb.append("   • ").append(a).append(" - ").append(n).append('\n'));
        sb.append("6. Ячеек по размеру: ").append(cellsBySize).append('\n');
        sb.append("7. Посылки: ожидают получения — ").append(waiting).append(", получены — ").append(picked).append('\n');
        maxAddress.ifPresent(a -> sb.append("8. Больше всего ячеек в: ").append(a).append('\n'));

        return sb.toString();
    }
}