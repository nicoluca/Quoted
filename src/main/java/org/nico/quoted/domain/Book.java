package org.nico.quoted.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Book extends Source {
    private Author author;
    private String coverPath;

    public Book(String title, Author author, String coverPath) {
        super(title);
        this.author = author;
        this.coverPath = coverPath;
    }

    @Override
    public boolean equals(Object o) {
        // TODO Replace with ID
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return this.getTitle().equals(book.getTitle())
                && this.author.equals(book.author);
    }

    @Override
    public String getOrigin() {
        return this.author.getFirstName() + " " + this.author.getLastName();
    }


}
