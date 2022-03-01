package controller;

import DAO.appointmentAccess;
import DAO.contactAccess;
import DAO.customerAccess;
import DAO.userAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.DBConnection;
import model.Appointments;
import model.Contacts;
import model.Customers;
import model.Users;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static main.timeUtil.convertTimeDateUTC;

/**
 * appointmentsMain class contains methods for sorting by week, month, and all. Also contains
 * methods to verify: overlapping appointments,
 */

public class appointmentsMain {

    @FXML private RadioButton allAppointmentRadio;
    @FXML private RadioButton appointmentWeekRadio;
    @FXML private RadioButton appointmentMonthRadio;
    @FXML private TableView<Appointments> allAppointmentsTable;
    @FXML private TableColumn<?, ?> appointmentContact;
    @FXML private TableColumn<?, ?> appointmentCustomerID;
    @FXML private TableColumn<?, ?> appointmentDescription;
    @FXML private TableColumn<?, ?> appointmentEnd;
    @FXML private TableColumn<?, ?> appointmentID;
    @FXML private TableColumn<?, ?> appointmentLocation;
    @FXML private TableColumn<?, ?> appointmentStart;
    @FXML private TableColumn<?, ?> appointmentTitle;
    @FXML private TableColumn<?, ?> appointmentType;
    @FXML private TableColumn<?, ?> tableContactID;
    @FXML private TableColumn<?, ?> tableUserID;
    @FXML private Button backToMainMenu;
    @FXML private Button deleteAppointment;
    @FXML private TextField updateAppointmentTitle;
    @FXML private TextField addAppointmentDescription;
    @FXML private TextField addAppointmentType;
    @FXML private TextField addAppointmentCustomerID;
    @FXML private TextField addAppointmentLocation;
    @FXML private TextField updateAppointmentID;
    @FXML private TextField addAppointmentUserID;
    @FXML private DatePicker addAppointmentStartDate;
    @FXML private DatePicker addAppointmentEndDate;
    @FXML private ComboBox<String> addAppointmentStartTime;
    @FXML private ComboBox<String> addAppointmentEndTime;
    @FXML private ComboBox<String> addAppointmentContact;
    @FXML private Button saveAppointment;

