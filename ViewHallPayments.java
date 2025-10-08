package event_management;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ViewHallPayments")
public class ViewHallPayments extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private String jdbcURL = "jdbc:mysql://localhost:3306/event_management";
    private String jdbcUsername = "root";    // 🔹 Change if different
    private String jdbcPassword = "";        // 🔹 Add your MySQL password

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

            stmt = con.createStatement();
            String sql = "SELECT * FROM hall_payment";
            rs = stmt.executeQuery(sql);

            // HTML Table
            out.println("<html><head><title>Hall Payments</title>");
            out.println("<style>");
            out.println("table { border-collapse: collapse; width: 90%; margin: 20px auto; font-family: Arial, sans-serif; }");
            out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; color: #333; }");
            out.println("h2 { text-align: center; color: #444; }");
            out.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println("</style>");
            out.println("</head><body>");

            out.println("<h2> Hall Payments</h2>");
            out.println("<table>");
            out.println("<tr><th>ID</th><th>Event Name</th><th>Date</th><th>Time</th><th>Total Persons</th><th>Rent</th><th>Created At</th><th>Payment Method</th><th>Status</th></tr>");

            while (rs.next()) {
                int id = rs.getInt("hall_payment_id");
                String eventName = rs.getString("event_name");
                String date = rs.getString("date");
                String time = rs.getString("time");
                int totalPersons = rs.getInt("total_persons");
                int rent = rs.getInt("rent");
                String createAt = rs.getString("create_at");
                String paymentMethod = rs.getString("payment_mathod");
                String status = rs.getString("status");

                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + eventName + "</td>");
                out.println("<td>" + date + "</td>");
                out.println("<td>" + time + "</td>");
                out.println("<td>" + totalPersons + "</td>");
                out.println("<td>" + rent + "</td>");
                out.println("<td>" + createAt + "</td>");
                out.println("<td>" + paymentMethod + "</td>");
                out.println("<td>" + status + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("<div style='text-align:center; margin-top:20px;'>");
            out.println("<a href='admin dashboard.html'> Go Back To Admin Dashbord</a>");
            out.println("</div>");
            out.println("</body></html>");

        } catch (Exception e) {
            out.println("<h2>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace(out);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace(out);
            }
        }
    }
}