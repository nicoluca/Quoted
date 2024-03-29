package org.nico.quoted.ui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.nico.quoted.config.UIConfig;
import org.nico.quoted.domain.Article;
import org.nico.quoted.domain.Book;
import org.nico.quoted.domain.Quote;
import org.nico.quoted.domain.Source;
import org.nico.quoted.ui.controller.form.BookFormView;
import org.nico.quoted.util.StringUtil;

public class NewViewController extends MainController {
    @FXML
    private Button newBookButton;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField urlInputField;

    @FXML
    private Button addButton;

    @FXML
    private ChoiceBox<Book> bookChoiceBox;

    @FXML
    private TextArea quoteInputField;

    @FXML
    private CheckBox toggleURL;

    @FXML
    void initialize() {
        checkAssertions();
        setupChoiceBox();
        wrapText();
        urlInputField.setDisable(true);
        errorLabel.setText("");
    }

    private void checkAssertions() {
        assert newBookButton != null : "fx:id=\"newBookButton\" was not injected: check your FXML file 'new-view.fxml'.";
        assert urlInputField != null : "fx:id=\"UrlInputField\" was not injected: check your FXML file 'new-view.fxml'.";
        assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'new-view.fxml'.";
        assert bookChoiceBox != null : "fx:id=\"bookChoiceBox\" was not injected: check your FXML file 'new-view.fxml'.";
        assert quoteInputField != null : "fx:id=\"quoteInputField\" was not injected: check your FXML file 'new-view.fxml'.";
        assert toggleURL != null : "fx:id=\"toggleURL\" was not injected: check your FXML file 'new-view.fxml'.";
    }

    private void setupChoiceBox() {
        bookChoiceBox.setItems(model.getBooks());
        model.getBooks().addListener((ListChangeListener<Book>) c -> bookChoiceBox.setItems(model.getBooks()));
    }


    private void wrapText() {
        quoteInputField.setWrapText(true);
    }


    public void toggleUrlField() {
        urlInputField.setDisable(!toggleURL.isSelected());
        bookChoiceBox.setDisable(toggleURL.isSelected());
        newBookButton.setDisable(toggleURL.isSelected());
    }

    public void onAddButtonClick() {
        if (!inputIsValid())
            displayError();
        else {
            addQuote();
            displaySuccess();
            resetForm();
        }
    }

    private void displaySuccess() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    errorLabel.setTextFill(Color.GREEN);
                    errorLabel.setText("Quote added!");
                }),
                new KeyFrame(Duration.millis(UIConfig.DURATION_INFO_MESSAGE), event -> {
                    errorLabel.setText("");
                    errorLabel.setTextFill(Color.RED);
                })
        );
        timeline.setCycleCount(1); // Play once
        timeline.play();
    }


    private void resetForm() {
        quoteInputField.setText("");
    }

    private void addQuote() {
        if (toggleURL.isSelected())
            addQuoteFromUrl();
        else
            addQuoteFromBook();
    }

    private void addQuoteFromBook() {
        Book book = bookChoiceBox.getValue();
        Quote quote = new Quote(quoteInputField.getText(), book);
        model.addQuote(quote);
    }

    private void addQuoteFromUrl() {
        String url = urlInputField.getText();
        Source article = new Article(null, url);
        Quote quote = new Quote(quoteInputField.getText(), article);
        model.addQuote(quote);
    }

    private void displayError() {
        if (quoteInputField.getText().isEmpty())
            errorLabel.setText("Quote cannot be empty.");
        else if (toggleURL.isSelected() && !StringUtil.isValidURL(urlInputField.getText()))
            errorLabel.setText("Invalid URL");
        else if (!toggleURL.isSelected() && bookChoiceBox.getValue() == null)
            errorLabel.setText("Please select a book");
    }

    private boolean inputIsValid() {
        return !quoteInputField.getText().isEmpty()
                && ((toggleURL.isSelected() && StringUtil.isValidURL(urlInputField.getText())
                || (!toggleURL.isSelected() && bookChoiceBox.getValue() != null)));
    }

    public void onNewBookButtonClick() {
        model.setSourceToEdit(null);
        new BookFormView().show();
    }
}
