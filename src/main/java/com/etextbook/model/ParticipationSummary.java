// model/ParticipationSummary.java
package com.etextbook.model;

public class ParticipationSummary {
    private int totalPoints;
    private int maxPoints;
    private int activitiesCompleted;
    private int perfectScores;
    private double averageScore;
    private int rank;

    // Constructor
    public ParticipationSummary() {}

    // Getters and Setters
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { 
        this.totalPoints = totalPoints; 
    }

    public int getMaxPoints() { return maxPoints; }
    public void setMaxPoints(int maxPoints) { 
        this.maxPoints = maxPoints; 
    }

    public int getActivitiesCompleted() { return activitiesCompleted; }
    public void setActivitiesCompleted(int activitiesCompleted) { 
        this.activitiesCompleted = activitiesCompleted; 
    }

    public int getPerfectScores() { return perfectScores; }
    public void setPerfectScores(int perfectScores) { 
        this.perfectScores = perfectScores; 
    }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { 
        this.averageScore = averageScore; 
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { 
        this.rank = rank; 
    }
}