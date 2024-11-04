package com.etextbook.model;

public class Activity {
    private Integer activityId;
    private String question;
    private String correctAnswer;
    private String incorrectAnswer1;
    private String incorrectAnswer2;
    private String incorrectAnswer3;
    private String explanationCorrect;
    private String explanationIncorrect1;
    private String explanationIncorrect2;
    private String explanationIncorrect3;
    private Integer contentBlockId;

    // Constructors
    public Activity() {}

    public Activity(Integer activityId, String question, String correctAnswer,
                   String incorrectAnswer1, String incorrectAnswer2, String incorrectAnswer3,
                   Integer contentBlockId) {
        this.activityId = activityId;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswer1 = incorrectAnswer1;
        this.incorrectAnswer2 = incorrectAnswer2;
        this.incorrectAnswer3 = incorrectAnswer3;
        this.contentBlockId = contentBlockId;
    }

    // Getters and Setters
    public Integer getActivityId() { return activityId; }
    public void setActivityId(Integer activityId) { this.activityId = activityId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getIncorrectAnswer1() { return incorrectAnswer1; }
    public void setIncorrectAnswer1(String incorrectAnswer1) { this.incorrectAnswer1 = incorrectAnswer1; }

    public String getIncorrectAnswer2() { return incorrectAnswer2; }
    public void setIncorrectAnswer2(String incorrectAnswer2) { this.incorrectAnswer2 = incorrectAnswer2; }

    public String getIncorrectAnswer3() { return incorrectAnswer3; }
    public void setIncorrectAnswer3(String incorrectAnswer3) { this.incorrectAnswer3 = incorrectAnswer3; }

    public String getExplanationCorrect() { return explanationCorrect; }
    public void setExplanationCorrect(String explanationCorrect) { this.explanationCorrect = explanationCorrect; }

    public String getExplanationIncorrect1() { return explanationIncorrect1; }
    public void setExplanationIncorrect1(String explanationIncorrect1) { this.explanationIncorrect1 = explanationIncorrect1; }

    public String getExplanationIncorrect2() { return explanationIncorrect2; }
    public void setExplanationIncorrect2(String explanationIncorrect2) { this.explanationIncorrect2 = explanationIncorrect2; }

    public String getExplanationIncorrect3() { return explanationIncorrect3; }
    public void setExplanationIncorrect3(String explanationIncorrect3) { this.explanationIncorrect3 = explanationIncorrect3; }

    public Integer getContentBlockId() { return contentBlockId; }
    public void setContentBlockId(Integer contentBlockId) { this.contentBlockId = contentBlockId; }

    @Override
    public String toString() {
        return "Activity{" +
                "activityId=" + activityId +
                ", question='" + question + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", incorrectAnswer1='" + incorrectAnswer1 + '\'' +
                ", incorrectAnswer2='" + incorrectAnswer2 + '\'' +
                ", incorrectAnswer3='" + incorrectAnswer3 + '\'' +
                ", explanationCorrect='" + explanationCorrect + '\'' +
                ", explanationIncorrect1='" + explanationIncorrect1 + '\'' +
                ", explanationIncorrect2='" + explanationIncorrect2 + '\'' +
                ", explanationIncorrect3='" + explanationIncorrect3 + '\'' +
                ", contentBlockId=" + contentBlockId +
                '}';
    }
}
