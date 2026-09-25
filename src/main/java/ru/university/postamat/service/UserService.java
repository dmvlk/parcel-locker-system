package ru.university.postamat.service;

import ru.university.postamat.exception.BusinessException;
import ru.university.postamat.exception.EntityNotFoundException;
import ru.university.postamat.model.User;
import ru.university.postamat.repository.DeliveryRequestRepository;
import ru.university.postamat.repository.UserRepository;

import java.util.List;

public class UserService {

    private final UserRepository userRepository = new UserRepository();
    private final DeliveryRequestRepository requestRepository = new DeliveryRequestRepository();

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public User create(User user) {
        validate(user, 0L);
        return userRepository.create(user);
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new BusinessException("Не указан id пользователя");
        }
        getById(user.getId());
        validate(user, user.getId());
        userRepository.update(user);
        return user;
    }

    public void delete(long id) {
        getById(id);
        if (requestRepository.existsByUserId(id)) {
            throw new BusinessException("Нельзя удалить пользователя: с ним связаны заявки (сначала удалите их)");
        }
        userRepository.deleteById(id);
    }

    private void validate(User user, long excludeId) {
        if (isBlank(user.getFullName())) {
            throw new BusinessException("ФИО обязательно (нельзя сохранять запись без обязательного поля)");
        }
        if (isBlank(user.getPhone())) {
            throw new BusinessException("Телефон обязателен");
        }
        if (!user.getPhone().matches("\\+?[0-9\\-() ]{6,20}")) {
            throw new BusinessException("Некорректный формат телефона");
        }
        if (isBlank(user.getEmail())) {
            throw new BusinessException("Email обязателен");
        }
        if (!user.getEmail().matches(".+@.+\\..+")) {
            throw new BusinessException("Некорректный формат email");
        }
        if (userRepository.existsByPhoneOrEmail(user.getPhone(), user.getEmail(), excludeId)) {
            throw new BusinessException("Пользователь с таким телефоном или email уже существует (бизнес-правило №7)");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}