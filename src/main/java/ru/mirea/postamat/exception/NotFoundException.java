package ru.mirea.postamat.exception;

// Исключение: сущность с таким id не найдена
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}