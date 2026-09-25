package ru.university.postamat.model;

/** Состояние постамата. */
public enum PostamatStatus {
    ACTIVE("Активен"),
    MAINTENANCE("Обслуживание"),
    OFFLINE("Отключён");

    private final String displayName;

    PostamatStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}