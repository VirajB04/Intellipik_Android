package package1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;


@WebServlet("/AddItemlist")
public class AddItemlist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String code = request.getParameter("code");

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC", 
                "root", "Viraj@9311")) {

            // Insert new item if name and code are provided
            if (name != null && code != null) {
                String insertQuery = "INSERT INTO item_list (item_name, code) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(insertQuery)) {
                    ps.setString(1, name);
                    ps.setString(2, code);
                    ps.executeUpdate();
                }
            }

            // Fetch updated list of items
            String selectQuery = "SELECT * FROM item_list";
            try (PreparedStatement ps = con.prepareStatement(selectQuery);
                 ResultSet rs = ps.executeQuery()) {

                JSONArray jsonArray = new JSONArray();

                while (rs.next()) {
                    JSONObject item = new JSONObject();
                    item.put("id", rs.getInt("id"));
                    item.put("name", rs.getString("item_name"));
                    item.put("code", rs.getString("code"));
                    jsonArray.put(item);
                }

                JSONObject result = new JSONObject();
                result.put("status", "success");
                result.put("data", jsonArray);

                out.print(result.toString());
            }
        } catch (Exception e) {
            try {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", e.getMessage());
                out.print(error.toString());
            } catch (JSONException jsonException) {
                // Handle JSONException if needed
                out.print("{\"status\":\"error\",\"message\":\"Failed to generate error response\"}");
            }
        }
    }

	
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
	
	@Override
    public String getServletInfo() {
        return "Adding an item to itemlist";
    }
   

}
