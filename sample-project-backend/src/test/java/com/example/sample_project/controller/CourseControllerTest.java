package com.example.sample_project.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.sample_project.SampleProjectApplication;
import com.example.sample_project.model.Course;
import com.example.sample_project.repository.CourseRepository;

import io.micrometer.common.lang.NonNull;
import lombok.SneakyThrows;

@SpringBootTest(classes = SampleProjectApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CourseRepository courseRepository;

    //@Test
    @SneakyThrows
    void testListAllCourse_success() {

        // Given
        List<Course> testCourseList = List.of (
            anyCourse(1, "course1"),
            anyCourse(2, "course2")
        );

        String courseListPayload = """
                [
                    {
                        "id": 1,
                        "professor_id": 153,
                        "name": "course1",
                        "degree_id": 7
                    },
                    {
                        "id": 2,
                        "professor_id": 153,
                        "name": "course2",
                        "degree_id": 7
                    }
                ]
                 """; 

        // When
        when(courseRepository.listAllCourses())
               .thenReturn(testCourseList);

        // Then
        this.mockMvc.perform(get("/course/list-all"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(courseListPayload));
    }

    @Test
    @SneakyThrows
    void testGetCourseById_success() {

        Integer id = 1;
        Optional<Course> courseOpt1 = Optional.of(anyCourse(1, "course1"));

        String coursePayload = """
                {
                    "id": 1,
                    "professor_id": 153,
                    "name": "course1",
                    "degree_id": 7
                }
                 """; 

        // When
        when(courseRepository.getCourseById(id))
               .thenReturn(courseOpt1);

        // Then
        this.mockMvc.perform(get("/course/get/{id}",id))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(coursePayload));
    }

    public static Course anyCourse(@NonNull Integer id, @NonNull String name) {
        return Course.builder()
            .id(id)
            .name(name)
            .degree_id(7)
            .professor_id(153)
        .build();
    }
}