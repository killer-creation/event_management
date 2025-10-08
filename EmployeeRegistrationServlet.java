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

@WebServlet("/EmployeeRegistrationServlet")
public class EmployeeRegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection details
    private String jdbcURL = "jdbc:mysql://localhost:3306/event_management"; 
    private String jdbcUsername = "root";     // your MySQL username
    private String jdbcPassword = "";         // your MySQL password

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get all form values (must match HTML input names)
        String empName = request.getParameter("emp_name");
        String empPassword = request.getParameter("emp_password");
        String empEmail = request.getParameter("emp_email");
        String empMobile = request.getParameter("emp_mobile");
        String empAddress = request.getParameter("address");

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

            // SQL query (match your employee table column names)
            String sql = "INSERT INTO employe (emp_name, password, email_id, mobile_no, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Set values
            statement.setString(1, empName);
            statement.setString(2, empPassword);  // ⚠️ Should hash in real apps
            statement.setString(3, empEmail);
            statement.setString(4, empMobile);
            statement.setString(5, empAddress);

            // Execute insert
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                response.getWriter().println("<h2> Employee Registered Successfully</h2>");
            	response.sendRedirect("user dashboard.html");
            } else {
                response.getWriter().println("<h2>❌ Registration Failed</h2>");
            }

            // Close resources
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h2>Error: " + e.getMessage() + "</h2>");
        }
    }
}
