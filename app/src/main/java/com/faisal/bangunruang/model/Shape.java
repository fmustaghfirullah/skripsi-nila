package com.faisal.bangunruang.model;

public class Shape {
    private String id;
    private String name;
    private String description;
    private int iconResId;
    private int cardColor;

    public Shape(String id, String name, String description, int iconResId, int cardColor) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
        this.cardColor = cardColor;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
    public int getCardColor() { return cardColor; }
}
