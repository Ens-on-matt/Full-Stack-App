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

import com.example.sample_project.model.Course;
import com.example.sample_project.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.sample_project.controller.Response.notFound404;

//@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/course", produces = MediaType.APPLICATION_JSON_VALUE)
public class CourseController {

    private final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    private final CourseRepository courseRepository;
    
    @GetMapping(value = "/list-all")
    public ResponseEntity<ResponseObject> listAllCourses() 
    {
       LOGGER.info("Controller listAllDegrees called");

        List<Course> courseList = courseRepository.listAllCourses();
        CourseListDTO response = new CourseListDTO();
        response.addAll(courseList);

        if (courseList.size() > 0) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return notFound404("No courses found.");
        }
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<ResponseObject> getCourseById (
        @PathVariable Integer id
    ) {
       LOGGER.info("Controller getCourseById called");

        Optional<Course> courseOpt = courseRepository.getCourseById(id);

        if (courseOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(courseOpt.get());
        } else {
            return notFound404("No course found.");
        }
    }

    @GetMapping(value = "/get/page/{pageNo}/{pageSize}")
    public ResponseEntity<ResponseObject> listPageOfCourses (@PathVariable Integer pageNo,
                                                           @PathVariable Integer pageSize) {
        LOGGER.info("Controller listCoursePage called");

        List<Course> CourseList = courseRepository.getPageOfCourses(pageNo, pageSize, 0);
        int totalPages = courseRepository.getSizeOfCourseTable();
        CoursePageDTO pageResponse = new CoursePageDTO(CourseList, totalPages);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pageResponse);
    }

    @GetMapping(value = "/courses-for-degree/{id}")
    public ResponseEntity<ResponseObject> getCoursesRequiredForDegree (
            @PathVariable Integer id
    ) {
        LOGGER.info("Controller getCoursesRequiredForDegree called");

        List<Course> courses = courseRepository.getCoursesRequiredForDegree(id);
        CourseListDTO response = new CourseListDTO();
        response.addAll(courses);

        if (!courses.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return notFound404("No courses found for degree");
        }
    }
    @GetMapping(value = "/search/{searchTerm}/{pageSize}")
    public ResponseEntity<ResponseObject> searchCourses (@PathVariable String searchTerm,
                                                              @PathVariable Integer pageSize) {
        LOGGER.info("Controller searchCourses called");
        List<Course> courseList = courseRepository.searchCourses(searchTerm, pageSize);
        CoursePageDTO response = new CoursePageDTO(courseList, 1);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> saveCourse (@RequestBody Course course) {
        if (course.getId() == -1) {
            return saveNewCourse(course);
        } else {
            return updateCourse(course);
        }
    }

    public ResponseEntity<ResponseObject> saveNewCourse (Course course) {
        LOGGER.info("Controller saveNewCourse called");

        Optional<Integer> courseOpt = courseRepository.saveNewCourse(course);

        if (courseOpt.isPresent()) {
            Course updated_course = course.withId(courseOpt.get());
            LOGGER.info("New course member has id {}", courseOpt.get());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(updated_course);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    public ResponseEntity<ResponseObject> updateCourse (Course course) {
        LOGGER.info("Controller updateCourse called");

        Optional<Course> courseOpt = courseRepository.updateCourse(course);

        if (courseOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(courseOpt.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @DeleteMapping(value = "delete/{id}")
    public ResponseEntity<ResponseObject> deleteCourse (@PathVariable Integer id) {
        LOGGER.info("Controller deleteCourse called");

        Optional<Course> courseOpt = courseRepository.getCourseById(id);

        if (courseOpt.isPresent()) {
            Course course_in_db = courseOpt.get();
            LOGGER.info("Deleting {}", course_in_db);
            if (courseRepository.deleteCourse(id)) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(null);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        return notFound404("Course member provided not found.");
    }

    @AllArgsConstructor
    @Getter
    static class CoursePageDTO implements ResponseObject {
        List<Course> list;
        int totalElements;
    }

    @AllArgsConstructor
    @Getter
    static class CourseListDTO extends ArrayList<Course> implements ResponseObject {
    }

}