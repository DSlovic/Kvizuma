package com.example.kvizuma;

import java.util.Arrays;

public class CountrySet {
    private int id;
    private String countryName;
    private String countryCapitol;
    private byte[] countryBorderImg;
    private byte[] countryFlagImg;
    private byte[] countryBorderCapitolImg;
    private int countryContinent;

    public CountrySet(int id, String countryName, String countryCapitol, byte[] countryBorderImg, byte[] countryFlagImg, byte[] countryBorderCapitolImg, int isCountrySmall) {
        this.id = id;
        this.countryName = countryName;
        this.countryCapitol = countryCapitol;
        this.countryBorderImg = countryBorderImg;
        this.countryFlagImg = countryFlagImg;
        this.countryBorderCapitolImg = countryBorderCapitolImg;
        this.countryContinent = isCountrySmall;
    }

    public CountrySet() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCapitol() {
        return countryCapitol;
    }

    public void setCountryCapitol(String countryCapitol) {
        this.countryCapitol = countryCapitol;
    }

    public byte[] getCountryBorderImg() {
        return countryBorderImg;
    }

    public void setCountryBorderImg(byte[] countryBorderImg) {
        this.countryBorderImg = countryBorderImg;
    }

    public byte[] getCountryFlagImg() {
        return countryFlagImg;
    }

    public void setCountryFlagImg(byte[] countryFlagImg) {
        this.countryFlagImg = countryFlagImg;
    }

    public byte[] getCountryBorderCapitolImg() {
        return countryBorderCapitolImg;
    }

    public void setCountryBorderCapitolImg(byte[] countryBorderCapitolImg) {
        this.countryBorderCapitolImg = countryBorderCapitolImg;
    }

    public int isCountryContinent() {
        return countryContinent;
    }

    public void setCountryContinent(int countryContinent) {
        this.countryContinent = countryContinent;
    }

    @Override
    public String toString() {
        return "CountrySet{" +
                "id=" + id +
                ", countryName='" + countryName + '\'' +
                ", countryCapitol='" + countryCapitol + '\'' +
                ", countryBorderImg=" + Arrays.toString(countryBorderImg) +
                ", countryFlagImg=" + Arrays.toString(countryFlagImg) +
                ", countryBorderCapitolImg=" + Arrays.toString(countryBorderCapitolImg) +
                '}';
    }
}
