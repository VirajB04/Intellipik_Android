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
import org.json.JSONObject;
import java.io.BufferedReader;
import java.sql.Statement;

@WebServlet("/Deleteitemlist")
public class Deleteitemlist extends HttpServlet {
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
            JSONObject requestJson = new JSONObject(sb.toString());

            int id = requestJson.getInt("id");

            // Database connection and deletion
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC",
                "root", "Viraj@9311");

            String deleteQuery = "DELETE FROM item_list WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(deleteQuery);
            ps.setInt(1, id);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
            	Statement statement = con.createStatement();

                // Reset row number variable
                String resetRowNumber = "SET @row_number = 0";
                statement.execute(resetRowNumber);

                // Rearrange the IDs
                String rearrangeQuery = "UPDATE item_list SET id = (@row_number := @row_number + 1) ORDER BY id";
                statement.executeUpdate(rearrangeQuery);

                //Reset the auto-increment counter
                String resetAutoIncrement = "ALTER TABLE item_list AUTO_INCREMENT = 1";
                statement.executeUpdate(resetAutoIncrement);

                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Item deleted successfully!");
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Item not found!");
            }

            out.print(jsonResponse.toString());
            con.close();

        } catch (Exception e) {
        	try {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", e.getMessage());
        	}catch (Exception jsonException){
        		out.print("{\"status\":\"error\",\"message\":\"An unexpected error occurred.\"}");
                return; 
        	}
            out.print(jsonResponse.toString());
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
        return "Servlet for deleting items";
    }

}
