package ru.mirea.postamat.service;

import ru.mirea.postamat.exception.BusinessException;
import ru.mirea.postamat.exception.NotFoundException;
import ru.mirea.postamat.model.Role;
import ru.mirea.postamat.model.User;
import ru.mirea.postamat.repository.UserRepo;
import ru.mirea.postamat.util.PasswordUtil;

import java.util.List;
import java.util.regex.Pattern;

public class UserService {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAll() {
        return userRepo.getAll();
    }

    public User getById(int id) {
        User user = userRepo.getById(id);
        if (user == null)
            throw new NotFoundException("Пользователь с ID=" + id + " не найден");
        return user;
    }


    public void create(String full_name, String phone, String email, String password, Role role) {
        String name = validate_full_name(full_name);
        String ph   = validate_phone(phone);
        String em   = validate_email(email);
        validate_password(password, true);

        if (userRepo.phoneExists(ph))
            throw new BusinessException("Пользователь с телефоном " + ph + " уже существует");
        if (em != null && userRepo.emailExists(em))
            throw new BusinessException("Пользователь с email " + em + " уже существует");

        User user = new User(
                name,
                ph,
                em,
                PasswordUtil.hash(password),
                role == null ? Role.USER : role
        );
        userRepo.save(user);
    }

    public void create(String full_name, String phone, String email, String password) {
        create(full_name, phone, email, password, Role.USER);
    }

    public void update(int id, String full_name, String phone, String email,
                       String new_password, Role role) {

        User user = getById(id);

        String name = validate_full_name(full_name);
        String ph   = validate_phone(phone);
        String em   = validate_email(email);

        if (userRepo.phoneExistsForOther(ph, id))
            throw new BusinessException("Телефон " + ph + " уже занят другим пользователем");
        if (em != null && userRepo.emailExistsForOther(em, id))
            throw new BusinessException("Email " + em + " уже занят другим пользователем");

        user.setFullName(name);
        user.setPhone(ph);
        user.setEmail(em);

        if (new_password != null && !new_password.isBlank()) {
            validate_password(new_password, false);
            user.setPasswordHash(PasswordUtil.hash(new_password));
        }
        if (role != null) user.setRole(role);
        userRepo.update(user);
    }

    public void update(int id, String full_name, String phone, String email) {
        update(id, full_name, phone, email, null, null);
    }

    public void delete(int id) {
        User user = getById(id);

        if (user.getRole() == Role.ADMIN) {
            long admins = userRepo.getAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN)
                    .count();
            if (admins <= 1) {
                throw new BusinessException(
                        "Нельзя удалить последнего администратора");
            }
        }
        userRepo.delete(id);
    }

    public List<User> search_by_phone(String phone) {
        return userRepo.getByPhone(phone);
    }

    public List<User> search_by_full_name(String full_name) {
        return userRepo.getByFullName(full_name);
    }

    public List<User> search_by_email(String email) {
        return userRepo.getByEmail(email);
    }

    private String validate_full_name(String full_name) {
        if (full_name == null || full_name.trim().isEmpty())
            throw new BusinessException("ФИО не может быть пустым");
        String trimmed = full_name.trim();
        if (trimmed.length() > 100)
            throw new BusinessException("ФИО длиннее 100 символов");
        return trimmed;
    }

    private String validate_phone(String phone) {
        if (phone == null || phone.trim().isEmpty())
            throw new BusinessException("Телефон не может быть пустым");
        String trimmed = phone.trim();
        if (trimmed.length() > 20)
            throw new BusinessException("Телефон длиннее 20 символов");
        if (!PHONE_PATTERN.matcher(trimmed).matches())
            throw new BusinessException("Телефон должен содержать 10–15 цифр, возможно с ведущим '+'");
        return trimmed;
    }

    private String validate_email(String email) {
        if (email == null) return null;
        String trimmed = email.trim();
        if (trimmed.isEmpty()) return null;
        if (trimmed.length() > 100)
            throw new BusinessException("Email длиннее 100 символов");
        if (!EMAIL_PATTERN.matcher(trimmed).matches())
            throw new BusinessException("Некорректный email: " + trimmed);
        return trimmed;
    }

    private void validate_password(String password, boolean required) {
        if (password == null || password.isEmpty()) {
            if (required)
                throw new BusinessException("Пароль не может быть пустым");
            return;
        }
        if (password.length() < 6)
            throw new BusinessException("Пароль должен быть не короче 6 символов");
        if (password.length() > 100)
            throw new BusinessException("Пароль длиннее 100 символов");
    }
}