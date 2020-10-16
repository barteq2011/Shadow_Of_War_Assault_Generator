
package org.barteq.alerting;

import javafx.scene.control.Alert;

// Class of prepared alerts for informing user of changes or errors in program
public class Alerts {
    public static void showError(String content) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setHeaderText(null);
        error.setContentText(content);
        error.showAndWait();
    }
    public static void showInfo(String content) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Info");
        info.setHeaderText(null);
        info.setContentText(content);
        info.showAndWait();
    }
}
