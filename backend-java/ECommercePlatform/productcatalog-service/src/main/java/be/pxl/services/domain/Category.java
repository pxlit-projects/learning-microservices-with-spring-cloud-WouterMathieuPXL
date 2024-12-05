package be.pxl.services.domain;

public enum Category {
    KLEDING("Kleding"),
    ZERO_WASTE("Zero Waste"),
    VERZORGING("Verzorging"),
    HUISHOUDEN("Huishouden"),
    WELLNESS("Wellness"),
    KANTOOR("Kantoor"),
    MEUBELS("Meubels"),
    ELEKTRONICA("Elektronica"),
    SPEELGOED("Speelgoed"),
    VOEDING("Voeding"),
    SPORT("Sport");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
