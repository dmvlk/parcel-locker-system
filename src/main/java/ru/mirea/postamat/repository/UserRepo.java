package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.User;
import java.util.List;

public interface UserRepo {

    List<User> get_all();

    User get_by_id(int id);

    void save(User user);
    void update(User user);
    void delete(int id);

    List<User> get_by_phone(String phone);
}