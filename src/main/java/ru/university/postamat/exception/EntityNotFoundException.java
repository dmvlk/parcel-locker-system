package ru.university.postamat.exception;

/** Сущность не найдена в БД по id / трек-номеру. */
public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}