    /**
     *
     * Initialize controls and setup variables/observable lists.
     * @throws SQLException
     */
    public void initialize() throws SQLException {

        ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();

        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appointmentStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        tableContactID.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        tableUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));

        allAppointmentsTable.setItems(allAppointmentsList);
    }

    /**
     * Open addAppointments.fxml upon clicking the add button.
     * @param event
     * @throws IOException
     */
    @FXML
    void addAppointment(ActionEvent event) throws IOException {

        Parent addParts = FXMLLoader.load(getClass().getResource("../views/addAppointments.fxml"));
        Scene scene = new Scene(addParts);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();

    }

    /**
     * Navigate back to the main menu on button press.
     * @throws IOException
     * @param event
     */
    @FXML
    void backToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../views/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage)((Node)event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();

    }

    /**
     * Delete appointment on button press.
     * @throws Exception
     * @param event
     */
    @FXML
    void deleteAppointment(ActionEvent event) throws Exception {
        try {
            Connection connection = DBConnection.startConnection();
            int deleteAppointmentID = allAppointmentsTable.getSelectionModel().getSelectedItem().getAppointmentID();
            String deleteAppointmentType = allAppointmentsTable.getSelectionModel().getSelectedItem().getAppointmentType();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected appointment with appointment id: " + deleteAppointmentID + " and appointment type " + deleteAppointmentType);
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                appointmentAccess.deleteAppointment(deleteAppointmentID, connection);

                ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
                allAppointmentsTable.setItems(allAppointmentsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Load appointment data on click.
     * Lambda #3 to fill the allContactNames observable list with contact information.
     */
    @FXML
    void loadAppointment() {
        try {
            DBConnection.startConnection();
            Appointments selectedAppointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

            if (selectedAppointment != null) {

                //get all contact info and fill ComboBox.
                ObservableList<Contacts> contactsObservableList = contactAccess.getAllContacts();
                ObservableList<String> allContactsNames = FXCollections.observableArrayList();
                String displayContactName = "";

                //lambda #3
                contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
                addAppointmentContact.setItems(allContactsNames);

                for (Contacts contact: contactsObservableList) {
                    if (selectedAppointment.getContactID() == contact.getId()) {
                        displayContactName = contact.getContactName();
                    }
                }

                updateAppointmentID.setText(String.valueOf(selectedAppointment.getAppointmentID()));
                updateAppointmentTitle.setText(selectedAppointment.getAppointmentTitle());
                addAppointmentDescription.setText(selectedAppointment.getAppointmentDescription());
                addAppointmentLocation.setText(selectedAppointment.getAppointmentLocation());
                addAppointmentType.setText(selectedAppointment.getAppointmentType());
                addAppointmentCustomerID.setText(String.valueOf(selectedAppointment.getCustomerID()));
                addAppointmentStartDate.setValue(selectedAppointment.getStart().toLocalDate());
                addAppointmentEndDate.setValue(selectedAppointment.getEnd().toLocalDate());
                addAppointmentStartTime.setValue(String.valueOf(selectedAppointment.getStart().toLocalTime()));
                addAppointmentEndTime.setValue(String.valueOf(selectedAppointment.getEnd().toLocalTime()));
                addAppointmentUserID.setText(String.valueOf(selectedAppointment.getUserID()));
                addAppointmentContact.setValue(displayContactName);

                ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

                LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
                LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);

                //if statement fixed issue with infinite loop
                if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
                    while (firstAppointment.isBefore(lastAppointment)) {
                        appointmentTimes.add(String.valueOf(firstAppointment));
                        firstAppointment = firstAppointment.plusMinutes(15);
                    }
                }
                addAppointmentStartTime.setItems(appointmentTimes);
                addAppointmentEndTime.setItems(appointmentTimes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save appointment upon clicking save.
     * @param event
     */
    @FXML
    void saveAppointment(ActionEvent event) {
        try {

            Connection connection = DBConnection.startConnection();

            if (!updateAppointmentTitle.getText().isEmpty() && !addAppointmentDescription.getText().isEmpty() && !addAppointmentLocation.getText().isEmpty() && !addAppointmentType.getText().isEmpty() && addAppointmentStartDate.getValue() != null && addAppointmentEndDate.getValue() != null && !addAppointmentStartTime.getValue().isEmpty() && !addAppointmentEndTime.getValue().isEmpty() && !addAppointmentCustomerID.getText().isEmpty())
            {
                ObservableList<Customers> getAllCustomers = customerAccess.getAllCustomers(connection);
                ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
                ObservableList<userAccess> getAllUsers = userAccess.getAllUsers();
                ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
                ObservableList<Appointments> getAllAppointments = appointmentAccess.getAllAppointments();

                //IDE converted to forEach
                getAllCustomers.stream().map(Customers::getCustomerID).forEach(storeCustomerIDs::add);
                getAllUsers.stream().map(Users::getUserID).forEach(storeUserIDs::add);

                LocalDate localDateEnd = addAppointmentEndDate.getValue();
                LocalDate localDateStart = addAppointmentStartDate.getValue();

                DateTimeFormatter minHourFormat = DateTimeFormatter.ofPattern("HH:mm");

                LocalTime localTimeStart = LocalTime.parse(addAppointmentStartTime.getValue(), minHourFormat);
                LocalTime LocalTimeEnd = LocalTime.parse(addAppointmentEndTime.getValue(), minHourFormat);

                LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
                LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, LocalTimeEnd);

                ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
                ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

                ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

                if (convertStartEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) || convertStartEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue()) || convertEndEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue())  || convertEndEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue()) )
                {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Day is outside of business operations (Monday-Friday)");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("day is outside of business hours");
                    return;
                }

                if (convertStartEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) || convertStartEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0)) || convertEndEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) || convertEndEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0)))
                {
                    System.out.println("time is outside of business hours");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Time is outside of business hours (8am-10pm EST): " + convertStartEST.toLocalTime() + " - " + convertEndEST.toLocalTime() + " EST");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                int newCustomerID = Integer.parseInt(addAppointmentCustomerID.getText());
                int appointmentID = Integer.parseInt(updateAppointmentID.getText());


                if (dateTimeStart.isAfter(dateTimeEnd)) {
                    System.out.println("Appointment has start time after end time");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has start time after end time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                if (dateTimeStart.isEqual(dateTimeEnd)) {
                    System.out.println("Appointment has same start and end time");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has same start and end time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                for (Appointments appointment: getAllAppointments)
                {
                    LocalDateTime checkStart = appointment.getStart();
                    LocalDateTime checkEnd = appointment.getEnd();

                    //"outer verify" meaning check to see if an appointment exists between start and end.
                    if ((newCustomerID == appointment.getCustomerID()) && (appointmentID != appointment.getAppointmentID()) &&
                            (dateTimeStart.isBefore(checkStart)) && (dateTimeEnd.isAfter(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment overlaps with existing appointment.");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("Appointment overlaps with existing appointment.");
                        return;
                    }

                    if ((newCustomerID == appointment.getCustomerID()) && (appointmentID != appointment.getAppointmentID()) &&
//                            Clarification on isEqual is that this does not count as an overlapping appointment
//                            (dateTimeStart.isEqual(checkStart) || dateTimeStart.isAfter(checkStart)) &&
//                            (dateTimeStart.isEqual(checkEnd) || dateTimeStart.isBefore(checkEnd))) {
                            (dateTimeStart.isAfter(checkStart)) && (dateTimeStart.isBefore(checkEnd))) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start time overlaps with existing appointment.");
                            Optional<ButtonType> confirmation = alert.showAndWait();
                            System.out.println("Start time overlaps with existing appointment.");
                            return;
                    }



                    if (newCustomerID == appointment.getCustomerID() && (appointmentID != appointment.getAppointmentID()) &&
//                            Clarification on isEqual is that this does not count as an overlapping appointment
//                            (dateTimeEnd.isEqual(checkStart) || dateTimeEnd.isAfter(checkStart)) &&
//                            (dateTimeEnd.isEqual(checkEnd) || dateTimeEnd.isBefore(checkEnd)))
                        (dateTimeEnd.isAfter(checkStart)) && (dateTimeEnd.isBefore(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "End time overlaps with existing appointment.");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("End time overlaps with existing appointment.");
                        return;
                    }
                }

                String startDate = addAppointmentStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String startTime = addAppointmentStartTime.getValue();

                String endDate = addAppointmentEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endTime = addAppointmentEndTime.getValue();

                String startUTC = convertTimeDateUTC(startDate + " " + startTime + ":00");
                String endUTC = convertTimeDateUTC(endDate + " " + endTime + ":00");

                String insertStatement = "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

                DBConnection.setPreparedStatement(DBConnection.getConnection(), insertStatement);
                PreparedStatement ps = DBConnection.getPreparedStatement();
                ps.setInt(1, Integer.parseInt(updateAppointmentID.getText()));
                ps.setString(2, updateAppointmentTitle.getText());
                ps.setString(3, addAppointmentDescription.getText());
                ps.setString(4, addAppointmentLocation.getText());
                ps.setString(5, addAppointmentType.getText());
                ps.setString(6, startUTC);
                ps.setString(7, endUTC);
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, Integer.parseInt(addAppointmentCustomerID.getText()));
                ps.setInt(11, Integer.parseInt(addAppointmentUserID.getText()));
                ps.setInt(12, Integer.parseInt(contactAccess.findContactID(addAppointmentContact.getValue())));
                ps.setInt(13, Integer.parseInt(updateAppointmentID.getText()));

                System.out.println("ps " + ps);
                ps.execute();

                ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
                allAppointmentsTable.setItems(allAppointmentsList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * When radio button for "All appointments" is selected.
     * @param event
     * @throws SQLException
     */
    @FXML
    void appointmentAllSelected(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();

            if (allAppointmentsList != null)
                for (model.Appointments appointment : allAppointmentsList) {
                    allAppointmentsTable.setItems(allAppointmentsList);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * When radio button for "Month" is selected.
     * @throws SQLException
     */
    @FXML
    void appointmentMonthSelected(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
            ObservableList<Appointments> appointmentsMonth = FXCollections.observableArrayList();

            LocalDateTime currentMonthStart = LocalDateTime.now().minusMonths(1);
            LocalDateTime currentMonthEnd = LocalDateTime.now().plusMonths(1);


            if (allAppointmentsList != null)
                //IDE converted to forEach
                allAppointmentsList.forEach(appointment -> {
                    if (appointment.getEnd().isAfter(currentMonthStart) && appointment.getEnd().isBefore(currentMonthEnd)) {
                        appointmentsMonth.add(appointment);
                    }
                    allAppointmentsTable.setItems(appointmentsMonth);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * When radio button for week is selected.
     * @throws SQLException
     */
    @FXML
    void appointmentWeekSelected(ActionEvent event) throws SQLException {
        try {

            ObservableList<Appointments> allAppointmentsList = appointmentAccess.getAllAppointments();
            ObservableList<Appointments> appointmentsWeek = FXCollections.observableArrayList();

            LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
            LocalDateTime weekEnd = LocalDateTime.now().plusWeeks(1);

            if (allAppointmentsList != null)
                //IDE converted forEach
                allAppointmentsList.forEach(appointment -> {
                    if (appointment.getEnd().isAfter(weekStart) && appointment.getEnd().isBefore(weekEnd)) {
                        appointmentsWeek.add(appointment);
                    }
                    allAppointmentsTable.setItems(appointmentsWeek);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}