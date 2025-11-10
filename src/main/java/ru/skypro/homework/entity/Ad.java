package ru.skypro.homework.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Ad {
    @NotNull
    private int author;
    @NotNull
    private String image;
    @Id
    @NotNull
    private int pk;
    @NotNull
    private int price;
    @NotNull
    private String title;
    @NotNull
    private String description;

    public Ad(int author, String image, int pk, int price, String title, String description) {
        this.author = author;
        this.image = image;
        this.pk = pk;
        this.price = price;
        this.title = title;
        this.description = description;
    }

    public Ad() {}

    @NotNull
    public int getAuthor() {
        return author;
    }

    public void setAuthor(@NotNull int author) {
        this.author = author;
    }

    public @NotNull String getImage() {
        return image;
    }

    public void setImage(@NotNull String image) {
        this.image = image;
    }

    @NotNull
    public int getPk() {
        return pk;
    }

    public void setPk(@NotNull int pk) {
        this.pk = pk;
    }

    @NotNull
    public int getPrice() {
        return price;
    }

    public void setPrice(@NotNull int price) {
        this.price = price;
    }

    public @NotNull String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }
}