package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.DBConnection;
import model.Customers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class customerAccess {

    /**
     * Observablelist that takes all customer data.
     * @throws SQLException
     * @return customersObservableList
     */
    public static ObservableList<Customers> getAllCustomers(Connection connection) throws SQLException {

        String query = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, customers.Division_ID, first_level_divisions.Division from customers INNER JOIN  first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        ObservableList<Customers> customersObservableList = FXCollections.observableArrayList();

            while (rs.next()) {
                int customerID = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String customerAddress = rs.getString("Address");
                String customerPostalCode = rs.getString("Postal_Code");
                String customerPhone = rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");
                String divisionName = rs.getString("Division");
                Customers customer = new Customers(customerID, customerName, customerAddress, customerPostalCode, customerPhone, divisionID, divisionName);
                customersObservableList.add(customer);
        }
        return customersObservableList;
    }


}
