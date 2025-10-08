package event_management;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Product")
public class Product extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials (UPDATE THESE WITH YOUR ACTUAL CONFIGURATION)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/event_management";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // 1. Get ALL parameters
        String itemDetails = request.getParameter("item_name");
        String quantityStr = request.getParameter("quantity");
        String totalAmountStr = request.getParameter("total_amount");
        String mobileNumber = request.getParameter("mobile_no");
        String addressDetails = request.getParameter("address");
        String paymentMethod = request.getParameter("payment_method");

        // Prepare data variables
        int quantity = 0;
        double totalAmount = 0.0;
        double unitPrice = 0.0;

        // Prepare bill-specific details
        String invoiceId = "PROD-" + System.currentTimeMillis(); 
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // Dummy date_time for DB (since form doesn't provide it)
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        Connection con = null;
        PreparedStatement psBooking = null;
        PreparedStatement psPayment = null;

        try {
            // 2. Input Validation and Parsing
            if (quantityStr == null || quantityStr.trim().isEmpty() || 
                totalAmountStr == null || totalAmountStr.trim().isEmpty() ||
                itemDetails == null || itemDetails.trim().isEmpty()) {
                
                throw new IllegalArgumentException("Missing required item, quantity, or total amount details.");
            }

            // SAFELY PARSE numbers
            quantity = Integer.parseInt(quantityStr.trim());
            totalAmount = Double.parseDouble(totalAmountStr.trim());
            
            if (quantity <= 0 || totalAmount <= 0) {
                 throw new IllegalArgumentException("Quantity and Total Amount must be positive values.");
            }
            
            unitPrice = totalAmount / quantity;

            // 3. Database Connection and Transaction Setup
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false); // Start transaction

            // 4. Insert into booking table (Assuming 'event_item_booking' has a 'date_time' column)
            // If your table DOES NOT have a 'date_time' column, remove it from the SQL and the psBooking.setString(4) line.
            String sqlBooking = "INSERT INTO event_item_booking (machine_name, quantity, total_amount, date_time, mobile_no, address) VALUES (?, ?, ?, ?, ?, ?)";
            psBooking = con.prepareStatement(sqlBooking);
            psBooking.setString(1, itemDetails);
            psBooking.setInt(2, quantity);
            psBooking.setDouble(3, totalAmount);
            psBooking.setString(4, currentDateTime); // Use current time for the record
            psBooking.setString(5, mobileNumber);
            psBooking.setString(6, addressDetails);
            psBooking.executeUpdate();

            // 5. Insert into payments table
            String sqlPayment = "INSERT INTO payments (item_details, mobile_number, address_details, payment_method) VALUES (?, ?, ?, ?)";
            psPayment = con.prepareStatement(sqlPayment);
            psPayment.setString(1, itemDetails + " (Qty: " + quantity + ", Total: " + totalAmount + ")");
            psPayment.setString(2, mobileNumber);
            psPayment.setString(3, addressDetails);
            psPayment.setString(4, paymentMethod);
            psPayment.executeUpdate();

            // 6. Finalize Transaction
            con.commit(); // Commit if both inserts succeeded

            // ------------------------------------------------------------------
            // --- 7. HTML Bill Generation (Success Response) ---
            // ------------------------------------------------------------------
            
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Product Order Confirmation</title>");
            
            // Inline CSS for Bill Presentation
            out.println("<style>");
            out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 20px; }");
            out.println(".container { max-width: 700px; margin: 20px auto; background: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); border-top: 5px solid #ffcc00; }");
            out.println("h1.logo { text-align: center; color: #333; margin-bottom: 5px; font-size: 2.5em; }");
            out.println("h2 { color: #cc9900; text-align: center; margin-bottom: 25px; border-bottom: 2px solid #ffcc00; padding-bottom: 10px; }");
            out.println(".info-section { margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 8px; }");
            out.println(".info-section p { margin: 5px 0; font-size: 1em; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
            out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            out.println("th { background-color: #fcf8e3; color: #333; }");
            out.println("tfoot td { font-weight: bold; background-color: #fff0b3; }");
            out.println(".total-row td { font-size: 1.2em; color: #cc9900; }");
            out.println(".print-btn, .home-btn { display: block; width: 250px; margin: 15px auto 10px auto; padding: 12px 20px; color: white; text-align: center; border: none; border-radius: 5px; cursor: pointer; font-size: 1.1em; text-decoration: none; transition: background-color 0.3s; }");
            out.println(".print-btn { background-color: #007bff; }");
            out.println(".print-btn:hover { background-color: #0056b3; }");
            out.println(".home-btn { background-color: #4CAF50; }");
            out.println(".home-btn:hover { background-color: #45a049; }");
            out.println("@media print { .print-btn, .home-btn { display: none; } .container { box-shadow: none; border: none; } }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("<div class='container'>");
            out.println("<h1 class='logo'>VIP Traders</h1>");
            out.println("<h2>Order Confirmation & Bill</h2>");
            
            // Header Info
            out.println("<div class='info-section'>");
            out.println("<p><strong>Invoice ID:</strong> " + invoiceId + "</p>");
            out.println("<p><strong>Date Generated:</strong> " + currentDate + "</p>");
            out.println("<p><strong>Payment Status:</strong> Successful</p>");
            out.println("</div>");

            // Customer Info
            out.println("<div class='info-section'>");
            out.println("<h3>Delivery & Payment Details</h3>");
            out.println("<p><strong>Mobile No:</strong> " + mobileNumber + "</p>");
            out.println("<p><strong>Delivery Address:</strong> " + addressDetails + "</p>");
            out.println("<p><strong>Payment Method:</strong> " + paymentMethod + "</p>");
            out.println("</div>");
            
            // Bill Details Table
            out.println("<h3>Order Summary</h3>");
            out.println("<table>");
            out.println("<thead><tr><th>Product Name</th><th>Unit Price </th><th>Quantity</th><th>Subtotal </th></tr></thead>");
            out.println("<tbody>");
            out.println("<tr>");
            out.println("<td>" + itemDetails + "</td>");
            out.println("<td>" + String.format("%.2f", unitPrice) + "</td>");
            out.println("<td>" + quantity + "</td>");
            out.println("<td>" + String.format("%.2f", totalAmount) + "</td>");
            out.println("</tr>");
            out.println("</tbody>");
            out.println("<tfoot>");
            out.println("<tr class='total-row'><td colspan='3'><strong>TOTAL AMOUNT PAID</strong></td><td><strong>" + String.format("%.2f", totalAmount) + "</strong></td></tr>");
            out.println("</tfoot>");
            out.println("</table>");
            
            // Print Button (Allows Save as PDF)
            out.println("<button class='print-btn' onclick='window.print()'> Print / Save as PDF</button>");
            
            // Button to return to the product page
            out.println("<a href='product.html' class='home-btn'> Continue Shopping</a>");

            out.println("</div>"); // .container
            out.println("</body>");
            out.println("</html>");


        } catch (ClassNotFoundException | SQLException e) {
            // Handle SQL/DB errors and roll back
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace(out);
            }
            e.printStackTrace(out);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<!DOCTYPE html><html><body><div class='container' style='background:#f9e8e8; border-top: 5px solid #cc0000;'><h2> Server Error</h2><p>An issue occurred while saving to the database. Please check your console for details or contact support.</p><a href='product.html' class='home-btn' style='background-color:#cc0000;'> Return to Products</a></div></body></html>");
        
        } catch (NumberFormatException e) {
            // Fix for Java 6: Separate NumberFormatException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("<!DOCTYPE html><html><body><div class='container' style='background:#f9e8e8; border-top: 5px solid #ffc107;'><h2> Invalid Number Input</h2><p>Please ensure Quantity and Total Amount are valid numbers. Error: " + e.getMessage() + "</p><a href='product.html' class='home-btn' style='background-color:#ffc107;'> Return to Products</a></div></body></html>");

        } catch (IllegalArgumentException e) {
            // Fix for Java 6: Separate IllegalArgumentException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("<!DOCTYPE html><html><body><div class='container' style='background:#f9e8e8; border-top: 5px solid #ffc107;'><h2> Invalid Input</h2><p>The form data was incomplete or invalid. Error: " + e.getMessage() + "</p><a href='product.html' class='home-btn' style='background-color:#ffc107;'>← Return to Products</a></div></body></html>");
        } finally {
            // Close resources
            try { if (psBooking != null) psBooking.close(); } catch (Exception e) {}
            try { if (psPayment != null) psPayment.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
}