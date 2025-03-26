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
import com.example.sample_project.model.Student;
import com.example.sample_project.repository.StudentRepository;

import io.micrometer.common.lang.NonNull;
import lombok.SneakyThrows;

@SpringBootTest(classes = SampleProjectApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    StudentRepository studentRepository;

    @Test
    @SneakyThrows
    void testListAllStaff_success() {

        // Given
        List<Student> testStudentList = List.of (
            anyStudent(1, "student1"),
            anyStudent(2, "student2")
        );

        String studentListPayload = """
                [
                    {
                        "id": 1,
                        "name": "student1",
                        "email": "student@example.com",
                        "phone_number": "041122223333",
                        "degree_id": 5
                    },
                    {
                        "id": 2,
                        "name": "student2",
                        "email": "student@example.com",
                        "phone_number": "041122223333",
                        "degree_id": 5
                    }
                ]
                 """; 

        // When
        when(studentRepository.listAllStudents())
               .thenReturn(testStudentList);

        // Then
        this.mockMvc.perform(get("/student/list-all"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(studentListPayload));
    }

    @Test
    @SneakyThrows
    void testGetStaffById_success() {

        Integer id = 1;
        Optional<Student> studentOpt1 = Optional.of(anyStudent(1, "student1"));

        String studentPayload = """
                {
                    "id": 1,
                    "name": "student1",
                    "email": "student@example.com",
                    "phone_number": "041122223333",
                    "degree_id": 5
                }
                 """; 

        // When
        when(studentRepository.getStudentById(id))
               .thenReturn(studentOpt1);

        // Then
        this.mockMvc.perform(get("/student/get/{id}",id))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(studentPayload));
    }

    public static Student anyStudent(@NonNull Integer id, @NonNull String name) {
        return Student.builder()
            .id(id)
            .name(name)
            .email("student@example.com")
            .phone_number("041122223333")
            .degree_id(5)
        .build();
    }
}