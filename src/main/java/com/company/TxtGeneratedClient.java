package com.company;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class TxtGeneratedClient implements Client {
    String name;
    String surname;
    String patronymic;
    String sex;
    LocalDate birthDay;
    long inn;
    int index;
    String country;
    String region;
    String city;
    String street;
    int home;
    int flat;

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPatronomic() {
        return patronymic;
    }

    @Override
    public String getGender() {
        return sex;
    }

    @Override
    public int getAge() {
        return Period.between(birthDay, LocalDate.now()).getYears();
    }

    @Override
    public String getBD() {
        return birthDay.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Override
    public String getBDForDB() {return birthDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); }

    @Override
    public String getInn() {
        return Long.toString(inn);
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public String getStreet() {
        return street;
    }

    @Override
    public int getHouse() {
        return home;
    }

    @Override
    public String getIndex() {
        return Integer.toString(index);
    }

    @Override
    public int getFlat() {
        return flat;
    }
}
