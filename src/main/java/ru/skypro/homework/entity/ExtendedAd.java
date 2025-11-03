package ru.skypro.homework.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity



public class ExtendedAd {
    @Id
    @NotNull
    private int pk;
    @NotNull
    private String authorFirstName;
    @NotNull
    private String authorLastName;
    @NotNull
    private String description;
    @NotNull
    private String email;
    @NotNull
    private String image;
    @NotNull
    private String phone;
    @NotNull
    private int price;
    @NotNull
    private String title;


    public ExtendedAd(int pk, String authorFirstName, String authorLastName, String description, String email, String image, String phone, int price, String title) {
        this.pk = pk;
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
        this.description = description;
        this.email = email;
        this.image = image;
        this.phone = phone;
    }

    public ExtendedAd() {}

    @NotNull
    public int getPk() {
        return pk;
    }

    public void setPk(@NotNull int pk) {
        this.pk = pk;
    }

    public @NotNull String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(@NotNull String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public @NotNull String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(@NotNull String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public @NotNull String getImage() {
        return image;
    }

    public void setImage(@NotNull String image) {
        this.image = image;
    }

    public @NotNull String getPhone() {
        return phone;
    }

    public void setPhone(@NotNull String phone) {
        this.phone = phone;
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
}