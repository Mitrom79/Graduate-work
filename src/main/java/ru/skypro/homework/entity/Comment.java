package ru.skypro.homework.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;



import java.util.Date;

@Entity


public class Comment {
    @NotNull
    private int author;
    @NotNull
    private String authorImage;
    @NotNull
    private String authorFirstName;
    @NotNull
    private Date createdAt;
    @Id
    @NotNull
    private int pk;
    @NotNull
    private String text;

    public Comment(int author, String authorImage, String authorFirstName, Date createdAt, int pk, String text) {
        this.author = author;
        this.authorImage = authorImage;
        this.authorFirstName = authorFirstName;
        this.createdAt = createdAt;
        this.pk = pk;
        this.text = text;
    }

    @NotNull
    public int getAuthor() {
        return author;
    }

    public void setAuthor(@NotNull int author) {
        this.author = author;
    }

    public @NotNull String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(@NotNull String authorImage) {
        this.authorImage = authorImage;
    }

    public @NotNull String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(@NotNull String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public @NotNull Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NotNull Date createdAt) {
        this.createdAt = createdAt;
    }

    @NotNull
    public int getPk() {
        return pk;
    }

    public void setPk(@NotNull int pk) {
        this.pk = pk;
    }

    public @NotNull String getText() {
        return text;
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }

    public Comment() {}
}