package com.example.sample_project.controller;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
class ErrorsDTO implements ResponseObject {
    private final List<ErrorDTO> errors = new ArrayList<>();

    public ErrorsDTO(final List<ErrorDTO> errors) {
        if (errors != null) {
            this.errors.addAll(errors);
        }
    }
}