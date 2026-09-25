package ru.university.postamat.repository;

import java.util.List;
import java.util.Optional;

/** Общий интерфейс для всех репозиториев: стандартный набор операций CRUD. */
public interface CrudRepository<T> {
    T create(T entity);
    Optional<T> findById(long id);
    List<T> findAll();
    boolean update(T entity);
    boolean deleteById(long id);
}