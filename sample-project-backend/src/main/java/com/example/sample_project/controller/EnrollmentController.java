package com.example.sample_project.controller;

import com.example.sample_project.model.Enrollment;
import com.example.sample_project.repository.EnrollmentRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.sample_project.controller.Response.internalServer500;
import static com.example.sample_project.controller.Response.notFound404;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/enrollment", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnrollmentController {

    private final Logger LOGGER = LoggerFactory.getLogger(EnrollmentController.class);

    private final EnrollmentRepository enrollmentRepository;

    @GetMapping(value = "/list-all")
    public ResponseEntity<ResponseObject> listAllEnrollments()
    {
       LOGGER.info("Controller listAllEnrollments called");

        List<Enrollment> enrollmentList = enrollmentRepository.listAllEnrollments();
        EnrollmentListDTO response = new EnrollmentListDTO();
        response.addAll(enrollmentList);

        if (enrollmentList.size() > 0) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return notFound404("No enrollments found.");
        }
    }

    @GetMapping(value = "/get/{student}-{course}")
    public ResponseEntity<ResponseObject> getEnrollmentById (
        @PathVariable Integer student, @PathVariable Integer course
    ) {
       LOGGER.info("Controller getEnrollmentById called");

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.getEnrollmentById(student, course);

        if (enrollmentOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(enrollmentOpt.get());
        } else {
            return notFound404("No enrollment found.");
        }
    }

    @GetMapping(value = "/get-from-student/{id}")
    public ResponseEntity<ResponseObject> listAllEnrollmentsForStudent(@PathVariable Integer id)
    {
        LOGGER.info("Controller listAllEnrollmentsForStudent called");

        List<Enrollment> enrollmentList = enrollmentRepository.listAllEnrollmentsForStudent(id);
        EnrollmentListDTO response = new EnrollmentListDTO();
        response.addAll(enrollmentList);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping(value = "/get-from-course/{id}")
    public ResponseEntity<ResponseObject> listAllEnrollmentsInCourse(@PathVariable Integer id)
    {
        LOGGER.info("Controller listAllEnrollmentsInCourse called");

        List<Enrollment> enrollmentList = enrollmentRepository.listAllEnrollmentsInCourse(id);
        EnrollmentListDTO response = new EnrollmentListDTO();
        response.addAll(enrollmentList);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> saveEnrollment (@RequestBody Enrollment enrollment) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.getEnrollmentById(enrollment.getStudent(), enrollment.getCourse());

        if (enrollmentOpt.isEmpty()) {
            return saveNewEnrollment(enrollment);
        } else {
            return updateEnrollment(enrollment);
        }
    }

    public ResponseEntity<ResponseObject> saveNewEnrollment (Enrollment enrollment) {
        LOGGER.info("Controller saveNewEnrollment called");

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.saveNewEnrollment(enrollment);

        if (enrollmentOpt.isPresent()) {
            Enrollment updated_enrollment = enrollmentOpt.get();
            LOGGER.info("New enrollment member has id {}-{}", updated_enrollment.getStudent(), updated_enrollment.getCourse());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(updated_enrollment);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    public ResponseEntity<ResponseObject> updateEnrollment (Enrollment enrollment) {
        LOGGER.info("Controller updateEnrollment called");

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.updateEnrollmentStatus(enrollment);

        if (enrollmentOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(enrollmentOpt.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @DeleteMapping(value = "delete/{student}-{course}")
    public ResponseEntity<ResponseObject> deleteEnrollment (@PathVariable Integer student, @PathVariable Integer course) {
        LOGGER.info("Controller deleteEnrollment called");

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.getEnrollmentById(student, course);

        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment_in_db = enrollmentOpt.get();
            LOGGER.info("Deleting {}", enrollment_in_db);
            if (enrollmentRepository.deleteEnrollment(student, course)) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(null);
            } else {
                return internalServer500(String.format("Error deleting enrollment with id %d-%d", student, course));
            }
        }
        return notFound404("Enrollment member provided not found.");
    }


    @AllArgsConstructor
    @Getter
    static class EnrollmentPageDTO implements ResponseObject {
        List<Enrollment> list;
        int totalElements;
    }

    @AllArgsConstructor
    @Getter
    static class EnrollmentListDTO extends ArrayList<Enrollment> implements ResponseObject {
    }

}