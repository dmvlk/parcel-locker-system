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
    List<User> get_by_full_name(String fullName);
    List<User> get_by_email(String email);
    User get_by_phone_exact(String phone);
    User get_by_email_exact(String email);

    boolean phone_exists(String phone);
    boolean phone_exists_for_other(String phone, int excludeId);
    boolean email_exists(String email);
    boolean email_exists_for_other(String email, int excludeId);

    List<User> sort_by_full_name();
    List<User> sort_by_phone();
}