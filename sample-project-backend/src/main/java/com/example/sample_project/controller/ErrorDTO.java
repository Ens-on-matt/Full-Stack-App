package com.example.sample_project.controller;

import lombok.Data;

@Data
class ErrorDTO {
    private final String code;
    private final String message;
}
