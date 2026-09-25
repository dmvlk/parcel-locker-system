package ru.university.postamat.service;

import ru.university.postamat.exception.BusinessException;
import ru.university.postamat.exception.EntityNotFoundException;
import ru.university.postamat.model.Postamat;
import ru.university.postamat.model.PostamatStatus;
import ru.university.postamat.repository.DeliveryRequestRepository;
import ru.university.postamat.repository.PostamatRepository;

import java.util.List;

public class PostamatService {

    private final PostamatRepository postamatRepository = new PostamatRepository();
    private final DeliveryRequestRepository requestRepository = new DeliveryRequestRepository();

    public List<Postamat> findAll() {
        return postamatRepository.findAll();
    }

    public Postamat getById(long id) {
        return postamatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Постамат с id=" + id + " не найден"));
    }

    public Postamat create(Postamat postamat) {
        validate(postamat, 0L);
        if (postamat.getStatus() == null) {
            postamat.setStatus(PostamatStatus.ACTIVE);
        }
        return postamatRepository.create(postamat);   // свободные ячейки = capacity
    }

    public Postamat update(Postamat postamat) {
        if (postamat.getId() == null) {
            throw new BusinessException("Не указан id постамата");
        }
        Postamat existing = getById(postamat.getId());
        validate(postamat, postamat.getId());
        if (postamat.getStatus() == null) {
            postamat.setStatus(existing.getStatus());
        }
        // Нельзя уменьшить ёмкость ниже числа занятых ячеек
        int occupied = existing.getCapacity() - existing.getFreeCells();
        if (postamat.getCapacity() < occupied) {
            throw new BusinessException("Нельзя уменьшить количество ячеек: сейчас занято " + occupied);
        }
        postamat.setFreeCells(postamat.getCapacity() - occupied);
        postamatRepository.update(postamat);
        return postamat;
    }

    public void delete(long id) {
        getById(id);
        if (requestRepository.existsByPostamatId(id)) {
            throw new BusinessException("Нельзя удалить постамат: с ним связаны заявки");
        }
        postamatRepository.deleteById(id);
    }

    private void validate(Postamat p, long excludeId) {
        if (p.getAddress() == null || p.getAddress().isBlank()) {
            throw new BusinessException("Адрес постамата обязателен");
        }
        if (p.getCapacity() <= 0 || p.getCapacity() > 1000) {
            throw new BusinessException("Количество ячеек должно быть от 1 до 1000");
        }
        if (postamatRepository.existsByAddress(p.getAddress(), excludeId)) {
            throw new BusinessException("Постамат с таким адресом уже существует");
        }
    }
}