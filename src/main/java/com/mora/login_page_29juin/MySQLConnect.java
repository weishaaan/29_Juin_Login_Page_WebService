package com.mora.login_page_29juin;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class MySQLConnect extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String role;
        Context initCtx = null;
        Context envCtx = null;
        DataSource ds = null;
        Connection connection = null;
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String user_input = request.getParameter("user");
        String pass_input = request.getParameter("pass");
        
        try {
            initCtx = new InitialContext();
            envCtx = (Context) initCtx.lookup("java:comp/env");
            // Look up our data source
            ds = (DataSource) envCtx.lookup("jdbc/DSTest-c3p0");
            // Allocate and use a connection from the pool
            connection = ds.getConnection();
            String sql = "Select user,pass from users where user=? and pass=?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, user_input);
            pst.setString(2, pass_input);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                //RequestDispatcher rd = request.getRequestDispatcher("Welcome");   //class

                PreparedStatement pst_role = connection.prepareStatement("select role from users where user=? and pass=?");
                pst_role.setString(1, user_input);
                pst_role.setString(2, pass_input);
                ResultSet rs_role = pst_role.executeQuery();
                if (rs_role.next()) {
                    role = rs_role.getString("role");
                    System.out.println("role is : " + role);

                    if (role.equals("admin")) {

                        RequestDispatcher rd = request.getRequestDispatcher("/admin.html");  //web page
                        rd.forward(request, response);
                    }
                    if (role.equals("user")) {

                        RequestDispatcher rd = request.getRequestDispatcher("/user.html");  //web page
                        rd.forward(request, response);
                    }
                }
            } else {
                out.println("Username or Password incorrect");
                RequestDispatcher rd = request.getRequestDispatcher("/index.html");
                rd.include(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NamingException ex) {
            Logger.getLogger(MySQLConnect.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                Thread.sleep(5000);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
