package org.nico.quoted.domain.model;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.nico.quoted.api.QuotesAPI;
import org.nico.quoted.api.SourcesAPI;
import org.nico.quoted.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;


public class ClientModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientModel.class);

    // ############################## Setup ###########################

    private final ObservableList<SourceInterface> sources;
    private final ObservableList<Book> books;
    private final ObservableList<Author> authors;
    private final ObservableList<Article> articles;
    private final ObservableList<Quote> quotes;

    private final ObjectProperty<SourceInterface> selectedSource;
    private final ObjectProperty<Quote> selectedQuote;
    private static Quote lastRandomQuote;
    private final BooleanProperty resetForm;

    public ClientModel() {
        // Initial read
        this.sources = FXCollections.observableArrayList(SourcesAPI.getInstance().readAll());
        this.books = FXCollections.observableArrayList(filterBooksFromSources());
        this.authors = FXCollections.observableArrayList(getAuthorsFromBooks());
        this.articles = FXCollections.observableArrayList(filterArticlesFromSources());
        this.quotes = FXCollections.observableArrayList(QuotesAPI.getInstance().readAll());

        // Selectors
        selectedSource = new SimpleObjectProperty<>();
        selectedQuote = new SimpleObjectProperty<>();
        resetForm = new SimpleBooleanProperty();

        // Change Listeners
        // TODO Register the change listeners to update the API/DB - Question: Just adding via quote or source?

        this.quotes.addListener(quoteListChangeListener());
        this.sources.addListener(sourceListChangeListener());
        this.books.addListener(bookListChangeListener());
    }

    // ############################## Getters ###########################

    public ObjectProperty<SourceInterface> selectedSourceProperty() {
        return selectedSource; // TODO SETTING the selected source will throw an InvocationTargetException as the source is bound. Either remove the binding, or restrict access to the property.
    }

    public ObjectProperty<Quote> selectedQuoteProperty() {
        return selectedQuote;
    }

    public ObservableList<SourceInterface> getSources() {
        return sources;
    }

    public SourceInterface getSourceByIndex(int index) {
        return sources.get(index);
    }

    public ObservableList<Book> getBooks() {
        return books;
    }

    public ObservableList<Author> getAuthors() {
        return authors;
    }

    public ObservableList<Quote> getQuotes() {
        return quotes;
    }

    // ############################## Controller API ###########################

    public void addQuote(Quote quoteToAdd) {
        quotes.add(quoteToAdd); // Sources are updating automatically once changes in quote list are registered
        LOGGER.info("Added quote: " + quoteToAdd.getText() + ", from source: " + quoteToAdd.getSource().getOrigin());
    }

    public void deleteQuoteByIndex(int index) {
        LOGGER.info("Removing quote: " + quotes.get(index).getText() + ", from source: " + quotes.get(index).getSource().getOrigin());
        quotes.remove(index); // Sources are updating automatically once changes in quote list are registered
    }

    public Quote getQuoteByIndex(int index) {
        return quotes.get(index);
    }

    public Quote getRandomQuote() {
        if (quotes.size() == 0)
            return new Quote("No quotes found", new Article("No source found", "-"));
        if (quotes.size() == 1)
            return quotes.get(0);

        Quote randomQuote;
        do {
            randomQuote = quotes.get((int) (Math.random() * quotes.size()));
        } while (randomQuote.equals(lastRandomQuote));
        lastRandomQuote = randomQuote;
        return randomQuote;
    }


    public void updateQuote(Quote quote) {
        quotes.stream().filter(quoteToUpdate -> quoteToUpdate.equals(quote)).findFirst().ifPresent(quote1 -> {
            quote1.setText(quote.getText());
            quote1.setSource(quote.getSource());
        });
    }

    public ObservableList<SourceInterface> searchSources(String searchString) {
        return sources.stream()
                .filter(source -> source.getTitle().toLowerCase().contains(searchString.toLowerCase())
                        || source.getOrigin().toLowerCase().contains(searchString.toLowerCase()))
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
    }

    public ObservableList<Quote> searchQuotes(String searchString) {
        return quotes.stream()
                .filter(quote -> quote.getText().toLowerCase().contains(searchString.toLowerCase())
                        || quote.getSource().toString().toLowerCase().contains(searchString.toLowerCase()))
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
    }

    public ObservableList<Quote> getQuotesBySource(SourceInterface source) {
        LOGGER.info("Getting quotes by source: " + source.getOrigin());
        return quotes.stream()
                .filter(quote -> quote.getSource().equals(source))
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
    }

    public void addBook(Book newBook) {
        sources.add(newBook);
        LOGGER.info("Added book: " + newBook.getTitle() + ", by author: " + newBook.getAuthor());
    }

    public void updateSource(SourceInterface source) {
        quotes.stream()
                .filter(quote -> quote.getSource().equals(EditViewModel.getSourceToEdit()))
                .forEach(quote -> quote.setSource(source));

        // TODO with DB
        // source.setId(selectedSource.get().getId());

        sources.set(sources.indexOf(EditViewModel.getSourceToEdit()), source);
        LOGGER.info("Updated source: " + source.getOrigin());
        quotes.forEach(quote -> LOGGER.info("Updated quote: " + quote.getText() + " to new source: " + quote.getSource().getOrigin()));

    }

    public void deleteSourceByIndex(int index) {
        LOGGER.info("Deleting source: " + sources.get(index).getOrigin());
        this.sources.remove(index);
    }

    public void resetForm() {
        this.resetForm.set(!resetForm.get());
    }

    public void registerResetListener(ChangeListener<Boolean> listener) {
        this.resetForm.addListener(listener);
    }


    // ############################## Change listeners ###########################

    // TODO As per doc, c.wasReplaced() is not usually used, and appears to not work as expected.

    private ListChangeListener<Quote> quoteListChangeListener() {
        return c -> {
            while (c.next()) {
                if (c.wasReplaced()) {
                    c.getRemoved().forEach(quote -> {
                        if (sources.contains(quote.getSource()) && getQuotesBySource(quote.getSource()).isEmpty())
                            sources.remove(quote.getSource()); // TODO this is not working
                    });

                    c.getAddedSubList().forEach(quote -> {
                        if (!sources.contains(quote.getSource()))
                            sources.add(quote.getSource());
                    });
                }


                else if (c.wasAdded())
                    c.getAddedSubList().forEach(quote -> {
                        if (!sources.contains(quote.getSource()))
                            sources.add(quote.getSource());
                    });

                else if (c.wasRemoved())
                    c.getRemoved().forEach(quote -> {
                        if (sources.contains(quote.getSource()) && getQuotesBySource(quote.getSource()).isEmpty())
                            sources.remove(quote.getSource());
                    });
            }
        };
    }

    private ListChangeListener<SourceInterface> sourceListChangeListener() {
        return c -> {
            while (c.next()) {
                if (c.wasReplaced()) {
                    c.getRemoved().forEach(source -> {
                        if (source instanceof Book)
                            books.remove(source);
                        else if (source instanceof Article)
                            articles.remove(source);
                    });

                    c.getAddedSubList().forEach(source -> {
                        LOGGER.info("Updating source: " + source.getOrigin());
                        if (source instanceof Book)
                            books.add((Book) source);
                        else if (source instanceof Article)
                            articles.add((Article) source);
                    });

                }

                else if (c.wasAdded())
                    c.getAddedSubList().forEach(source -> {
                        if (source instanceof Book)
                            books.add((Book) source);
                        else if (source instanceof Article)
                            articles.add((Article) source);
                    });

                else if (c.wasRemoved())
                    c.getRemoved().forEach(source -> {
                        if (source instanceof Book)
                            books.remove(source);
                        else if (source instanceof Article)
                            articles.remove(source);

                        quotes.removeIf(quote -> quote.getSource().equals(source));
                    });
            }
        };
    }


    private ListChangeListener<Book> bookListChangeListener() {
        return c -> {
            while (c.next()) {
                if (c.wasReplaced()) {
                    c.getRemoved().forEach(book -> this.authors.removeIf(author
                            -> books.stream().noneMatch(remainingBook -> remainingBook.getAuthor().equals(author))));

                    c.getAddedSubList().forEach(book -> {
                        if (!authors.contains(book.getAuthor()))
                            authors.add(book.getAuthor());
                    });
                }

                else if (c.wasAdded())
                    c.getAddedSubList().forEach(book -> {
                        if (!authors.contains(book.getAuthor()))
                            authors.add(book.getAuthor());
                    });

                else if (c.wasRemoved())
                    c.getRemoved().forEach(book -> this.authors.removeIf(author
                            -> books.stream().noneMatch(remainingBook -> remainingBook.getAuthor().equals(author))));
            }
        };
    }

    private List<Book> filterBooksFromSources() {
        return sources.stream()
                .filter(Book.class::isInstance)
                .map(Book.class::cast)
                .collect(Collectors.toList());
    }

    private List<Article> filterArticlesFromSources() {
        return sources.stream()
                .filter(Article.class::isInstance)
                .map(Article.class::cast)
                .collect(Collectors.toList());
    }

    private List<Author> getAuthorsFromBooks() {
        return books.stream()
                .map(Book::getAuthor)
                .distinct() // TODO check if this is necessary
                .collect(Collectors.toList());
    }

}