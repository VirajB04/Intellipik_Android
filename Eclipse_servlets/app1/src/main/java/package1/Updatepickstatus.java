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
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/Updatepickstatus")
public class Updatepickstatus extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            // Read JSON data from request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            JSONObject requestJson = new JSONObject(requestBody);

            // Parse data from JSON request
            int id = requestJson.optInt("id", -1);
            int code = requestJson.optInt("code", -1);
            String date = requestJson.optString("date", null);
            String name = requestJson.optString("name", null);
            int no_of_kits = requestJson.optInt("no_of_kits", 0); // Default to 0
            int no_of_kits_picked = requestJson.optInt("no_of_kits_picked", 0); // Default to 0
            String status = requestJson.optString("status", null);

            // Log the id and input values for debugging
            System.out.println("Updating pick order with ID: " + id);
            System.out.println("Code: " + code + ", Date: " + date + ", Name: " + name + 
                               ", No of Kits: " + no_of_kits + ", No of Kits Picked: " + 
                               no_of_kits_picked + ", Status: " + status);

            // Validate inputs
            if (name == null || name.trim().isEmpty() || date == null || date.trim().isEmpty() || 
                code <= 0 || status == null || status.trim().isEmpty() || id <= 0) {
                jsonResponse.put("program_status", "error");
                jsonResponse.put("message", "Required fields are missing or invalid.");
                out.print(jsonResponse.toString());
                return;
            }

            // Database connection
            try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC",
                "root", "Viraj@9311");
                PreparedStatement ps = con.prepareStatement(
                "UPDATE pick_order SET date = ?, name = ?, no_of_kits = ?, no_of_kits_picked = ?, status = ?, code = ? WHERE id = ?")) {
                
                ps.setString(1, date);
                ps.setString(2, name);
                ps.setInt(3, no_of_kits);
                ps.setInt(4, no_of_kits_picked);
                ps.setString(5, status);
                ps.setInt(6, code);
                ps.setInt(7, id);

                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    jsonResponse.put("program_status", "success");
                    jsonResponse.put("message", "Pick order updated successfully!");
                } else {
                    jsonResponse.put("program_status", "error");
                    jsonResponse.put("message", "Pick order not found!"); // No rows updated
                }
            }

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();  // Log SQL exceptions
            try {
                jsonResponse.put("program_status", "error");
                jsonResponse.put("message", sqlEx.getMessage());
            } catch (JSONException jsonException) {
                jsonException.printStackTrace(); // Log JSON handling errors
            }
        } catch (Exception e) {
            e.printStackTrace();  // Log general exceptions
            try {
                jsonResponse.put("program_status", "error");
                jsonResponse.put("message", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
            } catch (JSONException jsonException) {
                jsonException.printStackTrace(); // Log JSON handling errors
            }
        } finally {
            out.print(jsonResponse.toString());
            out.close();
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
        return "updating an item from pick status list";
    }
}
