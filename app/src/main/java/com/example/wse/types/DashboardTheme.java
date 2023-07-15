package com.example.wse.types;

public enum DashboardTheme {
    LIGHT("light"),
    DARK("dark");

    private final String theme;

    DashboardTheme(String theme) {
        this.theme = theme;
    }

    public static DashboardTheme fromValue(String sTheme) {
        for (DashboardTheme theme : DashboardTheme.values()) {
            if (theme.getTheme().equals(sTheme)) {
                return theme;
            }
        }
        return null;
    }

    public String getTheme() {
        return theme;
    }
}
