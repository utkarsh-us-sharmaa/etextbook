// model/ActivityStatistics.java
package com.etextbook.model;

public class ActivityStatistics {
    private int totalAttempts;
    private double averageScore;
    private int perfectScores;

    // Constructors
    public ActivityStatistics() {}

    // Getters and Setters
    public int getTotalAttempts() { return totalAttempts; }
    public void setTotalAttempts(int totalAttempts) { 
        this.totalAttempts = totalAttempts; 
    }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { 
        this.averageScore = averageScore; 
    }

    public int getPerfectScores() { return perfectScores; }
    public void setPerfectScores(int perfectScores) { 
        this.perfectScores = perfectScores; 
    }
}