package org.barteq;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.barteq.alerting.Alerts;
import org.barteq.dataOperating.DatabseHandler;
import org.barteq.entities.Assault;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainWindowController {
    // Default time until assault
    private static final int ASSAULT_INITIAL_TIME = 5400;
    private int actualUntilAssaultTime;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Label fortessLabel, timeUntilAssaultLabel;
    @FXML
    private Button timerButton;
    // Boolean used for stopping timer thread
    private AtomicBoolean timerStop;
    private boolean isTimePassed;

    @FXML
    public void initialize() {
        timerStop = new AtomicBoolean();
        // Get current assault from user save file
        Assault savedAssault = DatabseHandler.getInstance().getCurrentAssault();
        changeAssault(Objects.requireNonNullElseGet(savedAssault, () -> Objects.requireNonNull(getRandomAssault())));
        timeUntilAssaultLabel.setText(calculateTimerLabelText());
    }

    @FXML
    public void handleStartTimerButton() {
        Task<Object> timerThread = new Task<>() {
            @Override
            protected Object call() {
                try {
                    while (true) {
                        if (!timerStop.get()) {
                            if(actualUntilAssaultTime > 0) {
                                actualUntilAssaultTime -= 1;
                                // Message used to display current time until assault
                                updateMessage(calculateTimerLabelText());
                                //noinspection BusyWait
                                Thread.sleep(1000);
                            } else {
                                isTimePassed = true;
                                Platform.runLater( () -> handleStopTimerButton());
                                break;
                            }
                        } else break;
                    }
                } catch (InterruptedException e) {
                    // Suspended :)
                }
                return null;
            }
        };
        // Set stopper to value that allows thread to run
        timerStop.set(false);
        timeUntilAssaultLabel.textProperty().bind(timerThread.messageProperty());
        new Thread(timerThread).start();
        // Change button
        timerButton.setText("Stop Timer");
        timerButton.setOnAction(e -> handleStopTimerButton());
    }

    @FXML
    public void handleStopTimerButton() {
        // Set stopper to value that will stop timer thread
        timerStop.set(true);
        // Reset timer display and change button
        timeUntilAssaultLabel.textProperty().unbind();
        timeUntilAssaultLabel.setText(calculateTimerLabelText());
        timerButton.setText("Start Timer");
        timerButton.setOnAction(e -> handleStartTimerButton());
        // If thread is stopped by time passing, display notification
        if(isTimePassed) {
            timePassedNotification();
            isTimePassed = false;
        }
    }

    @FXML
    public void generateNewAssaultItem() {
        // Stop timer if running
        handleStopTimerButton();
        // Display confirmation dialog
        Alert newAssaultDialog = new Alert(Alert.AlertType.CONFIRMATION);
        newAssaultDialog.setTitle("Generate New Assault");
        newAssaultDialog.setHeaderText(null);
        newAssaultDialog.setContentText("Generate new Assault?\nThat will delete actual assault and begin new.");
        Optional<ButtonType> result = newAssaultDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If user confirms, generate new assault and change current
            Assault newAssault = getRandomAssault();
            if (newAssault != null)
                changeAssault(newAssault);
        }
    }

    @FXML
    public void handleAboutMenuItem() {
        // Shows info about author of app
        Alerts.showInfo("App made by Bartosz Bartosik :)");
    }

    @FXML
    public void handleExitMenuItem() {
        // Stop the timer and save progress
        handleStopTimerButton();
        DatabseHandler.getInstance().saveCurrentAssault(Assault.of(fortessLabel.getText(),
                actualUntilAssaultTime));
        Platform.exit();
    }

    private void changeAssault(Assault assault) {
        fortessLabel.setText(assault.getFortess());
        actualUntilAssaultTime = assault.getTimeRemaining();
        timeUntilAssaultLabel.setText(calculateTimerLabelText());
    }

    private Assault getRandomAssault() {
        // Get fortesses list from database file
        String[] fortesses = DatabseHandler.getInstance().queryFortesses();
        if (fortesses != null) {
            // Random new assault and return it
            int fortessNumber = (int) (new Random().nextDouble() * fortesses.length);
            String fortess = fortesses[fortessNumber];
            return Assault.of(fortess, ASSAULT_INITIAL_TIME);
        }
        return null;
    }

    private String calculateTimerLabelText() {
        int seconds = actualUntilAssaultTime;
        int minutes = seconds / 60;
        seconds -= (60 * minutes);
        int hours = minutes / 60;
        minutes -= (60 * hours);
        String secondsText = String.valueOf(seconds);
        String minutesText = String.valueOf(minutes);
        String hoursText = String.valueOf(hours);
        if (seconds < 10) secondsText = "0" + secondsText;
        if (minutes < 10) minutesText = "0" + minutesText;
        if (hours < 10) hoursText = "0" + hoursText;
        return hoursText + " : " + minutesText + " : " + secondsText;
    }

    // Getter for menu bar used for create draggable window
    public MenuBar getMenuBar() {
        return menuBar;
    }

    private void timePassedNotification() {
        Alert notification = new Alert(Alert.AlertType.WARNING);
        notification.setTitle("Time Passed");
        notification.setHeaderText(null);
        notification.setContentText("Time until Assault is passed!\n" +
                "Protect your fortess now!");
        notification.showAndWait();
    }
}
