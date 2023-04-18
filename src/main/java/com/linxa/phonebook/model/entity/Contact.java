package com.linxa.phonebook.model.entity;

import java.util.Objects;

public class Contact {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String country;
    private String city;
    private String street;

    public Contact() {
    }

    public Contact(Long id, String firstName, String lastName, String email,
                   String phoneNumber, String country, String city, String street) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.street = street;
    }

    public Contact(Contact other) {
        this.id = other.getId();
        this.firstName = other.getFirstName();
        this.lastName = other.getLastName();
        this.email = other.getEmail();
        this.phoneNumber = other.getPhoneNumber();
        this.country = other.getCountry();
        this.city = other.getCity();
        this.street = other.getStreet();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
