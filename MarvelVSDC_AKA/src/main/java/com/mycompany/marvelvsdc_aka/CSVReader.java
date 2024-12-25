/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.marvelvsdc_aka;

import java.io.*;
import java.util.*;
import java.nio.file.*;

public class CSVReader {
    public static List<Film> readCSVFile(String fileName) {
        List<Film> films = new ArrayList<>();
        String line;
    
        try {
            // Coba beberapa kemungkinan lokasi file
            Path resourcePath = null;
            Path[] possiblePaths = {
                Paths.get("src", "main", "resources", fileName),
                Paths.get("target", "classes", fileName),
                Paths.get("src", "resources", fileName),
                Paths.get("resources", fileName),
                Paths.get(fileName)
            };

            // Cek setiap kemungkinan path
            for (Path path : possiblePaths) {
                if (Files.exists(path)) {
                    resourcePath = path;
                    System.out.println("File found at: " + path.toAbsolutePath());
                    break;
                }
            }

            if (resourcePath == null) {
                System.out.println("File tidak ditemukan di lokasi manapun:");
                for (Path path : possiblePaths) {
                    System.out.println("- Tried: " + path.toAbsolutePath());
                }
                return getDummyData();
            }

            BufferedReader br = new BufferedReader(new FileReader(resourcePath.toFile()));
            
            // Debug: Print header
            String header = br.readLine();
            System.out.println("CSV Header: " + header);
            
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = parseCSVLine(line);
                    // Debug: Print setiap baris yang dibaca
                    System.out.println("Reading line: " + line);
                    System.out.println("Parsed values length: " + values.length);
                    
                    if (values.length >= 18) {
                        films.add(new Film(
                            values[1].trim(),  // title
                            Integer.parseInt(values[2].trim()),  // year
                            values[3].trim(),  // genre
                            Integer.parseInt(values[4].trim()),  // runtime
                            values[5].trim(),  // mpaRating
                            Double.parseDouble(values[6].trim()),  // imdbRating
                            values[17].trim(), // studio
                            values[9].trim()   // director
                        ));
                    } else {
                        System.out.println("Baris tidak memiliki cukup kolom: " + line);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing line: " + line);
                    System.out.println("Error detail: " + e.getMessage());
                }
            }
            br.close();
            
            if (films.isEmpty()) {
                System.out.println("Tidak ada data valid yang ditemukan dalam file CSV");
                return getDummyData();
            } else {
                System.out.println("Berhasil membaca " + films.size() + " film dari CSV");
            }
            
        } catch (IOException e) {
            System.out.println("Error membaca file: " + e.getMessage());
            e.printStackTrace();
            return getDummyData();
        }
        return films;
    }
    
    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString().trim());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        values.add(currentValue.toString().trim());
        
        return values.toArray(new String[0]);
    }
    
    private static List<Film> getDummyData() {
        List<Film> dummy = new ArrayList<>();
        dummy.add(new Film("Captain America", 1944, "Action, Adventure, Sci-Fi", 244, "Approved", 5.5, "MARVEL", "Elmer Clifton"));
        dummy.add(new Film("The Dark Knight", 2008, "Action, Crime, Drama", 152, "PG-13", 9.0, "DC", "Christopher Nolan"));
        dummy.add(new Film("Avengers: Endgame", 2019, "Action, Adventure", 181, "PG-13", 8.4, "MARVEL", "Russo Brothers"));
        dummy.add(new Film("Wonder Woman", 2017, "Action, Adventure", 141, "PG-13", 7.4, "DC", "Patty Jenkins"));
        dummy.add(new Film("Black Panther", 2018, "Action, Adventure", 134, "PG-13", 7.3, "MARVEL", "Ryan Coogler"));
        dummy.add(new Film("Joker", 2019, "Crime, Drama, Thriller", 122, "R", 8.4, "DC", "Todd Phillips"));
        dummy.add(new Film("Daredevil", 2003, "Action, Crime", 103, "PG-13", 5.3, "MARVEL", "Mark Steven Johnson"));
        return dummy;
    }
}