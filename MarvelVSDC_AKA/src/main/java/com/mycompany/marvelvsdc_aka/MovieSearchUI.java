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
    private JComboBox<String> searchMethodComboBox;
    private JSpinner dataLimitSpinner;
    private JTextArea resultArea;
    private JButton applyLimitButton;
    
    public MovieSearchUI() {
        try {
            // Inisialisasi dengan semua data
            films = CSVReader.readCSVFile("mdc - Copy.csv");
            setupUI();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Terjadi error saat inisialisasi: " + e.getMessage(),
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
        
        // Create control panel at the top
        JPanel topControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Add data limit controls
        topControlPanel.add(new JLabel("Jumlah Data:"));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(90, 1, 90, 1);
        dataLimitSpinner = new JSpinner(spinnerModel);
        dataLimitSpinner.setPreferredSize(new Dimension(80, 25));
        topControlPanel.add(dataLimitSpinner);
        
        applyLimitButton = new JButton("Terapkan Limit");
        topControlPanel.add(applyLimitButton);
        
        add(topControlPanel, BorderLayout.NORTH);
        
        // Update table initially with all data
        updateTableData((Integer) dataLimitSpinner.getValue());
        
        // Create and configure table
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create bottom control panel
        JPanel bottomControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Studio selection
        bottomControlPanel.add(new JLabel("Studio:"));
        studioComboBox = new JComboBox<>(new String[]{"MARVEL", "DC"});
        bottomControlPanel.add(studioComboBox);
        
        // Search method selection
        bottomControlPanel.add(new JLabel("Metode Pencarian:"));
        searchMethodComboBox = new JComboBox<>(new String[]{"Iteratif", "Rekursif"});
        bottomControlPanel.add(searchMethodComboBox);
        
        // Search button
        JButton searchButton = new JButton("Cari Film Terbaik");
        bottomControlPanel.add(searchButton);
        
        // Result area
        resultArea = new JTextArea(4, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        bottomControlPanel.add(new JScrollPane(resultArea));
        
        add(bottomControlPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        searchButton.addActionListener(e -> searchBestRating());
        applyLimitButton.addActionListener(e -> updateTableData((Integer) dataLimitSpinner.getValue()));
        
        // Set window size and location
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }
    
    private void updateTableData(int limit) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add limited data to table
        int count = 0;
        for (Film film : films) {
            if (count >= limit) break;
            
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
            count++;
        }
        
        // Update status
        setTitle(String.format("Movie Search Application - Menampilkan %d dari %d film", count, films.size()));
    }
    
    private void searchBestRating() {
        String selectedStudio = (String) studioComboBox.getSelectedItem();
        String searchMethod = (String) searchMethodComboBox.getSelectedItem();
        int limit = (Integer) dataLimitSpinner.getValue();
        
        // Convert list to array with limit
        Film[] filmsArray = films.stream()
                                .limit(limit)
                                .toArray(Film[]::new);
        
        // Perform search based on selected method
        Film bestFilm;
        long startTime = System.nanoTime();
        
        if (searchMethod.equals("Iteratif")) {
            bestFilm = SequentialSearch.findBestRatingIterative(filmsArray, selectedStudio);
        } else {
            bestFilm = SequentialSearch.findBestRatingRecursive(filmsArray, selectedStudio, filmsArray.length, null);
        }
        
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
        
        // Display results
        StringBuilder result = new StringBuilder();
        result.append("Hasil Pencarian ").append(searchMethod).append(":\n");
        result.append("Studio: ").append(selectedStudio).append("\n");
        
        if (bestFilm != null) {
            result.append(String.format("Film Terbaik: %s (%d)\n", 
                bestFilm.getTitle(), 
                bestFilm.getYear()));
            result.append(String.format("IMDb: %.1f, Director: %s\n",
                bestFilm.getImdbRating(),
                bestFilm.getDirector()));
        } else {
            result.append("Tidak ditemukan film untuk studio ").append(selectedStudio);
        }
        
        result.append(String.format("\nRunning Time: %.3f ms", executionTime));
        resultArea.setText(result.toString());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MovieSearchUI().setVisible(true);
        });
    }
}
