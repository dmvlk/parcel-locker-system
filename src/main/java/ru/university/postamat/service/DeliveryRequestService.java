package ru.university.postamat.service;

import ru.university.postamat.exception.BusinessException;
import ru.university.postamat.exception.EntityNotFoundException;
import ru.university.postamat.model.*;
import ru.university.postamat.repository.DeliveryRequestRepository;
import ru.university.postamat.repository.PostamatRepository;
import ru.university.postamat.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class DeliveryRequestService {

    public static final BigDecimal MAX_WEIGHT = new BigDecimal("30");

    private final DeliveryRequestRepository requestRepository = new DeliveryRequestRepository();
    private final UserRepository userRepository = new UserRepository();
    private final PostamatRepository postamatRepository = new PostamatRepository();
    private final Random random = new Random();

    public List<DeliveryRequest> findAll() {
        return requestRepository.findAll();
    }

    public DeliveryRequest getById(long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заявка с id=" + id + " не найдена"));
    }

    public DeliveryRequest getByTrackingNumber(String trackingNumber) {
        return requestRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new EntityNotFoundException("Заявка с трек-номером " + trackingNumber + " не найдена"));
    }

    public List<DeliveryRequest> search(String field, String query) {
        if (query == null || query.isBlank()) {
            throw new BusinessException("Введите поисковый запрос");
        }
        query = query.trim();
        switch (field) {
            case "tracking":
                return requestRepository.searchByTracking(query);
            case "item":
                return requestRepository.searchByItem(query);
            case "user":
                return requestRepository.searchByUser(query);
            default:
                return requestRepository.searchByPostamat(query);
        }
    }

    public DeliveryRequest create(DeliveryRequest request) {
        validateContent(request);
        // Правило 1: получатель и постамат должны существовать
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("Получатель не найден: укажите существующего пользователя (бизнес-правило №1)"));
        Postamat postamat = postamatRepository.findById(request.getPostamatId())
                .orElseThrow(() -> new BusinessException("Постамат не найден: укажите существующий постамат (бизнес-правило №1)"));
        // Правило 2: постамат доступен и есть куда положить посылку
        if (postamat.getStatus() != PostamatStatus.ACTIVE) {
            throw new BusinessException("Постамат недоступен: статус «" + postamat.getStatus().getDisplayName()
                    + "» (бизнес-правило №2)");
        }
        if (postamat.getFreeCells() == 0) {
            throw new BusinessException("В постамате нет свободных ячеек (бизнес-правило №2)");
        }
        request.setStatus(DeliveryStatus.CREATED);
        return requestRepository.create(request);
    }

    public DeliveryRequest update(DeliveryRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("Не указан id заявки");
        }
        DeliveryRequest existing = getById(request.getId());
        if (existing.getStatus() != DeliveryStatus.CREATED) {
            throw new BusinessException("Редактировать можно только заявку в статусе «Создана» (текущий: "
                    + existing.getStatus().getDisplayName() + ")");
        }
        validateContent(request);
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("Получатель не найден (бизнес-правило №1)"));
        Postamat postamat = postamatRepository.findById(request.getPostamatId())
                .orElseThrow(() -> new BusinessException("Постамат не найден (бизнес-правило №1)"));
        if (postamat.getStatus() != PostamatStatus.ACTIVE) {
            throw new BusinessException("Постамат недоступен (бизнес-правило №2)");
        }
        requestRepository.update(request);
        return getById(request.getId());
    }

    /**
     * Смена статуса с проверкой карты переходов (правило №4).
     * enteredCode нужен ТОЛЬКО при переходе в DELIVERED (правило №8).
     */
    public DeliveryRequest changeStatus(long id, DeliveryStatus newStatus, String enteredCode) {
        DeliveryRequest request = getById(id);
        if (request.getStatus() == newStatus) {
            throw new BusinessException("Заявка уже находится в этом статусе");
        }
        if (!request.getStatus().canTransitionTo(newStatus)) {
            throw new BusinessException("Недопустимый переход статуса: "
                    + request.getStatus().getDisplayName() + " -> " + newStatus.getDisplayName()
                    + " (бизнес-правило №4)");
        }
        String pickupCode = null;
        if (newStatus == DeliveryStatus.IN_POSTAMAT) {
            Postamat postamat = postamatRepository.findById(request.getPostamatId())
                    .orElseThrow(() -> new BusinessException("Постамат не найден"));
            if (postamat.getStatus() != PostamatStatus.ACTIVE) {
                throw new BusinessException("Постамат недоступен: статус «"
                        + postamat.getStatus().getDisplayName() + "» (бизнес-правило №2)");
            }
            pickupCode = String.format("%06d", random.nextInt(1_000_000));
        }
        if (newStatus == DeliveryStatus.DELIVERED) {
            String stored = request.getPickupCode();
            if (stored == null || !stored.equals(enteredCode)) {
                throw new BusinessException("Неверный код получения — посылка не выдана (бизнес-правило №8)");
            }
        }
        requestRepository.updateStatusWithCells(request, newStatus, pickupCode);
        return request;
    }

    public void delete(long id) {
        DeliveryRequest request = getById(id);
        if (request.getStatus() == DeliveryStatus.IN_TRANSIT || request.getStatus() == DeliveryStatus.IN_POSTAMAT) {
            throw new BusinessException("Нельзя удалить заявку в статусе «" + request.getStatus().getDisplayName()
                    + "» — её можно только отменить или вернуть (бизнес-правило №5)");
        }
        requestRepository.deleteById(id);
    }

    /** Правило 3 + обязательные поля. */
    private void validateContent(DeliveryRequest r) {
        if (r.getItemDescription() == null || r.getItemDescription().isBlank()) {
            throw new BusinessException("Описание товара обязательно");
        }
        if (r.getWeightKg() == null || r.getWeightKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Вес должен быть больше нуля (бизнес-правило №3)");
        }
        if (r.getWeightKg().compareTo(MAX_WEIGHT) > 0) {
            throw new BusinessException("Вес не может превышать " + MAX_WEIGHT + " кг (бизнес-правило №3)");
        }
        if (r.getSize() == null) {
            throw new BusinessException("Укажите габарит посылки (S/M/L)");
        }
        if (r.getUserId() == null || r.getPostamatId() == null) {
            throw new BusinessException("Укажите получателя и постамат");
        }
    }
}