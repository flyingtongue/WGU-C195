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
 * Class and methods to add individual appointments.
 */
public class addAppointments {

    @FXML private TextField addAppointmentCustomerID;
    @FXML private TextField addAppointmentUserID;
    @FXML private TextField addAppointmentDescription;
    @FXML private DatePicker addAppointmentEndDate;
    @FXML private ComboBox<String> addAppointmentEndTime;
    @FXML private TextField addAppointmentID;
    @FXML private TextField addAppointmentLocation;
    @FXML private Button addAppointmentSave;
    @FXML private DatePicker addAppointmentStartDate;
    @FXML private ComboBox<String> addAppointmentStartTime;
    @FXML private TextField addAppointmentTitle;
    @FXML private ComboBox<String> addAppointmentContact;
    @FXML private Button addAppointmentsCancel;
    @FXML private TextField addAppointmentType;

    /**
     * Initialize controls and fill start and end time boxes in 15 minute increments.
     * Contains lambda #2 that replaces for loop to add getContactName() to the allContactsNames observabelist.
     * @throws SQLException
     */
    @FXML
    public void initialize() throws SQLException {

        ObservableList<Contacts> contactsObservableList = contactAccess.getAllContacts();
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();

        // lambda #2
        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
        // here
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
        addAppointmentContact.setItems(allContactsNames);

    }

