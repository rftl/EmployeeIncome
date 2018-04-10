package Controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "Controllers.Records")
public class Records extends HttpServlet {

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
            result = DBCalls.insertHours(id, week, sun, mon, tue, wed, thu, fri, sat);
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

            JSONArray result = DBCalls.findEmployee(id, fname, lname);

            response.getWriter().print(result);

        } else if (request.getParameter("type").equals("hours")) {

            String id = request.getParameter("id");

            JSONArray result = DBCalls.getHours(id);

            response.getWriter().print(result);
        }
    }

}
