package org.nico.quoted.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import org.nico.quoted.domain.Book;
import org.nico.quoted.domain.Quote;

import static org.nico.quoted.ui.controller.BaseController.model;

public class RandomViewController {

    @FXML
    private Button nextQuoteButton;

    @FXML
    private Label quoteOriginTitle;

    @FXML
    private Text quoteText;

    @FXML
    void initialize() {
        checkAssertions();
        setNextRandomQuote();
    }

    private void setNextRandomQuote() {
        Quote quote = model.getRandomQuote();
        quoteText.setText(quote.getText());
        setQuoteOrigin(quote);
    }

    private void setQuoteOrigin(Quote quote) {
        // TODO Maybe integrate into interface and use polymorphism
        if (quote.getSource() instanceof Book book)
            quoteOriginTitle.setText(book.getTitle() + " - " + book.getAuthor());
        else
            quoteOriginTitle.setText(quote.getSource().getOrigin());
    }

    public void onNextQuoteButtonClicked(ActionEvent actionEvent) {
        setNextRandomQuote();
    }

    private void checkAssertions() {
        assert nextQuoteButton != null : "fx:id=\"nextQuoteButton\" was not injected: check your FXML file 'random-view.fxml'.";
        assert quoteOriginTitle != null : "fx:id=\"quoteOriginTitle\" was not injected: check your FXML file 'random-view.fxml'.";
        assert quoteText != null : "fx:id=\"quoteText\" was not injected: check your FXML file 'random-view.fxml'.";
    }
}
