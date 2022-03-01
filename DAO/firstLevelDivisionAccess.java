package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.DBConnection;
import model.firstLevelDivision;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class firstLevelDivisionAccess extends firstLevelDivision {

    public firstLevelDivisionAccess(int divisionID, String divisionName, int country_ID) {
        super(divisionID, divisionName, country_ID);
    }

    /**
     * ObservableList that takes entire first_level_divisions table.
     * @throws SQLException
     * @return firstLevelDivisionsObservableList
     */
    public static ObservableList<firstLevelDivisionAccess> getAllFirstLevelDivisions() throws SQLException {
        ObservableList<firstLevelDivisionAccess> firstLevelDivisionsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from first_level_divisions";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            int country_ID = rs.getInt("COUNTRY_ID");
            firstLevelDivisionAccess firstLevelDivision = new firstLevelDivisionAccess(divisionID, divisionName, country_ID);
            firstLevelDivisionsObservableList.add(firstLevelDivision);
        }
        return firstLevelDivisionsObservableList;
    }

}
