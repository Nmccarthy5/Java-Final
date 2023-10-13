import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class CustomerDataGUI {

    // UI elements
    private TextField firstNameField, lastNameField, emailField, phoneField;
    private TableView<Customer> tableView;

    // Method that initializes and displays the GUI
    public void showCustomerData() {
        // Create primary stage
        Stage primaryStage = new Stage();

        // UI layout with grid for form inputs
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // Creating and adding form elements to grid
        gridPane.add(new Label("First Name:"), 0, 0);
        firstNameField = new TextField();
        gridPane.add(firstNameField, 1, 0);

        gridPane.add(new Label("Last Name:"), 0, 1);
        lastNameField = new TextField();
        gridPane.add(lastNameField, 1, 1);

        gridPane.add(new Label("Email:"), 0, 2);
        emailField = new TextField();
        gridPane.add(emailField, 1, 2);

        gridPane.add(new Label("Phone number:"), 0, 3);
        phoneField = new TextField();
        gridPane.add(phoneField, 1, 3);

        // Buttons for actions
        Button submitButton = new Button("Add Customer to database");
        submitButton.setOnAction(event -> {
            Customer customer = new Customer(firstNameField.getText(), lastNameField.getText(), emailField.getText(), phoneField.getText());
            if (CustomerManager.addCustomer(customer)) {
                // Clear the form fields after successful addition
                firstNameField.clear();
                lastNameField.clear();
                emailField.clear();
                phoneField.clear();
            } else {
                // Handle any addition failures (e.g., show an alert or message)
            }
        });
        gridPane.add(submitButton, 0, 4);

        Button queryButton = new Button("Show Records");
        queryButton.setOnAction(event -> {
            List<Customer> customers = CustomerManager.getAllCustomers();
            ObservableList<Customer> observableList = FXCollections.observableArrayList(customers);
            tableView.setItems(observableList);
        });
        gridPane.add(queryButton, 1, 4);

        //Table for displaying customer data
        tableView = new TableView<>();

        // Columns for TableView
        TableColumn<Customer, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setPrefWidth(150);

        TableColumn<Customer, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setPrefWidth(150);

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);  // Set the preferred width for the Email column

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(150);

        // Adding columns to TableView
        tableView.getColumns().add(firstNameCol);
        tableView.getColumns().add(lastNameCol);
        tableView.getColumns().add(emailCol);
        tableView.getColumns().add(phoneCol);
        // Combine grid (form) and TableView in a VBox and show
        VBox root = new VBox(10, gridPane, tableView);
        primaryStage.setTitle("NATO PHOTOGRAPHY DATA");
        primaryStage.setScene(new Scene(root, 650, 400));
        primaryStage.show();
    }

    // Inner class representing a Customer
    public static class Customer {
        private final SimpleStringProperty firstName, lastName, email, phone;

        public Customer(String firstName, String lastName, String email, String phone) {
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.email = new SimpleStringProperty(email);
            this.phone = new SimpleStringProperty(phone);
        }

        // Getters for the properties
        public String getFirstName() { return firstName.get(); }
        public String getLastName() { return lastName.get(); }
        public String getEmail() { return email.get(); }
        public String getPhone() { return phone.get(); }
    }

    static class CustomerManager {
        private static final String FILE_PATH = "customers.txt";

        // Add a customer to the storage
        public static boolean addCustomer(Customer customer) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                writer.write(customer.getFirstName() + "," + customer.getLastName() + "," + customer.getEmail() + "," + customer.getPhone());
                writer.newLine();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        // Fetch all customers from the storage
        public static List<Customer> getAllCustomers() {
            List<Customer> customers = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 4) {
                        customers.add(new Customer(data[0], data[1], data[2], data[3]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return customers;
        }
    }
}