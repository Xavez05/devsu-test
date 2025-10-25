package com.bank.customers.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Person {

    @NotBlank
    private String name;

    @NotBlank
    private String gender;

    @Min(0)
    private Integer age;

    @NotBlank
    private String identification;

    @NotBlank
    private String address;

    @NotBlank
    private String phoneNumber;
}
