package ru.mirea.postamat.service;

import ru.mirea.postamat.exception.BusinessException;
import ru.mirea.postamat.exception.NotFoundException;
import ru.mirea.postamat.model.User;
import ru.mirea.postamat.repository.UserRepo;
import java.util.List;

public class UserService {

    private final UserRepo user_repo;

    public UserService(UserRepo user_repo) {
        this.user_repo = user_repo;
    }


    public List<User> get_all() {
        return user_repo.get_all();
    }

    public User get_by_id(int id) {
        User user = user_repo.get_by_id(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID=" + id + " не найден");
        }
        return user;
    }

    public void create(String full_name, String phone, String email) {
        if (full_name == null || full_name.trim().isEmpty()) {
            throw new BusinessException("ФИО не может быть пустым");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new BusinessException("Телефон не может быть пустым");
        }
        user_repo.save(new User(full_name.trim(), phone.trim(), email));
    }

    public void update(int id, String full_name, String phone, String email) {
        if (full_name == null || full_name.trim().isEmpty()) {
            throw new BusinessException("ФИО не может быть пустым");
        }
        User user = get_by_id(id);
        user.set_full_name(full_name.trim());
        user.set_phone(phone.trim());
        user.set_email(email);
        user_repo.update(user);
    }

    public void delete(int id) {
        get_by_id(id);
        user_repo.delete(id);
    }

    public List<User> search_by_phone(String phone) {
        return user_repo.get_by_phone(phone);
    }
}