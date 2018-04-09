import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.time.format.DateTimeFormatter;

import static java.time.DayOfWeek.*;
import static java.time.temporal.TemporalAdjusters.*;


@WebServlet(name = "Records")
public class Records extends HttpServlet {

    private static MysqlDataSource getDS() {
        MysqlDataSource ds = new MysqlConnectionPoolDataSource();
        ds.setURL("jdbc:mysql://localhost:3306/Company");
        ds.setUser("user");
        ds.setPassword("");
        return ds;
    }

    private static JSONArray getRecords(String id, String fname, String lname) {
        String test = "";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String idSearch = "";
        String fSearch = "";
        String lSearch = "";
        JSONArray jsonArray = new JSONArray();

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

        System.out.println(sql);

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
        System.out.println("TEST: " + test);
        return jsonArray;
    }

    private static JSONObject insertHours(String id, String week, String sun, String mon, String tue, String wed,
                                          String thu, String fri, String sat) throws ParseException, JSONException {
        String test = "";
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

            System.out.println(sql);

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
        } else {
            response = new JSONObject("{state:failed,msg:Selected week cannot be in the future}");
        }

        return response;
    }

    private static JSONArray getHours(String id) {
        String test = "";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JSONArray jsonArray = new JSONArray();

        String sql = "select week, sun, mon, tue, wed, thu, " +
                "fri, sat, (sun + mon + tue + wed + thu + fri + sat) as total from Employee_Hours " +
                "where processed <> 'Y' and emp_id = '" + id + "'" +
                "order by week;";

        System.out.println(sql);

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
        return jsonArray;
    }

    private String fixBlanks(String input) {
        String output = (input == "") ? "0" : input;
        return output;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        String week = request.getParameter("week");
        String sun = fixBlanks(request.getParameter("sun"));
        String mon = fixBlanks(request.getParameter("mon"));
        String tue = fixBlanks(request.getParameter("tue"));
        String wed = fixBlanks(request.getParameter("wed"));
        String thu = fixBlanks(request.getParameter("thu"));
        String fri = fixBlanks(request.getParameter("fri"));
        String sat = fixBlanks(request.getParameter("sat"));
        JSONObject result = new JSONObject();

        try {
            result = insertHours(id, week, sun, mon, tue, wed, thu, fri, sat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().print(result);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (request.getParameter("type").equals("records")) {

            String id = request.getParameter("id");
            String fname = request.getParameter("fname");
            String lname = request.getParameter("lname");

            JSONArray result = getRecords(id, fname, lname);

            response.getWriter().print(result);
        } else if (request.getParameter("type").equals("hours")) {
            String id = request.getParameter("id");

            JSONArray result = getHours(id);

            response.getWriter().print(result);
        }
    }

}
