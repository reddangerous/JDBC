package com.example.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private TableView<Student> table;
    private TextField nameInput, scoreInput;
    private Connection conn;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "Reddangerous720@gmail");

            // Create the UI
            GridPane loginPane = createLoginPane();
            Scene loginScene = new Scene(loginPane, 400, 150);
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Student Marks Login");
            primaryStage.show();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public class Student {
        private String name;
        private String adm_no;
        private int marks;

        public Student(String name, int marks) {
            this.name = name;
            this.adm_no = adm_no;
            this.marks = marks;
        }

        public String getName() {
            return name;
        }

        public String getAdmissionNumber() {
            return adm_no;
        }

        public int getMarks() {
            return marks;
        }
    }



    private GridPane createLoginPane() {
        // Create the login UI
        Label usernameLabel = new Label("Username:");
        TextField usernameInput = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordInput = new PasswordField();
        Button loginButton = new Button("Login");

        // Validate the login credentials
        loginButton.setOnAction(event -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            if (validateLogin(username, password)) {
                GridPane mainPane = homePage();
                Scene mainScene = new Scene(mainPane, 600, 400);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(mainScene);
                stage.setTitle("Student Marks");

            } else {
                Label errorLabel = new Label("Invalid username or password.");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setPadding(new Insets(10, 0, 0, 0));
                GridPane.setConstraints(errorLabel, 1, 3);
                GridPane.setColumnSpan(errorLabel, 2);
                GridPane loginPane = (GridPane) loginButton.getScene().getRoot();
                loginPane.getChildren().add(errorLabel);
            }
        });

        GridPane loginPane = new GridPane();
        loginPane.setPadding(new Insets(10, 10, 10, 10));
        loginPane.setVgap(10);
        loginPane.setHgap(10);
        loginPane.setAlignment(Pos.CENTER);
        GridPane.setConstraints(usernameLabel, 0, 0);
        GridPane.setConstraints(usernameInput, 1, 0);
        GridPane.setConstraints(passwordLabel, 0, 1);
        GridPane.setConstraints(passwordInput, 1, 1);
        GridPane.setConstraints(loginButton, 1, 2);
        loginPane.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton);
        Button signupButton = new Button("Sign up");
        loginPane.add(signupButton, 1, 4);
        signupButton.setOnAction(event -> {
            // Create a new window for the signup feature
            Stage signupStage = new Stage();
            signupStage.setTitle("Sign up");
            GridPane signupGridPane = new GridPane();
            signupGridPane.setAlignment(Pos.CENTER);
            signupGridPane.setHgap(10);
            signupGridPane.setVgap(10);
            signupGridPane.setPadding(new Insets(25, 25, 25, 25));

            // Add the username and password text fields
            Label newUsernameLabel = new Label("New username:");
            signupGridPane.add(newUsernameLabel, 0, 1);
            TextField newUsernameField = new TextField();
            signupGridPane.add(newUsernameField, 1, 1);

            Label newPasswordLabel = new Label("New password:");
            signupGridPane.add(newPasswordLabel, 0, 2);
            PasswordField newPasswordField = new PasswordField();
            signupGridPane.add(newPasswordField, 1, 2);

            // Add the signup button
            Button createAccountButton = new Button("Create account");
            signupGridPane.add(createAccountButton, 1, 3);
            createAccountButton.setOnAction(createEvent -> {
                String newUsername = newUsernameField.getText();
                String newPassword = newPasswordField.getText();
                if (createAccount(newUsername, newPassword)) {
                    // Successful account creation
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Account created");
                    alert.setContentText("Your account has been created. Please log in using your new credentials.");
                    alert.showAndWait();
                    signupStage.close();
                } else {
                    // Invalid username or password
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid username or password");
                    alert.setContentText("The username and password you entered is invalid. Please try again.");
                    alert.showAndWait();
                }
            });

            Scene signupScene = new Scene(signupGridPane, 300, 200);
            signupStage.setScene(signupScene);
            signupStage.show();
        });

        return loginPane;

    }

    private GridPane homePage() {
        // Create the UI for the homepage
        Label nameLabel = new Label("Name:");
        TextField nameInput = new TextField();
        Label marksLabel = new Label("Marks:");
        TextField marksInput = new TextField();
        Button submitButton = new Button("Submit");
        Button viewMarks = new Button("viewMarks");
        // Define the layout of the UI elements
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameInput, 1, 0);
        gridPane.add(marksLabel, 0, 2);
        gridPane.add(marksInput, 1, 2);
        gridPane.add(submitButton, 1, 3);
        gridPane.add(viewMarks, 1, 4);


