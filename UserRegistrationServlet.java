package event_management;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UserRegistrationServlet")
public class UserRegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private String jdbcURL = "jdbc:mysql://localhost:3306/event_management"; 
    private String jdbcUsername = "root";   // your DB username
    private String jdbcPassword = "";       // your DB password

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get form parameters (names must match your HTML input names)
        String name = request.getParameter("user_name");
        String password = request.getParameter("user_pass");
        String email = request.getParameter("user_email");
        String mobile = request.getParameter("user_mobile");

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

            // SQL query matching your table/columns
            String sql = "INSERT INTO user (user_name, password, email_id, mobile_no) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, password);  // ⚠️ Should hash in real apps
            statement.setString(3, email);
            statement.setString(4, mobile);

            // Execute insert
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
            	// ... (inside the if block for successful login)
            	response.sendRedirect("user dashboard.html");
                response.getWriter().println("User Registered Successfully ✅");
            } else {
                response.getWriter().println("Registration failed!");
            }

            // Close resources
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
