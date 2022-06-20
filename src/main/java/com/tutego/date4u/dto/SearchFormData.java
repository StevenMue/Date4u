package com.tutego.date4u.dto;

public class SearchFormData {

    int startYear;
    int endYear;
    int startLength;
    int endLength;
    int gender;

    public SearchFormData() {
    }

    public SearchFormData(int startYear, int endYear, int startLength, int endLength, int gender) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.startLength = startLength;
        this.endLength = endLength;
        this.gender = gender;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getStartLength() {
        return startLength;
    }

    public void setStartLength(int startLength) {
        this.startLength = startLength;
    }

    public int getEndLength() {
        return endLength;
    }

    public void setEndLength(int endLength) {
        this.endLength = endLength;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "SearchFormData{" +
                "startYear=" + startYear +
                ", endYear=" + endYear +
                ", startLength=" + startLength +
                ", endLength=" + endLength +
                ", gender=" + gender +
                '}';
    }
}
