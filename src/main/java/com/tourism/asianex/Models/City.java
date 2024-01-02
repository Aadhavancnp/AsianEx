package com.tourism.asianex.Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class City {
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty country = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty image = new SimpleStringProperty("");
    private final IntegerProperty price = new SimpleIntegerProperty();
    private final IntegerProperty noOfDays = new SimpleIntegerProperty();

    public City(String name, String country, String description, String image, int price, int noOfDays) {
        this.name.set(name);
        this.country.set(country);
        this.description.set(description);
        this.image.set(image);
        this.price.set(price);
        this.noOfDays.set(noOfDays);
    }

    public static List<City> getCities() {
        City kyoto = new City("Kyoto", "Japan", "Kyoto is a city in Japan that was the imperial capital for over a thousand years. It has a population of around 1.5 million and is now the capital city of Kyoto Prefecture located in the Kansai region. Kyoto is also known as the thousand-year capital.", "kyoto.jpg", 1000, 3);
        City chiangMai = new City("Chiang Mai", "Thailand", "Chiang Mai is a city in northern Thailand that is known for its stunning temples, vibrant night markets, and scenic mountain views.", "chiang-mai.jpg", 1200, 4);
        City hoiAn = new City("Hoi An", "Vietnam", "Hoi An is a city in Vietnam that is a UNESCO World Heritage Site. It has a well-preserved ancient town, beautiful beaches, and delicious local cuisine.", "hoi-an.jpg", 1500, 5);
        City siemReap = new City("Siem Reap", "Cambodia", "Siem Reap is a city in Cambodia that is home to the famous Angkor Wat temple complex. It is a must-visit destination for history buffs and architecture enthusiasts.", "siem-reap.jpg", 1800, 6);
        City tokyo = new City("Tokyo", "Japan", "Tokyo is a bustling metropolis that seamlessly blends the traditional and modern. From its towering skyscrapers to its serene gardens and temples, there's something for everyone in this vibrant city.", "tokyo.jpg", 2100, 7);
        City bali = new City("Bali", "Indonesia", "Bali is an Indonesian island known for its stunning beaches, lush rice paddies, and vibrant culture. It is a popular destination for travelers seeking relaxation and adventure.", "bali.jpg", 2400, 8);
        City luangPrabang = new City("Luang Prabang", "Laos", "Luang Prabang is a charming city nestled in the mountains of northern Laos. It is known for its beautiful temples, stunning natural scenery, and laid-back atmosphere.", "luang-prabang.jpg", 2700, 9);
        City singapore = new City("Singapore", "Singapore", "Singapore is a modern city-state with a rich cultural heritage. It is home to some of the world's best shopping, dining, and entertainment options.", "singapore.jpg", 3000, 10);
        City hongKong = new City("Hong Kong", "China", "Hong Kong is a bustling metropolis with a unique blend of Chinese and Western cultures. It is known for its stunning skyline, delicious cuisine, and vibrant nightlife.", "hong-kong.jpg", 3300, 11);
        City seoul = new City("Seoul", "South Korea", "Seoul is a city of contrasts that is home to ancient palaces and temples, as well as modern skyscrapers and shopping malls. The city is also renowned for its delicious food and vibrant nightlife.", "seoul.jpg", 3600, 12);
        return Arrays.asList(kyoto, chiangMai, hoiAn, siemReap, tokyo, bali, luangPrabang, singapore, hongKong, seoul);
    }

    public static City fromDocument(Document document) {
        return new City(document.getString("name"), document.getString("country"), document.getString("description"), document.getString("image"), document.getInteger("price"), document.getInteger("noOfDays"));
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getCountry() {
        return country.get();
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getImage() {
        return image.get();
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public int getPrice() {
        return price.get();
    }

    public void setPrice(int price) {
        this.price.set(price);
    }

    public int getNoOfDays() {
        return noOfDays.get();
    }

    public void setNoOfDays(int noOfDays) {
        this.noOfDays.set(noOfDays);
    }

    public Document toDocument() {
        return new Document("name", name.get()).append("country", country.get()).append("description", description.get()).append("image", image.get()).append("price", price.get()).append("noOfDays", noOfDays.get());
    }


}
