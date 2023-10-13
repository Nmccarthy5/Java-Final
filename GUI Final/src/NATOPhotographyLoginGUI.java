import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

class Database {
    public void insertUser(User user) throws IOException {
        Files.write(Paths.get(user.getUsername()), List.of(user.getUsername(), user.getPassword()));
    }

    public User getUser(String username) throws IOException {
        Path path = Paths.get(username);
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path);
            return new User(lines.get(0), lines.get(1));
        }
        return null;
    }
}

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class UserManager {
    private Database db;

    public UserManager(Database db) {
        this.db = db;
    }

    public boolean register(User user) {
        try {
            db.insertUser(user);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticate(String username, String password) {
        try {
            User user = db.getUser(username);
            return user != null && user.getPassword().equals(password);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

public class NATOPhotographyLoginGUI extends Application {
    private UserManager userManager;
    private Stage mainStage;
    private Stage registerStage;
    private Stage loginStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Database db = new Database();
        userManager = new UserManager(db);
        mainAccountScreen();
    }

    private void mainAccountScreen() {
        mainStage = new Stage();
        mainStage.setTitle("Account Login");
        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView(new Image("file:Nathan.png"));
        mainLayout.getChildren().add(imageView);
        Label title = new Label("NATO PHOTOGRAPHY");
        mainLayout.getChildren().add(title);
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> login());
        mainLayout.getChildren().add(loginButton);
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> register());
        mainLayout.getChildren().add(registerButton);
        Label photoLabel = new Label("Photo: Silhouette of a man in a fedora");
        mainLayout.getChildren().add(photoLabel);
        Scene scene = new Scene(mainLayout, 650, 1240);
        mainStage.setScene(scene);
        mainStage.show();
    }

    private void register() {
        registerStage = new Stage();
        registerStage.setTitle("Register");
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        gridPane.add(new Label("Please enter details below"), 0, 0, 2, 1);
        gridPane.add(new Label("Username"), 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(new Label("Password"), 0, 2);
        gridPane.add(passwordField, 1, 2);
        Button registerBtn = new Button("Register");
        registerBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (userManager.register(new User(username, password))) {
                showAlert(Alert.AlertType.INFORMATION, registerStage, "Registration Success");
            } else {
                showAlert(Alert.AlertType.ERROR, registerStage, "Registration Failed");
            }
            
            usernameField.clear();
            passwordField.clear();
        });

        gridPane.add(registerBtn, 1, 3);
        registerStage.setScene(new Scene(gridPane, 300, 250));
        registerStage.show();
    }

    private void login() {
        loginStage = new Stage();
        loginStage.setTitle("Login");
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        gridPane.add(new Label("Please enter details below to login"), 0, 0, 2, 1);
        gridPane.add(new Label("Username"), 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(new Label("Password"), 0, 2);
        gridPane.add(passwordField, 1, 2);

        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            
            if (userManager.authenticate(username, password)) {
                loginSuccess();
            } else {
                showAlert(Alert.AlertType.ERROR, loginStage, "Login Failed");
            }

            usernameField.clear();
            passwordField.clear();
        });

        gridPane.add(loginBtn, 1, 3);
        loginStage.setScene(new Scene(gridPane, 300, 250));
        loginStage.show();
    }

    private void loginSuccess() {
        Stage successStage = new Stage();
        successStage.setTitle("Success");
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        Label successLabel = new Label("Login Success");
        layout.getChildren().add(successLabel);
        Button dataBtn = new Button("View Customer Data");
        // Assuming a separate class exists for this:
        dataBtn.setOnAction(e -> {
            CustomerDataGUI gui = new CustomerDataGUI();
            gui.showCustomerData();
        });
        layout.getChildren().add(dataBtn);
        successStage.setScene(new Scene(layout, 300, 250));
        successStage.show();
    }

    private void showAlert(Alert.AlertType alertType, Stage owner, String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}