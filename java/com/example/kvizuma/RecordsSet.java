package com.example.kvizuma;

public class RecordsSet {

    private String rank;
    private String name;
    private int score;

    public RecordsSet(String rank, String name, int score) {
        this.rank = rank;
        this.name = name;
        this.score = score;
    }

    public RecordsSet() {
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        String dots = "";
        String check = rank+". " + name + score;
        do {
            dots = dots + "..";
            check = rank+". " + name + dots + score;
        }
        while(check.length() < 30);

        return
               rank+". " + name + dots +score;
    }
}
