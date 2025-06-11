package package1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.io.PrintWriter;

@WebServlet("/login")
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("name");
        String password = request.getParameter("password") ;
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC","root","Viraj@9311");
            
            PreparedStatement preparestatement = con.prepareStatement("select * from users where BINARY name=? and BINARY password=?");
            preparestatement.setString(1,username);
            preparestatement.setString(2,password);
            
            ResultSet rs = preparestatement.executeQuery();
            if(rs.next()){
                out.print("Login successful");
            }else{
                out.print("Failed to login");
            }
            con.close();
            
        }catch(Exception e){
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
        return "login page for user";
    }
     
}
