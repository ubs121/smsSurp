package surprise;

import java.sql.*;
import oracle.jdbc.pool.*;
/**
 *
 * @author Uuganbayar.S
 */
public class DB {
    private static OracleOCIConnectionPool cpool = null;
    
    static {
        try {
            
            cpool = new OracleOCIConnectionPool();
            cpool.setServerName("server_ip");
            cpool.setDatabaseName("Surprise");
            cpool.setPortNumber(1521);
            cpool.setUser("user");
            cpool.setPassword("password");
            cpool.setNetworkProtocol("tcp");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static int DayFailCount(String isdn) {
        return 0;
    }
    
    public static int DayTransCount(String isdn) {
        int n = 0;
        
        Connection con = null;
        try {
            con = cpool.getConnection();
            PreparedStatement stmt = con.prepareStatement(
                    "select fn_DayTransCount(?) from dual");
            stmt.setString(1, isdn);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next())
                n = rs.getInt(1);
            
            rs.close();
            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {}
            }
        }
        
        return n;
    }
    
    /*
     * CREATE TABLE T_TRANSFER (
        C_ISDN       VARCHAR2(8),
        C_DIRECTION  CHAR(1),
        C_DATE       DATE,
        C_TYPE       CHAR(1),
        C_PARTNER    VARCHAR2(8),
        C_PRICE      FLOAT(126),
        C_BALANCE    FLOAT(126),
        C_DATA       VARCHAR2(160)
       )
     * */
    public static void AddLog(String isdn, String direction, String type,
            String partner, long price, long balance, String data) {
        
        Connection con = null;
        try {
            con = cpool.getConnection();
            con.setAutoCommit(true);
            
            CallableStatement sql = con.prepareCall("{call sp_Log(?,?,?,?,?,?,?)}");
            sql.setString(1, isdn);
            sql.setString(2, direction);
            sql.setString(3, type);
            sql.setString(4, partner);
            sql.setFloat(5, price);
            sql.setFloat(6, balance);
            sql.setString(7, data);
            sql.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {}
            }
        }
    }
    
}
