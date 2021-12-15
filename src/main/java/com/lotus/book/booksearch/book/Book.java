package com.lotus.book.booksearch.book;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@Table(value = "book_by_title")
public class Book {

    @Id
    @PrimaryKeyColumn(name = "book_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id;

    @Column("book_title")
    @CassandraType(type = Name.TEXT)
    private String title;

    @Column("book_desciption")
    @CassandraType(type = Name.TEXT)
    private String description;

    @Column("author_names")
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> authorsNames;

    @Column("author_id")
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> authorId;

    @Column("cover_ids")
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> coverIds;

    @Column("published_date")
    @CassandraType(type = Name.DATE)
    private LocalDate publishedDate;

    /**
     * @return String return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return String return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return String return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return List<String> return the authorsNames
     */
    public List<String> getAuthorsNames() {
        return authorsNames;
    }

    /**
     * @param authorsNames the authorsNames to set
     */
    public void setAuthorsNames(List<String> authorsNames) {
        this.authorsNames = authorsNames;
    }

    /**
     * @return List<String> return the authorId
     */
    public List<String> getAuthorId() {
        return authorId;
    }

    /**
     * @param authorId the authorId to set
     */
    public void setAuthorId(List<String> authorId) {
        this.authorId = authorId;
    }

    /**
     * @return List<String> return the coverIds
     */
    public List<String> getCoverIds() {
        return coverIds;
    }

    /**
     * @param coverIds the coverIds to set
     */
    public void setCoverIds(List<String> coverIds) {
        this.coverIds = coverIds;
    }

    /**
     * @return LocalDate return the publishedDate
     */
    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    /**
     * @param publishedDate the publishedDate to set
     */
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

}
