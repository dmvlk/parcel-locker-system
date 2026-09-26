package ru.mirea.postamat.service;

import ru.mirea.postamat.exception.BusinessException;
import ru.mirea.postamat.exception.NotFoundException;
import ru.mirea.postamat.model.*;
import ru.mirea.postamat.repository.CellRepo;
import ru.mirea.postamat.repository.ParcelRepo;
import ru.mirea.postamat.repository.UserRepo;
import java.util.Random;

public class ParcelService {

    private final ParcelRepo parcel_repo;
    private final CellRepo cell_repo;
    private final UserRepo user_repo;
    private final Random random = new Random();

    public ParcelService(ParcelRepo parcel_repo, CellRepo cell_repo, UserRepo user_repo) {
        this.parcel_repo = parcel_repo;
        this.cell_repo = cell_repo;
        this.user_repo = user_repo;
    }

    public String put_parcel(String phone, SizeType size, String locker_address) {

        if (phone == null || phone.trim().isEmpty()) {
            throw new BusinessException("Телефон не может быть пустым");
        }

        if (locker_address == null || locker_address.trim().isEmpty()) {
            throw new BusinessException("Адрес постамата не может быть пустым");
        }

        var users = user_repo.get_by_phone(phone.trim());
        if (users.isEmpty()) {
            throw new NotFoundException("Пользователь с телефоном " + phone + " не найден");
        }
        User user = users.get(0);

        Cell cell = cell_repo.find_free_cell(locker_address.trim(), size);
        if (cell == null) {
            throw new BusinessException(
                    "Нет свободных ячеек размера " + size + " в постамате " + locker_address);
        }

        cell.set_status(CellStatus.OCCUPIED);
        cell_repo.update(cell);

        String code = generate_code();

        Parcel parcel = new Parcel(code, user.get_id(), cell.get_id(), ParcelStatus.WAITING);
        parcel_repo.save(parcel);

        return code;
    }

    public Parcel take_parcel(String code) {

        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException("Код не может быть пустым");
        }

        Parcel parcel = parcel_repo.get_by_code(code.trim());
        if (parcel == null) {
            throw new NotFoundException("Посылка с кодом " + code + " не найдена");
        }

        if (parcel.get_status() == ParcelStatus.PICKED_UP) {
            throw new BusinessException("Эта посылка уже получена");
        }

        if (parcel.get_cell_id() != null) {
            Cell cell = cell_repo.get_by_id(parcel.get_cell_id());
            if (cell != null) {
                cell.set_status(CellStatus.FREE);
                cell_repo.update(cell);
            }
        }
        parcel.set_status(ParcelStatus.PICKED_UP);
        parcel.set_cell_id(null);
        parcel_repo.update(parcel);

        return parcel;
    }

    private String generate_code() {
        while (true) {
            int num = 10000 + random.nextInt(90000);
            String code = String.valueOf(num);
            if (!parcel_repo.code_exists(code)) {
                return code;
            }
        }
    }
}