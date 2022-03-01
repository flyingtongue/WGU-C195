package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.DBConnection;
import model.Contacts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class contactAccess {

    /**
     * Create observablelist from all contacts.
     * @throws SQLException
     * @return contactsObservableList
     */
    public static ObservableList<Contacts> getAllContacts() throws SQLException {
        ObservableList<Contacts> contactsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from contacts";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String contactEmail = rs.getString("Email");
            Contacts contact = new Contacts(contactID, contactName, contactEmail);
            contactsObservableList.add(contact);
        }
        return contactsObservableList;
    }

    /**
     * Find contact ID given contact name.
     * @throws SQLException
     * @param contactID
     * @return contactID
     */
    public static String findContactID(String contactID) throws SQLException {
        PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM contacts WHERE Contact_Name = ?");
        ps.setString(1, contactID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            contactID = rs.getString("Contact_ID");
        }
        return contactID;
    }
}