package com.askisi5.app.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Request payload for creating or updating a User. Deliberately has no
 * "id" field: the id is server-generated on create and taken from the URL
 * path on update, so it is never accepted from the request body. Unknown
 * properties (e.g. a client that still sends "id") are ignored rather than
 * rejected, so older/looser clients keep working.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(max = 100, message = "Street address must not exceed 100 characters")
    private String street;

    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @Min(value = 1000, message = "Postal code must be at least 1000")
    @Max(value = 99999, message = "Postal code must not exceed 99999")
    private int postalCode;

    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Past(message = "Birthday must be in the past")
    private Date birthdayDate;

    @Pattern(regexp = "^[MF]$", message = "Sex must be M or F")
    private String sex;

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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(Date birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
