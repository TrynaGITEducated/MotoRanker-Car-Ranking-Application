package za.ac.cput.votingserver.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author hsboo
 */
public class DBconnection {

    public DBconnection() {
        createConnection();
    }

    public static Connection createConnection() {
        Connection connectobj = null;
        String dbURL = "jdbc:derby://localhost:1527/VotingSystem";
        String username = "NebulaIO";
        String password = "administrator";
        try {
            connectobj = DriverManager.getConnection(dbURL, username, password);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return connectobj;
    }
}
