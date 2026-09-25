package ru.university.postamat.model;

/** Габарит посылки. */
public enum DeliverySize {
    S("S — малая (до 2 кг)"),
    M("M — средняя (2–8 кг)"),
    L("L — крупная (8–30 кг)");

    private final String displayName;

    DeliverySize(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}