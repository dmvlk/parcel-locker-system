package ru.university.postamat.exception;

/** Нарушение бизнес-правила (проверяется в Service-слое). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}