// Define the event handler for the submit button
        submitButton.setOnAction(ActionEvent -> {
            final AtomicReference<String>[] name = new AtomicReference[]{new AtomicReference<>(nameInput.getText())};
            //final AtomicReference<String>[] admNo = new AtomicReference[]{new AtomicReference<>(admNoInput.getText())};
            String marksStr = marksInput.getText();
            if (!name[0].get().isEmpty()  && !marksStr.isEmpty()) {
                try {
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO students (name, marks) VALUES (?, ?)");
                    stmt.setString(1, name[0].get());
                    //stmt.setString(2, admNo[0].get());
                    stmt.setInt(2, Integer.parseInt(marksStr));
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Student record added successfully!");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Failed to add student record. Please try again later.");
                        errorAlert.showAndWait();
                    }
                } catch (SQLException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("An error occurred while adding student record. Please try again later.");
                    errorAlert.showAndWait();
                    e.printStackTrace();
                }
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Please fill in all the fields.");
                errorAlert.showAndWait();
            }


        });
        viewMarks.setOnAction(actionEvent -> {
            TableView<Student> mainPane = showMarksTable();
            Scene mainScene = new Scene(mainPane, 600, 400);
            Stage stage = (Stage) viewMarks.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("Student Marks");
        });



        return gridPane;
    }

    private TableView<Student> showMarksTable() {
        // Define the columns of the data grid view
        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        //TableColumn<Student, String> admNoColumn = new TableColumn<>("Admission No");
        //admNoColumn.setCellValueFactory(new PropertyValueFactory<>("adm_no"));

        TableColumn<Student, Integer> marksColumn = new TableColumn<>("Marks");
        marksColumn.setCellValueFactory(new PropertyValueFactory<>("marks"));

        // Create the data grid view and add the columns
        TableView<Student> marksTable = new TableView<>();
        marksTable.getColumns().setAll(nameColumn, marksColumn);

        // Fetch the data from the database and add it to the data grid view
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "Reddangerous720@gmail")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT name, marks FROM students");
            ResultSet rs = stmt.executeQuery();
            ObservableList<Student> studentsList = FXCollections.observableArrayList();
            while (rs.next()) {
                String name = rs.getString("name");
                //String adm_no = rs.getString("adm_no");
                int marks = rs.getInt("marks");
                studentsList.add(new Student(name, marks));
            }
            marksTable.setItems(studentsList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create a new window and add the marks table to it
        Stage marksStage = new Stage();
        Scene marksScene = new Scene(marksTable);
        marksStage.setScene(marksScene);
        marksStage.show();

        return marksTable;
    }



    private boolean createAccount(String newUsername, String newPassword) {
        try {
            // Open a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "Reddangerous720@gmail");

            // Check if the username already exists
            PreparedStatement checkStatement = connection.prepareStatement("SELECT id FROM lecturers WHERE username = ?");
            checkStatement.setString(1, newUsername);
            ResultSet checkResult = checkStatement.executeQuery();
            if (checkResult.next()) {
                // Username already exists
                return false;
            }

            // Insert the new user into the database
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO lecturers (username, password) VALUES (?, ?)");
            insertStatement.setString(1, newUsername);
            insertStatement.setString(2, newPassword);
            int rowsAffected = insertStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateLogin(String username, String password) {
        // Validate the login credentials against the database
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM lecturers WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 1) {
                // Valid login
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Invalid login
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
