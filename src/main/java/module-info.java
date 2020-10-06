module org.barteq {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.barteq to javafx.fxml;
    exports org.barteq;
}