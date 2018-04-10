package Controllers;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import static java.time.DayOfWeek.*;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class DBCalls {

    private static MysqlDataSource getDS() {
        MysqlDataSource ds = new MysqlConnectionPoolDataSource();
        ds.setURL("jdbc:mysql://localhost:3306/Company");
        ds.setUser("user");
        ds.setPassword("");
        return ds;
    }

    private static void closeConnection(PreparedStatement stmt, Connection con) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static JSONArray findEmployee(String id, String fname, String lname) {
        String idSearch = "";
        String fSearch = "";
        String lSearch = "";
        JSONArray result = new JSONArray();

        if (id != "") {
            idSearch = "emp_id='" + id + "'";
            if (fname != "")
                fSearch = " and first_name='" + fname + "'";
            if (lname != "")
                lSearch = " and last_name='" + lname + "'";
        } else {
            if (fname != "") {
                fSearch = "first_name='" + fname + "'";
                if (lname != "")
                    lSearch = " and last_name='" + lname + "'";
            } else
                lSearch = "last_name='" + lname + "'";
        }

        String sql = "SELECT emp_id, first_name, last_name, rate, withholdings FROM Employee_Details WHERE " + idSearch + fSearch + lSearch;

        result = getRecords(sql);

        return result;
    }

    protected static JSONArray getHours(String id) {

        JSONArray result = new JSONArray();

        String sql = "select week, sun, mon, tue, wed, thu, " +
                "fri, sat, (sun + mon + tue + wed + thu + fri + sat) as total from Employee_Hours " +
                "where processed <> 'Y' and emp_id = '" + id + "'" +
                "order by week;";

        result = getRecords(sql);

        return result;
    }

    private static JSONArray getRecords(String sql) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JSONArray jsonArray = new JSONArray();

        try {
            con = getDS().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            ResultSetMetaData rm = rs.getMetaData();
            int cnum = rm.getColumnCount();

            while (rs.next()) {
                LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<String, String>();
                for (int i = 1; i <= cnum; i++) {
                    jsonOrderedMap.put(rm.getColumnLabel(i).toLowerCase(), rs.getString(i));
                }
                JSONObject orderedJson = new JSONObject(jsonOrderedMap);
                jsonArray.put(orderedJson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(stmt, con);
        }

        return jsonArray;
    }

    protected static JSONObject insertHours(String id, String week, String sun, String mon, String tue, String wed,
                                            String thu, String fri, String sat) throws ParseException, JSONException {
        Connection con = null;
        PreparedStatement stmt = null;
        int rs;
        JSONObject response = new JSONObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate inputDate = LocalDate.parse(week, formatter).with(previousOrSame(SUNDAY));

        java.sql.Date date = Date.valueOf(inputDate);

        if (inputDate.isBefore(LocalDate.now()) || inputDate.isEqual(LocalDate.now())) {
            String sql = "insert into Company.Employee_Hours " +
                    "(week,emp_id,sun,mon,tue,wed,thu,fri,sat) " +
                    "values" +
                    "('" + date + "','" + id + "','" + sun + "','" + mon + "','" +
                    tue + "','" + wed + "','" + thu + "','" + fri + "','" + sat + "')" +
                    "ON DUPLICATE KEY UPDATE sun='" + sun + "',mon='" + mon + "',tue='" +
                    tue + "',wed='" + wed + "',thu='" + thu + "',fri='" + fri + "',sat='" + sat + "'";

            try {
                con = getDS().getConnection();
                stmt = con.prepareStatement(sql);
                rs = stmt.executeUpdate();
                if (rs > 0) {
                    String msg = (rs == 1) ? "record inserted" : "record updated";
                    response = new JSONObject("{state:successful,msg:" + msg + "}");
                } else
                    response = new JSONObject("{state:failed,msg:error in insert}");

            } catch (SQLException e) {
                response = new JSONObject("{state:failed,msg:" + e.getMessage() + "}");
                e.printStackTrace();
            } finally {
                closeConnection(stmt, con);
            }
        } else {
            response = new JSONObject("{state:failed,msg:Selected week cannot be in the future}");
        }

        return response;
    }

}
