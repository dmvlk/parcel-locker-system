package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.Parcel;
import java.util.List;

public interface ParcelRepo {

    List<Parcel> getAll();
    Parcel getById(int id);
    void save(Parcel parcel);
    void update(Parcel parcel);
    void delete(int id);

    Parcel getByCode(String code);
    boolean codeExists(String code);
}
