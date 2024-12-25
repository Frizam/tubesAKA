/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.marvelvsdc_aka;


public class SequentialSearch {
    // Metode Sequential Search Iteratif menggunakan index i
    public static Film findBestRatingIterative(Film[] films, String category) {
        Film bestFilm = null;
        double maxRating = 0.0;
        
        for (int i = 0; i < films.length; i++) {
            if (films[i].getStudio().equalsIgnoreCase(category) && films[i].getImdbRating() > maxRating) {
                maxRating = films[i].getImdbRating();
                bestFilm = films[i];
            }
        }
        
        return bestFilm;
    }
    
    // Metode Sequential Search Rekursif
    public static Film findBestRatingRecursive(Film[] films, String category, int n, Film currentBest) {
        // Base case: jika n = 0
        if (n == 0) {
            return currentBest;
        }

        // Periksa elemen ke-(n-1)
        if (films[n - 1].getStudio().equalsIgnoreCase(category) &&
            (currentBest == null || films[n - 1].getImdbRating() > currentBest.getImdbRating())) {
            currentBest = films[n - 1];
        }

        // Recursive case: lanjut ke elemen sebelumnya
        return findBestRatingRecursive(films, category, n - 1, currentBest);
    }
}
