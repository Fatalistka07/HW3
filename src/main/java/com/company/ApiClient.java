package com.company;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiClient implements Client {
    public class FullName {
        public String title;
        public String first;
        public String last;
    }

    public FullName name;

    @Override
    public String getSurname() {
        return name.last;
    }

    @Override
    public String getName() {
        return name.first;
    }

    @Override
    public String getPatronomic() {
        return name.title;
    }

    public String gender;

    @Override
    public String getGender() {
        return gender;
    }

    public class DateAndAge {
        public Date date;
        public int age;
    }

    public DateAndAge dob;

    @Override
    public String getBD() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        return dateFormat.format(dob.date);
    }

    @Override
    public int getAge() {
        return dob.age;
    }

    public long inn;
    @Override
    public long getInn(){
        return inn;
    }
    public String nat;

    @Override
    public String getCountry() {
        return nat;
    }

    public class Location {
        public String state;
        public String city;
        public String street;
        public String postcode;

        public class Coordinates {
            public double latitude;
            public double longitude;
        }

        public Coordinates coordinates;

        public class Timezone {
            public String offset;
            public String description;
        }

        public Timezone timezone;
    }
    public Location location;

    @Override
    public String getRegion() {
        return location.state;
    }

    @Override
    public String getCity() {
        return location.city;
    }

    @Override
    public String getStreet() {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher streetMatch = pattern.matcher(location.street);
        streetMatch.find();
        return location.street.substring(streetMatch.start(),streetMatch.end());
    }

    @Override
    public int getHouse() {
        String []streetWords = location.street.split(" ");
        try {
            return Integer.decode(streetWords[0]);

        }
        catch (NumberFormatException ex){
            return Integer.decode(streetWords[streetWords.length - 1]);

        }
    }

    @Override
    public String getIndex() {
        return location.postcode;
    }

    public int flat;
    @Override
    public int getFlat() {
        return flat;
    }
}
