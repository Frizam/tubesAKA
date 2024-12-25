/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.marvelvsdc_aka;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MovieSearchUI extends JFrame {
    private JTable table;
    private List<Film> films;
    private DefaultTableModel tableModel;
    private JComboBox<String> studioComboBox;
    private JTextArea resultArea;
    
    public MovieSearchUI() {
        try {
            films = CSVReader.readCSVFile("mdc - Copy.csv");
            if (films.size() > 0) {
                System.out.println("Berhasil memuat " + films.size() + " film");
            } else {
                System.out.println("Tidak ada film yang dimuat");
            }
            setupUI();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error saat inisialisasi: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setupUI() {
        setTitle("Movie Search Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create table model
        String[] columns = {"Title", "Year", "Genre", "Runtime", "Rating", "IMDb", "Studio", "Director"};
        tableModel = new DefaultTableModel(columns, 0);
        
        // Fill table with data
        for (Film film : films) {
            Object[] row = {
                film.getTitle(),
                film.getYear(),
                film.getGenre(),
                film.getRuntime(),
                film.getMpaRating(),
                film.getImdbRating(),
                film.getStudio(),
                film.getDirector()
            };
            tableModel.addRow(row);
        }
        
        // Create and configure table
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        studioComboBox = new JComboBox<>(new String[]{"MARVEL", "DC"});
        JButton searchButton = new JButton("Find Best Rating");
        resultArea = new JTextArea(3, 40);
        resultArea.setEditable(false);
        
        controlPanel.add(new JLabel("Select Studio:"));
        controlPanel.add(studioComboBox);
        controlPanel.add(searchButton);
        controlPanel.add(new JScrollPane(resultArea));
        
        add(controlPanel, BorderLayout.SOUTH);
        
        // Add action listener for search button
        searchButton.addActionListener(e -> searchBestRating());
        
        // Set window size and location
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void searchBestRating() {
        String selectedStudio = (String) studioComboBox.getSelectedItem();
        Film[] filmsArray = films.toArray(new Film[0]);
        
        // Search using both methods
        Film bestIterative = SequentialSearch.findBestRatingIterative(filmsArray, selectedStudio);
        Film bestRecursive = SequentialSearch.findBestRatingRecursive(filmsArray, selectedStudio, filmsArray.length, null);
        
        // Display results
        StringBuilder result = new StringBuilder();
        result.append("Best ").append(selectedStudio).append(" Movie:\n");
        if (bestIterative != null) {
            result.append(String.format("%s (%d) - IMDb: %.1f\nDirected by: %s",
                bestIterative.getTitle(),
                bestIterative.getYear(),
                bestIterative.getImdbRating(),
                bestIterative.getDirector()));
        } else {
            result.append("No movies found for ").append(selectedStudio);
        }
        
        resultArea.setText(result.toString());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MovieSearchUI().setVisible(true);
        });
    }
}