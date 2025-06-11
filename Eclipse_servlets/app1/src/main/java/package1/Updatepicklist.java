package package1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.json.JSONObject;

@WebServlet("/Updatepicklist")
public class Updatepicklist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject jsonResponse = new JSONObject();
        try {
            // Read JSON data from request body
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            JSONObject requestJson = new JSONObject(requestBody);
            
            
            int id = requestJson.getInt("id");
            String name = requestJson.getString("name");
            String code = requestJson.getString("code");
            int no_of_items = requestJson.getInt("no_of_items");
            if (name == null || code == null || name.trim().isEmpty() || code.trim().isEmpty()) {
                throw new IllegalArgumentException("Name or code or no_of_items parameter is missing or empty.");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC",
                "root", "Viraj@9311");

            String updateQuery = "UPDATE pick_list SET name = ?, code = ? , no_of_items = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(updateQuery);
            ps.setString(1, name);
            ps.setString(2, code);
            ps.setInt(3, no_of_items);
            ps.setInt(4, id);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Item list updated successfully!");
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Item not found!");
            }

            out.print(jsonResponse.toString());
            con.close();

        } catch (Exception e) {
            try {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", e.getMessage());
                out.print(error.toString());
            } catch (Exception jsonException) {
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
        return "updating an item from pick list";
    }
}
