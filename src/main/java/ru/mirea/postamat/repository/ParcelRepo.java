package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.Parcel;
import java.util.List;

public interface ParcelRepo {

    List<Parcel> get_all();
    Parcel get_by_id(int id);
    void save(Parcel parcel);
    void update(Parcel parcel);
    void delete(int id);

    Parcel get_by_code(String code);
    boolean code_exists(String code);
}
