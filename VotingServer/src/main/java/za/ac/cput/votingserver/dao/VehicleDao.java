package za.ac.cput.votingserver.dao;

/**
 *
 * @author hsboo
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import za.ac.cput.votingserver.connection.DBconnection;
import za.ac.cput.votingserver.domain.Car;

public class VehicleDao {

    private static PreparedStatement pstatement;
    private static Connection native_con;
    private static ResultSet rset;

    public VehicleDao() {
        native_con = DBconnection.createConnection();
    }

    public static void createVehiclesTable() {
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS VEHICLES ("
                    + "CAR_NAME VARCHAR(255) NOT NULL, "
                    + "CATEGORY VARCHAR(255) NOT NULL, "
                    + "VOTES INT DEFAULT 0, "
                    + "PRIMARY KEY (carname))";

            native_con = DBconnection.createConnection();
            pstatement = native_con.prepareStatement(createTableSQL);
            pstatement.executeUpdate();
            System.out.println("TABLE CREATED");
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * SQL--INSERT TABLES: **ADMIN PREVILEGE** ADMINISTRATOR
     * CREDENTIALS{Admin_Credentials} VEHICLE{Vehicles}
     *
     */
    public static int insertVehiclesintoTable(Car car) {
        int status = 0;
        try {
            native_con = DBconnection.createConnection();

            String insertQuery = "insert into VEHICLES(CAR_NAME , CATEGORY) values (?,?)";
            pstatement = native_con.prepareStatement(insertQuery);
            pstatement.setString(1, car.getVehicle_name());
            pstatement.setString(2, car.getCategory());

            status = pstatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {
            closeSQLObjects();
        }
        return status;
    }

    /**
     * SQL--LIST/VIEW VEHICLES[VOTER & ADMIN] Place categories of cars in
     * Combobox Place all cars in tables
     */
    public static ArrayList<Car> getAllVehicles() {
        ArrayList<Car> statistics_DB = new ArrayList();
        try {
            native_con = DBconnection.createConnection();

            String GET_VEHICLES_QUERY = "select *from VEHICLES";
            pstatement = native_con.prepareStatement(GET_VEHICLES_QUERY);
            rset = pstatement.executeQuery();
            while (rset.next()) {
                String name = rset.getString(1);
                String category = rset.getString(2);
                int votes = rset.getInt(3);
                Car car = new Car(name, category, votes);
                statistics_DB.add(car);
            }
            closeSQLObjects();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return statistics_DB;
    }

    /**
     * SQL--DELETE VEHICLE FROM DATABASE[ADMIN]
     */
    public static int deleteCarByName(String CAR_NAME) {
        int status = 0;
        String DELETE_CAR_QUERY = "delete from VEHICLES where CAR_NAME = ?";
        try {
            pstatement = native_con.prepareStatement(DELETE_CAR_QUERY);
            pstatement.setString(1, CAR_NAME);
            status = pstatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return status;
    }

    /**
     * ADMIN VALIDATION
     */
    public static boolean validate_Admin_Access(String username, String password) {
        boolean status = false;
        try {
            native_con = DBconnection.createConnection();
            String VALIDATION_QUERY = "select * from AdminLogin where username = ? and  password = ?";
            pstatement = native_con.prepareStatement(VALIDATION_QUERY);
            pstatement.setString(1, username);
            pstatement.setString(2, password);
            rset = pstatement.executeQuery();
            status = rset.next();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "SQL ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rset != null) {
                try {
                    native_con.close();
                    rset.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex, "SQL ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        return status;
    }

    /**
     * UPDATE VOTES WHEN VOTED FOR A CAR
     */
    public static int updateVotes(String carName) {
        int status = 0;
        try {
            native_con = DBconnection.createConnection();

            String UPDATE_VOTEs_QUERY = "UPDATE VEHICLES SET VOTES = VOTES + 1 WHERE CAR_NAME = ?";
            pstatement = native_con.prepareStatement(UPDATE_VOTEs_QUERY);
            pstatement.setString(1, carName);
            status = pstatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return status;
    }//end of method

    /**
     * CLOSE ALL CONNECTIONS
     */
    private static void closeSQLObjects() {
        if (rset != null) {
            try {
                native_con.close();
                rset.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }
}
