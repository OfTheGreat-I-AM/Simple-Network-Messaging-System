/**
 * Client.java
 *
 * Entry point for the JavaFX client application.
 *
 * @author: Musa Nkosi
 * Purpose: Launch the JavaFX stage and show ChatGUI component
 */
import csc2b.gui.ChatGUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Client launches the JavaFX Application and sets ChatGUI as the Scene root.
 * Keep this class minimal; logic belongs to the ChatGUI component.
 */
public class Client extends Application {

    @Override
    public void start(Stage primaryStage) {
        ChatGUI chatUI = new ChatGUI();

        Scene scene = new Scene(chatUI, 600, 400);
        primaryStage.setTitle("Networking Client - Chat & Announcements");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * main - launch the JavaFX application
     *
     * @param args runtime args (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
