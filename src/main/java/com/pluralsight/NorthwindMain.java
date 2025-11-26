package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class NorthwindMain {
    public static void main(String[] args) {
        // Check for command line arguments for username and password
        if (args.length != 2) {
            System.out.println("Application needs two arguments to run: " +
                    "java com.pluralsight.Main <username> <password>");
            System.exit(1);
        }

        String username = args[0];
        String password = args[1];

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("What do you want to do?");
            System.out.println("1) Display all products");
            System.out.println("2) Display all customers");
            System.out.println("3) Display all categories");
            System.out.println("0) Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    displayAllProducts(username, password);
                    break;
                case 2:
                    displayAllCustomers(username, password);
                    break;
                case 3:
                    displayAllCategories(username, password, scanner);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
            scanner.close();
        }
    }

    private static void displayAllProducts(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String url = "jdbc:mysql://localhost:3306/northwind";
        String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM products";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                int productId = results.getInt("ProductID");
                String productName = results.getString("ProductName");
                double unitPrice = results.getDouble("UnitPrice");
                int unitsInStock = results.getInt("UnitsInStock");

                System.out.println("Product ID: " + productId);
                System.out.println("Product Name: " + productName);
                System.out.println("Unit Price: " + unitPrice);
                System.out.println("Units In Stock: " + unitsInStock);
                System.out.println("-----------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayAllCustomers(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        String url = "jdbc:mysql://localhost:3306/northwind";
        String query = "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                String contactName = results.getString("ContactName");
                String companyName = results.getString("CompanyName");
                String city = results.getString("City");
                String country = results.getString("Country");
                String phone = results.getString("Phone");

                System.out.println("Contact Name: " + contactName);
                System.out.println("Company Name: " + companyName);
                System.out.println("City: " + city);
                System.out.println("Country: " + country);
                System.out.println("Phone: " + phone);
                System.out.println("-----------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayAllCategories(String username, String password, Scanner scanner) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String url = "jdbc:mysql://localhost:3306/northwind";

        String query = "SELECT CategoryID, CategoryName FROM categories ORDER BY CategoryID";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet results = preparedStatement.executeQuery()) {

            System.out.println("=== Categories ===");
            while (results.next()) {
                int categoryID = results.getInt("CategoryID");
                String categoryName = results.getString("CategoryName");
                System.out.println("Category ID: " + categoryID);
                System.out.println("Category Name: " + categoryName);
                System.out.println("-----------------------------------------");
            }

            int selectedCategoryId;
            while (true) {
                System.out.print("Enter a Category ID to see its products: ");
                if (scanner.hasNextInt()) {
                    selectedCategoryId = scanner.nextInt();
                    if (selectedCategoryId > 0) {
                        break;
                    } else {
                        System.out.println("Please enter a positive Category ID.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a numeric Category ID.");
                    scanner.next();
                }
            }

            String productQuery =
                    """
                            SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM products
                            WHERE CategoryID = ? ORDER BY ProductID""";

            try (PreparedStatement prepareStatement = connection.prepareStatement(productQuery)) {
                prepareStatement.setInt(1, selectedCategoryId);

                try (ResultSet resultSet = prepareStatement.executeQuery()) {
                    System.out.println("=== Products in Category " + selectedCategoryId + " ===");
                    boolean exists = false;

                    while (resultSet.next()) {
                        exists = true;
                        int productId = resultSet.getInt("ProductID");
                        String productName = resultSet.getString("ProductName");
                        double unitPrice = resultSet.getDouble("UnitPrice");
                        int unitsInStock = resultSet.getInt("UnitsInStock");

                        System.out.println("Product ID: " + productId);
                        System.out.println("Product Name: " + productName);
                        System.out.println("Unit Price: " + unitPrice);
                        System.out.println("Units In Stock: " + unitsInStock);
                        System.out.println("-----------------------------------------");
                    }

                    if (!exists) {
                        System.out.println("No products found for that category.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
