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
import java.sql.Statement;

import org.json.JSONObject;


@WebServlet("/Deletepickstatus")
public class Deletepickstatus extends HttpServlet {
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
            System.out.println("Request Body: " + requestBody); // Debugging log

            JSONObject requestJson = new JSONObject(requestBody);

            // Extract ID to delete
            int id = requestJson.getInt("id");
            System.out.println("ID to delete: " + id); // Debugging log

            // Database connection and deletion
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC",
                    "root", "Viraj@9311")) {

                con.setAutoCommit(false); // Start transaction

                // Delete the item with the provided ID
                String deleteQuery = "DELETE FROM pick_order WHERE id = ?";
                try (PreparedStatement ps = con.prepareStatement(deleteQuery)) {
                    ps.setInt(1, id);

                    int rowsDeleted = ps.executeUpdate();
                    if (rowsDeleted > 0) {
                        // Reset and rearrange IDs
                        try (Statement statement = con.createStatement()) {
                            String resetRowNumber = "SET @row_number = 0";
                            statement.execute(resetRowNumber);

                            String rearrangeQuery = "UPDATE pick_order SET id = (@row_number := @row_number + 1) ORDER BY id";
                            statement.executeUpdate(rearrangeQuery);

                            String resetAutoIncrement = "ALTER TABLE pick_order AUTO_INCREMENT = 1";
                            statement.executeUpdate(resetAutoIncrement);
                        }

                        // Success response
                        jsonResponse.put("program_status", "success");
                        jsonResponse.put("message", "Item deleted and IDs rearranged successfully!");
                    } else {
                        // Item not found
                        jsonResponse.put("program_status", "error");
                        jsonResponse.put("message", "Item not found!");
                    }
                }

                con.commit(); // Commit transaction
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonResponse.put("program_status", "error");
                jsonResponse.put("message", e.getMessage());
            } catch (Exception jsonException) {
                out.print("{\"program_status\":\"error\",\"message\":\"An unexpected error occurred.\"}");
                return;
            }
        }

        out.print(jsonResponse.toString());
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
        return "Servlet for deleting items in pick status";
    }
    
}