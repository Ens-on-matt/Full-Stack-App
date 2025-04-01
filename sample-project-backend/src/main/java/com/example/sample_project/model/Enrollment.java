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
public class Enrollment implements ResponseObject {
    private final Integer student;
    private final Integer course;
    private final String status;
}
