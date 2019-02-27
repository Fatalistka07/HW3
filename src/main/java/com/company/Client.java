package com.company;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Client {
    public String name;
    public String surname;
    public String patronymic;
    public String sex;
    public LocalDate birthDay;
    public String birthDayStr() {return birthDay.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));}
    public int age() { return Period.between(birthDay, LocalDate.now()).getYears();}
    public long inn;
    public int index;
    public String country;
    public String region;
    public String city;
    public String street;
    public int home;
    public int flat;
}
