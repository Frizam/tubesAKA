/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.marvelvsdc_aka;

public class Film {
    private String title;
    private int year;
    private String genre;
    private int runtime;
    private String mpaRating;
    private double imdbRating;
    private String studio;
    private String director;
    
    public Film(String title, int year, String genre, int runtime, 
                String mpaRating, double imdbRating, String studio, String director) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.runtime = runtime;
        this.mpaRating = mpaRating;
        this.imdbRating = imdbRating;
        this.studio = studio;
        this.director = director;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getMpaRating() {
        return mpaRating;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public String getStudio() {
        return studio;
    }

    public String getDirector() {
        return director;
    }

    
}
