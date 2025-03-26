package com.example.sample_project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

public class Response {

    public static ResponseEntity<ResponseObject> badRequest400(final List<ErrorDTO> errorDTOs) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody(errorDTOs));
    }

    public static ResponseEntity<ResponseObject> notFound404(final String message) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody("404", message));
    }

    public static ResponseEntity<ResponseObject> internalServer500(final String message) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("500", message));
    }

    public static ErrorDTO createError(final String code, final String message) {
        return new ErrorDTO(
                code,
                message
        );
    }

    private static ErrorsDTO errorBody(final String code, final String message) {
        return errorBody(
                Collections.singletonList(
                        new ErrorDTO(
                                code,
                                message
                        )
                )
        );
    }

    private static ErrorsDTO errorBody(final List<ErrorDTO> errorDTOs) {
        return new ErrorsDTO(errorDTOs);
    }

}