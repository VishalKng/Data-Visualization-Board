package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PieChart {

    private JTextField item, amount;
    private JPanel mainFrame, panel2, piePanel;
    private JButton PIECHARTButton, RESETButton, DELETEButton, ADDDATAButton;
    private DefaultPieDataset pieDataset;
    private JFreeChart pieChart;
    private ChartPanel chartPanel;
    private DefaultTableModel model;
    private JTable table;
    private JFrame frame;
    private DatabaseManager dbManager;

    public PieChart() {
        // Initialize the DatabaseManager
        dbManager = new DatabaseManager();
        dbManager.createTable(); // Ensure the table exists

        // Setup GUI
        frame = new JFrame("Data Visualization Dashboard");
        mainFrame = new JPanel(new BorderLayout());
        panel2 = new JPanel(new BorderLayout());
        piePanel = new JPanel(new BorderLayout());

        item = new JTextField(15);
        amount = new JTextField(15);
        ADDDATAButton = new JButton("Add Data");
        PIECHARTButton = new JButton("Generate Pie Chart");
        RESETButton = new JButton("Reset");
        DELETEButton = new JButton("Delete Row");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Item:"));
        inputPanel.add(item);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amount);
        inputPanel.add(ADDDATAButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(PIECHARTButton);
        buttonPanel.add(RESETButton);
        buttonPanel.add(DELETEButton);

        mainFrame.add(inputPanel, BorderLayout.NORTH);
        mainFrame.add(panel2, BorderLayout.CENTER);
        mainFrame.add(buttonPanel, BorderLayout.SOUTH);
        mainFrame.add(piePanel, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mainFrame);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        displayTable();
        loadData();

        ADDDATAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemName = item.getText();
                String amountData = amount.getText();

                // Save data into the database
                dbManager.insertData(itemName, Double.parseDouble(amountData));

                // Add data to the table
                Object[] data = {itemName, amountData};
                model.addRow(data);

                // Clear the input fields
                item.setText("");
                amount.setText("");
            }
        });

        PIECHARTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear previous chart if exists
                piePanel.removeAll();
                // Generate and show the pie chart
                showPie();
                frame.validate();
            }
        });

        RESETButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                piePanel.removeAll();
                panel2.removeAll();
                dbManager.deleteAllData(); // Delete data from the database
                model.setRowCount(0); // Clear data from the table model
                displayTable();
                frame.validate();
            }
        });

        // Handle row deletion
        DELETEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) { // Check if a row is selected
                    String itemName = model.getValueAt(selectedRow, 0).toString();

                    // Remove the row from the database
                    dbManager.deleteRow(itemName);

                    // Remove the row from the table
                    model.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a row to delete.");
                }
            }
        });
    }

    public void displayTable() {
        String[] a = {"ITEMS", "AMOUNT"};
        model = new DefaultTableModel(null, a);
        table = new JTable(model);
        panel2.add(new JScrollPane(table));
    }

    public void showPie() {
        pieDataset = new DefaultPieDataset();
        for (int i = 0; i < table.getRowCount(); i++) {
            String name = table.getValueAt(i, 0).toString();
            Double amt = Double.valueOf(table.getValueAt(i, 1).toString());
            pieDataset.setValue(name, amt);
        }
        pieChart = ChartFactory.createPieChart("PIE CHART", pieDataset, true, true, true);
        chartPanel = new ChartPanel(pieChart);
        piePanel.add(chartPanel, BorderLayout.CENTER);
        piePanel.revalidate();
    }

    public void loadData() {
        String sql = "SELECT item, amount FROM Data";

        try (Connection conn = dbManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String itemName = rs.getString("item");
                double amountValue = rs.getDouble("amount");
                Object[] data = {itemName, amountValue};
                model.addRow(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PieChart();
    }
}
