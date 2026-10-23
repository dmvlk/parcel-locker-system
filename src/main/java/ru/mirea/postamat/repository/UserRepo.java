package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.User;
import java.util.List;

public interface UserRepo {
    List<User> getAll();
    User getById(int id);
    void save(User user);
    void update(User user);
    void delete(int id);

    List<User> getByPhone(String phone);
    List<User> getByFullName(String fullName);
    List<User> getByEmail(String email);
    User getByPhoneExact(String phone);
    User get_by_email_exact(String email);

    boolean phoneExists(String phone);
    boolean phoneExistsForOther(String phone, int excludeId);
    boolean emailExists(String email);
    boolean emailExistsForOther(String email, int excludeId);

    List<User> sortByFullName();
    List<User> sortByPhone();
}