package event_management;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ViewProductOrders1")
public class ViewProductOrders1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private String jdbcURL = "jdbc:mysql://localhost:3306/event_management";
    private String jdbcUsername = "root";    // Change if different
    private String jdbcPassword = "";        // Add your MySQL password

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

            // Get the 'timePeriod' parameter from the request
            String timePeriod = request.getParameter("timePeriod");
            
            // Start with a base SQL query
            String sql = "SELECT * FROM product_order";

            // If a time period is selected, build the WHERE clause
            if (timePeriod != null && !timePeriod.isEmpty()) {
                LocalDate currentDate = LocalDate.now();
                LocalDate startDate;

                switch (timePeriod) {
                    case "1month":
                        startDate = currentDate.minusMonths(1);
                        sql += " WHERE created_at >= '" + startDate + "'";
                        break;
                    case "3month":
                        startDate = currentDate.minusMonths(3);
                        sql += " WHERE created_at >= '" + startDate + "'";
                        break;
                    case "1year":
                        startDate = currentDate.minusYears(1);
                        sql += " WHERE created_at >= '" + startDate + "'";
                        break;
                }
            }

            rs = stmt.executeQuery(sql);

            // HTML Table
            out.println("<html><head><title>Product Orders</title>");
            out.println("<style>");
            out.println("table { border-collapse: collapse; width: 80%; margin: 20px auto; font-family: Arial, sans-serif; }");
            out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; color: #333; }");
            out.println("h2 { text-align: center; color: #444; }");
            out.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println("</style>");
            out.println("</head><body>");

            out.println("<h2> Product Orders</h2>");
            out.println("<table>");
            out.println("<tr><th>Order ID</th><th>Product Name</th><th>Quantity</th><th>Total Amount</th><th>Mobile No</th><th>Address</th><th>Created At</th></tr>");

            while (rs.next()) {
                int id = rs.getInt("product_id");
                String productName = rs.getString("product_name");
                int quantity = rs.getInt("quantity");
                double totalAmount = rs.getDouble("total_amount");
                String mobileNo = rs.getString("mobile_no");
                String address = rs.getString("address");
                String createdAt = rs.getString("created_at");

                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + productName + "</td>");
                out.println("<td>" + quantity + "</td>");
                out.println("<td>" + totalAmount + "</td>");
                out.println("<td>" + mobileNo + "</td>");
                out.println("<td>" + address + "</td>");
                out.println("<td>" + createdAt + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("<div style='text-align:center; margin-top:20px;'>");
            out.println("<a href='admin dashboard.html'>Go Back to admin dashboard</a>");
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
