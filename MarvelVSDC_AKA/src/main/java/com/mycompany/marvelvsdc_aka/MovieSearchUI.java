/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.marvelvsdc_aka;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class MovieSearchUI extends JFrame {
    private JTable table;
    private List<Film> films;
    private DefaultTableModel tableModel;
    private JComboBox<String> studioComboBox;
    private JComboBox<String> searchMethodComboBox;
    private JSpinner dataLimitSpinner;
    private JTextArea resultArea;
    private JButton applyLimitButton;
    private DefaultCategoryDataset dataset; // Dataset untuk grafik

    public MovieSearchUI() {
        try {
            films = CSVReader.readCSVFile("mdc - Copy.csv");
            dataset = new DefaultCategoryDataset(); // Inisialisasi dataset
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
        
        // Panel atas untuk kontrol data limit
        JPanel topControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topControlPanel.add(new JLabel("Jumlah Data:"));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(90, 1, 90, 1);
        dataLimitSpinner = new JSpinner(spinnerModel);
        dataLimitSpinner.setPreferredSize(new Dimension(80, 25));
        topControlPanel.add(dataLimitSpinner);
        applyLimitButton = new JButton("Terapkan Limit");
        topControlPanel.add(applyLimitButton);
        add(topControlPanel, BorderLayout.NORTH);
        
        // Tabel untuk menampilkan data film
        String[] columns = {"Title", "Year", "Genre", "Runtime", "Rating", "IMDb", "Studio", "Director"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel bawah untuk kontrol tambahan
        JPanel bottomControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomControlPanel.add(new JLabel("Studio:"));
        studioComboBox = new JComboBox<>(new String[]{"MARVEL", "DC"});
        bottomControlPanel.add(studioComboBox);
        bottomControlPanel.add(new JLabel("Metode Pencarian:"));
        searchMethodComboBox = new JComboBox<>(new String[]{"Iteratif", "Rekursif"});
        bottomControlPanel.add(searchMethodComboBox);
        JButton searchButton = new JButton("Cari Film Terbaik");
        bottomControlPanel.add(searchButton);

        // Tombol untuk menampilkan grafik
        JButton chartButton = new JButton("Lihat Grafik");
        bottomControlPanel.add(chartButton);

        // Tombol untuk menambahkan data ke grafik
        JButton addToChartButton = new JButton("Tambah Data ke Grafik");
        bottomControlPanel.add(addToChartButton);
        
        // Area hasil pencarian
        resultArea = new JTextArea(4, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        bottomControlPanel.add(new JScrollPane(resultArea));
        add(bottomControlPanel, BorderLayout.SOUTH);
        
        // Action listeners
        searchButton.addActionListener(e -> searchBestRating());
        applyLimitButton.addActionListener(e -> updateTableData((Integer) dataLimitSpinner.getValue()));
        chartButton.addActionListener(e -> showPerformanceChart(-1)); // -1 untuk menampilkan grafik penuh
        addToChartButton.addActionListener(e -> {
            int limit = (Integer) dataLimitSpinner.getValue();
            showPerformanceChart(limit);
        });
        
        // Ukuran window
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

    private void showPerformanceChart(int limit) {
        if (limit > 0) {
            // Konversi list ke array dengan batasan data
            Film[] filmsArray = films.stream()
                                     .limit(limit)
                                     .toArray(Film[]::new);

            // Ukur waktu eksekusi metode iteratif
            long startTime = System.nanoTime();
            SequentialSearch.findBestRatingIterative(filmsArray, "MARVEL");
            long endTime = System.nanoTime();
            double executionTimeIterative = (endTime - startTime) / 1_000_000.0;

            // Ukur waktu eksekusi metode rekursif
            startTime = System.nanoTime();
            SequentialSearch.findBestRatingRecursive(filmsArray, "MARVEL", filmsArray.length, null);
            endTime = System.nanoTime();
            double executionTimeRecursive = (endTime - startTime) / 1_000_000.0;

            // Tambahkan data ke dataset
            dataset.addValue(executionTimeIterative, "Iteratif", Integer.toString(limit));
            dataset.addValue(executionTimeRecursive, "Rekursif", Integer.toString(limit));
        }

        // Buat grafik
        JFreeChart chart = ChartFactory.createLineChart(
            "Performance vs Input Size",
            "Input Size (Jumlah Film)",
            "Execution Time (ms)",
            dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));

        // Tampilkan dalam dialog
        JFrame chartFrame = new JFrame("Performance Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.getContentPane().add(chartPanel);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MovieSearchUI().setVisible(true);
        });
    }
}
