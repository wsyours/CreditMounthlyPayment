package demo;

import com.mysql.fabric.jdbc.FabricMySQLDriver;



import java.sql.*;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Сергей on 23.06.2017.
 */
public class DemoApplication {

    private static final String URL = "jdbc:mysql://localhost:3306/cred_clients?useUnicode=true&useSSL=true&useJDBCCompliantTimezoneShift=true" +
            "&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        Connection connection = null;
        Driver driver = null;


        try {
            driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT  client_id FROM cred_users");
            int columns = res.getMetaData().getColumnCount(); // число коллонок

            Scanner scr = new Scanner(System.in);
            System.out.println("Enter your id:");
            int ident = scr.nextInt();
            boolean idIsOk = false;
            boolean pasIsOk = false;

            while (res.next()) {
                if (ident == res.getInt(1)) {
                    idIsOk = true;
                }
            }
            if (idIsOk == true) {
                System.out.println("Your id is correct.");
            } else System.out.println("Your id is not correct.");


            PreparedStatement preparedStatement = connection.prepareStatement(

                    "SELECT client_password FROM cred_users WHERE client_id = ?");
            preparedStatement.setInt(1, ident);

            ResultSet res2 = preparedStatement.executeQuery();


            if (idIsOk == true) {

                System.out.println("Enter your password:");
                int passw = scr.nextInt();

                while (res2.next()) {
                    if (passw == res2.getInt(1)) {
                        pasIsOk = true;
                    }
                }
                if (pasIsOk == true) {
                    System.out.println("Your password is correct.");

                    PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "SELECT * FROM cred_users WHERE client_id = ? AND client_password = ?");
                    preparedStatement2.setInt(1, ident);
                    preparedStatement2.setInt(2, passw);
                    ResultSet res3 = preparedStatement2.executeQuery();

                    PreparedStatement preparedStatement3 = connection.prepareStatement(
                            "SELECT * FROM client_credits WHERE credit_client_id = ?");
                    preparedStatement3.setInt(1, ident);
                    ResultSet res4 = preparedStatement3.executeQuery();

                    System.out.println("You are entered in your profile");

                    while (res3.next()) {
                        System.out.println("Name: " +
                                res3.getString("client_name"));
                        System.out.println("Surname: " +
                                res3.getString("client_surname"));
                        System.out.println("Registration date: " +
                                res3.getDate("client_regdate"));
                        System.out.println();
                    }
                    while (res4.next()) {
                        System.out.println("Credit open date: " +
                                res4.getDate("credit_open_date"));
                        System.out.println("Credit amount, rub: "+
                                res4.getDouble("credit_sum"));
                        System.out.println("Credit interest rate, %: " +
                                res4.getDouble("credit_interest_rate") * 100);
                        System.out.println("Credit term, years: " +
                                (double)res4.getInt("credit_months")/12);
                        System.out.println("");

                    double count1 =  res4.getDouble("credit_sum") *  res4.getDouble("credit_interest_rate") / 12;
                    double count2 = (1 + res4.getDouble("credit_interest_rate")/12);
                    double count3 =  res4.getDouble("credit_months");
                    double count4 =  Math.pow(count2, count3);
                    double count5 =  1 / count4;
                    double count6 =  1 - count5;
                    double count7 =  count1 / count6;

                         Locale locale = new Locale("en");
                         Locale.setDefault(locale);
                         String pattern = "##0.00";
                         DecimalFormat decimalFormat = new DecimalFormat(pattern);


                        System.out.println("Monthly payment, rub: " + decimalFormat.format(count7));
                    }

                } else System.out.println("Your password is not correct.");
            }
            statement.clearBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}