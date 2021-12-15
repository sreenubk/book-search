package com.lotus.book.booksearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.datastax.oss.driver.shaded.guava.common.base.Optional;
import com.lotus.book.booksearch.author.Author;
import com.lotus.book.booksearch.author.AuthorRepository;
import com.lotus.book.booksearch.book.Book;
import com.lotus.book.booksearch.book.BookRepository;
import com.lotus.book.booksearch.connection.DataStaxAstraProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BookSearchApplication {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Value("${library.book.dump.author}")
    private String authorDumpLocation;

    @Value("${library.book.dump.works}")
    private String worksDumpLocation;

    public static void main(String[] args) {
        SpringApplication.run(BookSearchApplication.class, args);
    }

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

    @PostConstruct
    public void start() {

        // Load author here;
        loadAuthor();
        loadWorks();

        // Author author = new Author();
        // author.setId("1234");
        // author.setName("sinu");
        // author.setPersonalName("Srinivas");
        // authorRepository.save(author);
    }

    private void loadAuthor() {

        Path path = Path.of(authorDumpLocation);

        try {
            Stream<String> lines = Files.lines(path);
            lines.forEach(l -> {
                String jsonString = l.substring(l.indexOf("{"));
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Author author = new Author();
                    author.setName(jsonObject.optString("name"));
                    author.setPersonalName(jsonObject.optString("personal_name"));
                    author.setId(jsonObject.optString("key").replace("/a/", ""));
                    // System.out.println("Author Name :" + jsonObject.optString("name") + "," +
                    // "Author Id:"
                    // + jsonObject.optString("key").replace("/a/", ""));
                    authorRepository.save(author);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            lines.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void loadWorks() {

        Path path = Path.of(worksDumpLocation);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try {
            Stream<String> lines = Files.lines(path);
            lines.forEach(l -> {
                String jsonString = l.substring(l.indexOf("{"));
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Book book = new Book();
                    book.setId(jsonObject.getString("key").replace("/works/", ""));
                    // JSONObject description = jsonObject.getJSONObject("description");
                    // if (description != null) {
                    // book.setDescription(description.optString("value"));
                    // }
                    JSONArray coverids = jsonObject.optJSONArray("convers");
                    if (coverids != null) {
                        List<String> covers = new ArrayList<>();
                        for (int i = 0; i < coverids.length(); i++) {
                            covers.add(coverids.getString(i));
                        }
                        book.setCoverIds(covers);

                    }
                    JSONArray authorIdAr = jsonObject.getJSONArray("authors");
                    List<String> authorNames = new ArrayList<>();
                    if (authorIdAr != null) {
                        List<String> auhtorids = new ArrayList<>();
                        for (int i = 0; i < authorIdAr.length(); i++) {
                            JSONObject authJson = authorIdAr.optJSONObject(i);
                            if (authJson != null) {
                                String authorid = authJson.getJSONObject("author")
                                        .getString(("key")).replace("/a/", "");
                                auhtorids.add(authorid);
                                if (authorRepository.findById(authorid).isPresent()) {
                                    String authorName = authorRepository.findById(authorid).get().getName();
                                    authorNames.add(authorName);
                                }

                            }
                        }
                        book.setAuthorId(auhtorids);
                        book.setAuthorsNames(authorNames);
                    }

                    book.setTitle(jsonObject.optString("title"));

                    JSONObject published = jsonObject.optJSONObject("created");
                    if (published != null) {
                        String psdate = published.getString("value");
                        book.setPublishedDate(LocalDate.parse(psdate, dateTimeFormatter));
                    }
                    bookRepository.save(book);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

            lines.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
