package com.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DBClient implements Client{
    private String name;
    private String surname;
    private String middlename;
    private LocalDate birthday;
    private String gender;
    private String inn;
    private String postcode;
    private String country;
    private String region;
    private String city;
    private String street;
    private int house;
    private int flat;

    DBClient(ResultSet rs) throws SQLException {
        name = rs.getString(1);
        surname = rs.getString(2);
        middlename = rs.getString(3);
        birthday = rs.getDate(4).toLocalDate();
        gender = rs.getString(5);
        inn = rs.getString(6);
        postcode = rs.getString(7);
        country = rs.getString(8);
        region = rs.getString(9);
        city = rs.getString(10);
        street = rs.getString(11);
        house = rs.getInt(12);
        flat = rs.getInt(13);
    }
    
    @Override
    public String getSurname() { return surname; }

    @Override
    public String getName() { return name; }

    @Override
    public String getPatronomic() { return middlename;}

    @Override
    public String getGender() { return gender; }

    @Override
    public int getAge() { return Period.between(birthday, LocalDate.now()).getYears(); }

    @Override
    public String getBD() {
        return birthday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Override
    public String getBDForDB() {
        return birthday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public String getInn() { return inn; }

    @Override
    public String getCountry() { return country; }

    @Override
    public String getRegion() { return region; }

    @Override
    public String getCity() { return city; }

    @Override
    public String getStreet() { return street; }

    @Override
    public int getHouse() { return house; }

    @Override
    public String getIndex() { return postcode; }

    @Override
    public int getFlat() { return flat; }
}
