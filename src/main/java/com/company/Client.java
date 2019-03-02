package com.company;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    public class FullName {
        public String title;
        public String first;
        public String last;
    }

    public FullName name;

    public String getSurname() {
        return name.last;
    }

    public String getName() {
        return name.first;
    }

    public String getPatronomic() {
        return name.title;
    }

    public String patronymic;
    public String gender;

    public String getGender() {
        return gender;
    }

    public LocalDate birthDay;

    public class DateAndAge {
        public Date date;
        public int age;
    }

    public int getAge() {
        return dob.age;
    }

    public DateAndAge dob;

    public String gatBD() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        return dateFormat.format(dob.date);
        //return birthDay.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public int age() {
        return Period.between(birthDay, LocalDate.now()).getYears();
    }

    public long inn;
    public String nat;

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

    public String getRegion() {
        return location.state;
    }

    public String getCity() {
        return location.city;
    }

    public String getStreet() {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher streetMatch = pattern.matcher(location.street);
        streetMatch.find();
        return location.street.substring(streetMatch.start(),streetMatch.end());
    }

    public int getHouse() {
        String []streetWords = location.street.split(" ");
        try {
            return Integer.decode(streetWords[0]);

        }
        catch (NumberFormatException ex){
            return Integer.decode(streetWords[streetWords.length - 1]);

        }
    }

    public String getIndex() {
        return location.postcode;
    }

    public Location location;
    public int home;
    public int flat;
}
