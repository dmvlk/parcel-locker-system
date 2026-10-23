package ru.mirea.postamat.service;

import ru.mirea.postamat.exception.BusinessException;
import ru.mirea.postamat.exception.NotFoundException;
import ru.mirea.postamat.model.*;
import ru.mirea.postamat.repository.CellRepo;
import ru.mirea.postamat.repository.ParcelRepo;
import ru.mirea.postamat.repository.UserRepo;

import java.util.Random;

public class ParcelService {

    private final ParcelRepo parcelRepo;
    private final CellRepo cellRepo;
    private final UserRepo userRepo;
    private final Random random = new Random();

    public ParcelService(ParcelRepo parcelRepo, CellRepo cellRepo, UserRepo userRepo) {
        this.parcelRepo = parcelRepo;
        this.cellRepo = cellRepo;
        this.userRepo = userRepo;
    }

    public String put_parcel(String phone, SizeType size, String lockerAddress) {

        if (phone == null || phone.trim().isEmpty())
            throw new BusinessException("Телефон не может быть пустым");
        if (lockerAddress == null || lockerAddress.trim().isEmpty())
            throw new BusinessException("Адрес постамата не может быть пустым");

        User user = userRepo.getByPhoneExact(phone.trim());
        if (user == null)
            throw new NotFoundException("Пользователь с телефоном " + phone + " не найден");

        Cell cell = cellRepo.findFreeCell(lockerAddress.trim(), size);
        if (cell == null)
            throw new BusinessException("Нет свободных ячеек размера " + size + " в постамате " + lockerAddress);

        cell.setStatus(CellStatus.OCCUPIED);
        cellRepo.update(cell);

        String code = generateCode();
        Parcel parcel = new Parcel(code, user.getId(), cell.getId(), ParcelStatus.WAITING);
        parcelRepo.save(parcel);
        return code;
    }

    public Parcel takeParcel(String code) {
        if (code == null || code.trim().isEmpty())
            throw new BusinessException("Код не может быть пустым");

        Parcel parcel = parcelRepo.getByCode(code.trim());
        if (parcel == null)
            throw new NotFoundException("Посылка с кодом " + code + " не найдена");

        if (parcel.getStatus() == ParcelStatus.PICKED_UP)
            throw new BusinessException("Эта посылка уже получена");

        if (parcel.getCellId() != null) {
            Cell cell = cellRepo.getById(parcel.getCellId());
            if (cell != null) {
                cell.setStatus(CellStatus.FREE);
                cellRepo.update(cell);
            }
        }
        parcel.setStatus(ParcelStatus.PICKED_UP);
        parcel.setCellId(null);
        parcelRepo.update(parcel);
        return parcel;
    }

    private String generateCode() {
        while (true) {
            int num = 10000 + random.nextInt(90000);
            String code = String.valueOf(num);
            if (!parcelRepo.codeExists(code)) return code;
        }
    }
}