    /**
     *
     * Saves appointment upon clicking save.
     * @throws IOException
     * @param event
     */
    @FXML
    void addAppointmentSave(ActionEvent event) throws IOException {
        try {

            Connection connection = DBConnection.startConnection();

            if (!addAppointmentTitle.getText().isEmpty() && !addAppointmentDescription.getText().isEmpty() && !addAppointmentLocation.getText().isEmpty() && !addAppointmentType.getText().isEmpty() && addAppointmentStartDate.getValue() != null && addAppointmentEndDate.getValue() != null && !addAppointmentStartTime.getValue().isEmpty() && !addAppointmentEndTime.getValue().isEmpty() && !addAppointmentCustomerID.getText().isEmpty()) {

                ObservableList<Customers> getAllCustomers = customerAccess.getAllCustomers(connection);
                ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
                ObservableList<userAccess> getAllUsers = userAccess.getAllUsers();
                ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
                ObservableList<Appointments> getAllAppointments = appointmentAccess.getAllAppointments();

                //IDE converted
                getAllCustomers.stream().map(Customers::getCustomerID).forEach(storeCustomerIDs::add);
                getAllUsers.stream().map(Users::getUserID).forEach(storeUserIDs::add);

                LocalDate localDateEnd = addAppointmentEndDate.getValue();
                LocalDate localDateStart = addAppointmentStartDate.getValue();

                DateTimeFormatter minHourFormat = DateTimeFormatter.ofPattern("HH:mm");
                String appointmentStartDate = addAppointmentStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String appointmentStartTime = addAppointmentStartTime.getValue();

                String endDate = addAppointmentEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endTime = addAppointmentEndTime.getValue();

                System.out.println("thisDate + thisStart " + appointmentStartDate + " " + appointmentStartTime + ":00");
                String startUTC = convertTimeDateUTC(appointmentStartDate + " " + appointmentStartTime + ":00");
                String endUTC = convertTimeDateUTC(endDate + " " + endTime + ":00");

                LocalTime localTimeStart = LocalTime.parse(addAppointmentStartTime.getValue(), minHourFormat);
                LocalTime LocalTimeEnd = LocalTime.parse(addAppointmentEndTime.getValue(), minHourFormat);

                LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
                LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, LocalTimeEnd);

                ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
                ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

                ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

                LocalTime startAppointmentTimeToCheck = convertStartEST.toLocalTime();
                LocalTime endAppointmentTimeToCheck = convertEndEST.toLocalTime();

                DayOfWeek startAppointmentDayToCheck = convertStartEST.toLocalDate().getDayOfWeek();
                DayOfWeek endAppointmentDayToCheck = convertEndEST.toLocalDate().getDayOfWeek();

                int startAppointmentDayToCheckInt = startAppointmentDayToCheck.getValue();
                int endAppointmentDayToCheckInt = endAppointmentDayToCheck.getValue();

                int workWeekStart = DayOfWeek.MONDAY.getValue();
                int workWeekEnd = DayOfWeek.FRIDAY.getValue();

                LocalTime estBusinessStart = LocalTime.of(8, 0, 0);
                LocalTime estBusinessEnd = LocalTime.of(22, 0, 0);

                if (startAppointmentDayToCheckInt < workWeekStart || startAppointmentDayToCheckInt > workWeekEnd || endAppointmentDayToCheckInt < workWeekStart || endAppointmentDayToCheckInt > workWeekEnd) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Day is outside of business operations (Monday-Friday)");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("day is outside of business hours");
                    return;
                }

                if (startAppointmentTimeToCheck.isBefore(estBusinessStart) || startAppointmentTimeToCheck.isAfter(estBusinessEnd) || endAppointmentTimeToCheck.isBefore(estBusinessStart) || endAppointmentTimeToCheck.isAfter(estBusinessEnd))
                {
                    System.out.println("time is outside of business hours");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Time is outside of business hours (8am-10pm EST): " + startAppointmentTimeToCheck + " - " + endAppointmentTimeToCheck + " EST");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                int newAppointmentID = Integer.parseInt(String.valueOf((int) (Math.random() * 100)));
                int customerID = Integer.parseInt(addAppointmentCustomerID.getText());

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
                    if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
                            (dateTimeStart.isBefore(checkStart)) && (dateTimeEnd.isAfter(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment overlaps with existing appointment.");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("Appointment overlaps with existing appointment.");
                        return;
                    }

                    if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
//                            Clarification on isEqual is that this does not count as an overlapping appointment
//                            (dateTimeStart.isEqual(checkStart) || dateTimeStart.isAfter(checkStart)) &&
//                            (dateTimeStart.isEqual(checkEnd) || dateTimeStart.isBefore(checkEnd))) {
                            (dateTimeStart.isAfter(checkStart)) && (dateTimeStart.isBefore(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start time overlaps with existing appointment.");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("Start time overlaps with existing appointment.");
                        return;
                    }



                    if (customerID == appointment.getCustomerID() && (newAppointmentID != appointment.getAppointmentID()) &&
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

                String insertStatement = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                DBConnection.setPreparedStatement(DBConnection.getConnection(), insertStatement);
                PreparedStatement ps = DBConnection.getPreparedStatement();
                ps.setInt(1, newAppointmentID);
                ps.setString(2, addAppointmentTitle.getText());
                ps.setString(3, addAppointmentDescription.getText());
                ps.setString(4, addAppointmentLocation.getText());
                ps.setString(5, addAppointmentType.getText());
                //ps.setTimestamp(6, Timestamp.valueOf(startLocalDateTimeToAdd));
                ps.setTimestamp(6, Timestamp.valueOf(startUTC));
                ps.setTimestamp(7, Timestamp.valueOf(endUTC));
                //need to verify this is correct
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(11, 1);
                ps.setInt(12, Integer.parseInt(addAppointmentCustomerID.getText()));
                ps.setInt(13, Integer.parseInt(contactAccess.findContactID(addAppointmentContact.getValue())));
                ps.setInt(14, Integer.parseInt(contactAccess.findContactID(addAppointmentUserID.getText())));

                //System.out.println("ps " + ps);
                ps.execute();
            }

            Parent root = FXMLLoader.load(getClass().getResource("../views/appointments.fxml"));
            Scene scene = new Scene(root);
            Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
            MainScreenReturn.setScene(scene);
            MainScreenReturn.show();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Cancel button is pressed and returns back to the appointment main screen.
     * @throws SQLException
     */
    @FXML
    public void addAppointmentsCancel (ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("../views/appointments.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage)((Node)event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();
    }
}
