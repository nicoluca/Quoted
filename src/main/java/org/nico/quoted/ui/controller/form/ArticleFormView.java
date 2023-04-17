package org.nico.quoted.ui.controller.form;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class ArticleFormView {

    public void show() {
        try {
            Parent bookFormView = FXMLLoader
                    .load(Objects.requireNonNull(getClass().getResource("/org/nico/quoted/form/url-form-view.fxml")));
            Scene scene = new Scene(bookFormView);

            Stage bookFormStage = new Stage();
            bookFormStage.setScene(scene);
            bookFormStage.setTitle("Edit Article");
            bookFormStage.show();
        } catch (IOException e) {
            log.error("url-form-view.fxml could not be found");
            throw new RuntimeException(e);
        }
    }
}
