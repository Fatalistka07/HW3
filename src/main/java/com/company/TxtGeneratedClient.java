package com.company;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class TxtGeneratedClient implements Client {
    public String name;
    public String surname;
    public String patronymic;
    public String sex;
    public LocalDate birthDay;
    public long inn;
    public int index;
    public String country;
    public String region;
    public String city;
    public String street;
    public int home;
    public int flat;

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
    public long getInn() {
        return inn;
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
