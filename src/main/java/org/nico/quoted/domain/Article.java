package org.nico.quoted.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor // Needed for JPA
@Getter @Setter
@Entity
@DiscriminatorValue("article")
public class Article extends Source {

    private String url;
    @CreationTimestamp
    @Column(name = "last_visited")
    private Timestamp lastVisited;

    public Article(String title, String url) {
        super(title);

        if (title == null || title.isBlank())
            super.setTitle(url);

        this.url = url;
    }

    @Override
    public String toString() {
        if (this.getTitle() == null || this.getTitle().equals(this.url))
            return this.getTitle();
        else
            return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return this.url.equalsIgnoreCase(article.url);
    }

    @Override
    public String getOrigin() {
        return this.url;
    }
}
