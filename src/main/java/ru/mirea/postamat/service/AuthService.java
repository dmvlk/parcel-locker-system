package ru.mirea.postamat.service;

import ru.mirea.postamat.exception.BusinessException;
import ru.mirea.postamat.model.User;
import ru.mirea.postamat.repository.UserRepo;
import ru.mirea.postamat.util.PasswordUtil;
import ru.mirea.postamat.util.Session;

public class AuthService {
    private final UserRepo userRepo;
    public AuthService(UserRepo userRepo) { this.userRepo = userRepo; }

    public User login(String phone, String password) {
        if (phone == null || phone.isBlank())
            throw new BusinessException("Введите телефон");
        if (password == null || password.isBlank())
            throw new BusinessException("Введите пароль");

        User u = userRepo.getByPhoneExact(phone.trim());
        if (u == null)
            throw new BusinessException("Пользователь не найден");
        if (!u.hasPassword())
            throw new BusinessException("Пароль не задан. Обратитесь к администратору");
        if (!PasswordUtil.verify(password, u.getPasswordHash()))
            throw new BusinessException("Неверный пароль");

        Session.set(u);
        return u;
    }

    public void logout() { Session.set(null); }
}