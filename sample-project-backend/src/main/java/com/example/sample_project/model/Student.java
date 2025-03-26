package com.example.sample_project.model;

import com.example.sample_project.controller.ResponseObject;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

@Getter
@Builder
@ToString
@With
public class Student implements ResponseObject {
    private final Integer id;
    private final String  name;
    private final String  email;
    private final String  phone_number;
    private final Integer degree_id;
}
