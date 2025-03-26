package com.example.sample_project.response_objects;

import com.example.sample_project.controller.ResponseObject;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class BooleanDTO implements ResponseObject {
    private final boolean value;
}
