package com.example.sample_project.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sample_project.model.Student;
import com.example.sample_project.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.sample_project.controller.Response.notFound404;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/student", produces = MediaType.APPLICATION_JSON_VALUE)
public class StudentController {

    private final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

    private final StudentRepository studentRepository;
    
    @GetMapping(value = "/list-all")
    public ResponseEntity<ResponseObject> listAllStudents() 
    {
       LOGGER.info("Controller listAllStudents called");

        List<Student> studentList = studentRepository.listAllStudents();
        StudentListDTO response = new StudentListDTO();
        response.addAll(studentList);

        if (studentList.size() > 0) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return notFound404("No staff found.");
        }
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<ResponseObject> getStudentById (
        @PathVariable Integer id
    ) {
       LOGGER.info("Controller listAllStudents called");

        Optional<Student> studentOpt = studentRepository.getStudentById(id);

        if (studentOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(studentOpt.get());
        } else {
            return notFound404("No students found.");
        }
    }

    @GetMapping(value = "/get/page/{pageNo}/{pageSize}")
    public ResponseEntity<ResponseObject> listPageOfStaff (@PathVariable Integer pageNo,
                                                           @PathVariable Integer pageSize) {
        LOGGER.info("Controller listStudentPage called");

        List<Student> StudentList = studentRepository.getPageOfStudents(pageNo, pageSize, 0);
        int totalPages = studentRepository.getSizeOfStudentTable();
        StudentPageDTO pageResponse = new StudentPageDTO(StudentList, totalPages);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pageResponse);
    }

    @GetMapping(value = "/search/{searchTerm}/{pageSize}")
    public ResponseEntity<ResponseObject> searchStudentMembers (@PathVariable String searchTerm,
                                                               @PathVariable Integer pageSize) {
        LOGGER.info("Controller searchStudentMembers called");
        List<Student> studentList = studentRepository.searchStudents(searchTerm, pageSize);
        StudentPageDTO response = new StudentPageDTO(studentList, 1);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> saveStudentMember (@RequestBody Student student) {
        if (student.getId() == -1) {
            return saveNewStudentMember(student);
        } else {
            return updateStudentMember(student);
        }
    }

    public ResponseEntity<ResponseObject> saveNewStudentMember (Student student) {
        LOGGER.info("Controller saveNewStudentMember called");

        Optional<Integer> studentOpt = studentRepository.saveNewStudent(student);

        if (studentOpt.isPresent()) {
            Student updated_student = student.withId(studentOpt.get());
            LOGGER.info("New student member has id {}", studentOpt.get());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(updated_student);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    public ResponseEntity<ResponseObject> updateStudentMember (Student student) {
        LOGGER.info("Controller updateStudentMember called");

        Optional<Student> studentOpt = studentRepository.updateStudent(student);

        if (studentOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(studentOpt.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }


    @DeleteMapping(value = "delete/{id}")
    public ResponseEntity<ResponseObject> deleteStudent (@PathVariable Integer id) {
        LOGGER.info("Controller deleteStudent called");

        Optional<Student> studentOpt = studentRepository.getStudentById(id);

        if (studentOpt.isPresent()) {
            Student student_in_db = studentOpt.get();
            LOGGER.info("Deleting {}", student_in_db);
            if (studentRepository.deleteStudent(id)) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(null);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        return notFound404("Student member provided not found.");
    }


    @AllArgsConstructor
    @Getter
    static class StudentPageDTO implements ResponseObject {
        List<Student> list;
        int totalElements;
    }

    @AllArgsConstructor
    @Getter
    static class StudentListDTO extends ArrayList<Student> implements ResponseObject {
    }

}