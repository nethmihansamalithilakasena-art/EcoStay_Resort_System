/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecostay.model;

public class Guest {
    private String fullName;
    private String nationalityType;
    private String identityNumber;
    private String phoneNumber;
    private String emailAddress;

    public Guest(String fullName, String nationalityType, String identityNumber, String phoneNumber, String emailAddress) {
        this.fullName = fullName;
        this.nationalityType = nationalityType;
        this.identityNumber = identityNumber;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getNationalityType() { return nationalityType; }
    public String getIdentityNumber() { return identityNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmailAddress() { return emailAddress; }
}

