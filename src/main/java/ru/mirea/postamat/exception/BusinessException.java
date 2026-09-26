package ru.mirea.postamat.exception;

// Исключение: нарушение бизнес-правил
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}