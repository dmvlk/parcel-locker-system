package ru.university.postamat.model;

/** Статусы заявки на доставку и допустимые переходы между ними. */
public enum DeliveryStatus {
    CREATED("Создана"),
    IN_TRANSIT("В пути"),
    IN_POSTAMAT("В постамате"),
    DELIVERED("Доставлена"),
    RETURNED("Возвращена отправителю"),
    CANCELED("Отменена");

    private final String displayName;

    DeliveryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // чтобы enum печатался по-русски
    @Override
    public String toString() {
        return displayName;
    }

    // конечные статусы: из них нельзя никуда перейти
    public boolean isFinal() {
        return this == DELIVERED || this == RETURNED || this == CANCELED;
    }

    // проверяем, разрешен ли переход в новый статус (бизнес-правило 4)
    public boolean canTransitionTo(DeliveryStatus target) {
        switch (this) {
            case CREATED:
                return target == IN_TRANSIT || target == CANCELED;
            case IN_TRANSIT:
                return target == IN_POSTAMAT || target == RETURNED || target == CANCELED;
            case IN_POSTAMAT:
                return target == DELIVERED || target == RETURNED;
            default:
                return false; // DELIVERED, RETURNED, CANCELED - конечные
        }
    }
}