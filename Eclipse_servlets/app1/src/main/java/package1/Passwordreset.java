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


@WebServlet("/Passwordreset")
public class Passwordreset extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String oldPassword = request.getParameter("old_password");
        String newPassword = request.getParameter("new_password");
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC","root","Viraj@9311");
            
            PreparedStatement checkStmt = con.prepareStatement("SELECT * FROM users WHERE name = ? AND password = ?");
            checkStmt.setString(1, username);
            checkStmt.setString(2, oldPassword);
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // Update the password
                PreparedStatement updateStmt = con.prepareStatement(
                    "UPDATE users SET password = ? WHERE name = ? AND password = ?");
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, username);
                updateStmt.setString(3, oldPassword);

                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    out.print("Password updated successfully!");
                } else {
                    out.print("Password update failed!");
                }
            } else {
                out.print("Invalid username or old password!");
            }
            con.close();
        } catch (Exception e) {
            out.print("Error: " + e.getMessage());
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
        return "Sign up page for user";
    }

}
