package com.example.bank_system.entity;

public enum Currency {
    USD("US Dollar"),
    ILS("Israeli Shekel"),
    EUR("Euro"),
    GBP("British Pound"),
    JPY("Japanese Yen"),
    CAD("Canadian Dollar");

    private final String displayName;

    Currency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
