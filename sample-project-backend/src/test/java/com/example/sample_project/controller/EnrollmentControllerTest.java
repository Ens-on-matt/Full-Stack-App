package com.example.sample_project.controller;

import com.example.sample_project.SampleProjectApplication;
import com.example.sample_project.matchers.EnrollmentMatcher;
import com.example.sample_project.model.Enrollment;
import com.example.sample_project.repository.EnrollmentRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SampleProjectApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EnrollmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    EnrollmentRepository enrollmentRepository;

    @Test
    @SneakyThrows
    void testListAllEnrollment() {

        // Given
        List<Enrollment> testEnrollmentList = createExampleEnrollmentList();

        String enrollmentListPayload = """
                [
                    {
                        "student": 3,
                        "course": 1,
                        "status": "Completed"
                    },
                    {
                        "student": 7,
                        "course": 2,
                        "status": "In progress"
                    },
                    {
                        "student": 8,
                        "course": 3,
                        "status": "Completed"
                    },
                    {
                        "student": 53,
                        "course": 4,
                        "status": "Completed"
                    },
                    {
                        "student": 62,
                        "course": 5,
                        "status": "In progress"
                    },
                    {
                        "student": 1,
                        "course": 6,
                        "status": "Completed"
                    },
                    {
                        "student": 77,
                        "course": 7,
                        "status": "Completed"
                    },
                    {
                        "student": 3,
                        "course": 8,
                        "status": "In progress"
                    },
                    {
                        "student": 15,
                        "course": 9,
                        "status": "Completed"
                    },
                    {
                        "student": 24,
                        "course": 10,
                        "status": "Completed"
                    }
                ]
                 """;

        // When
        when(enrollmentRepository.listAllEnrollments())
                .thenReturn(testEnrollmentList);

        // Then
        this.mockMvc.perform(get("/enrollment/list-all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(enrollmentListPayload));
    }

    @Test
    @SneakyThrows
    void testGetEnrollmentById_Valid() {

        Integer student_id = 3, course_id = 1;
        Optional<Enrollment> enrollmentOpt1 = Optional.of(createExampleEnrollmentList().get(0));

        String enrollmentPayload = """
                {
                    "student": 3,
                    "course": 1,
                    "status": "Completed"
                }
                 """;

        // When
        when(enrollmentRepository.getEnrollmentById(student_id, course_id))
                .thenReturn(enrollmentOpt1);

        // Then
        this.mockMvc.perform(get("/enrollment/get/{student}-{course}", student_id, course_id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(enrollmentPayload));
    }

    @Test
    @SneakyThrows
    void testListAllEnrollmentsForStudent() {
        Integer student_id = 10;
        List<Enrollment> enrollmentsOfStudent = List.of(new Enrollment[]{
                Enrollment.builder().student(10).course(3).status("In progress").build(),
                Enrollment.builder().student(10).course(4).status("In progress").build(),
                Enrollment.builder().student(10).course(12).status("Completed").build(),
                Enrollment.builder().student(10).course(18).status("Completed").build()
        });
        String enrollmentPayload = """
                [
                    {
                        "student": 10,
                        "course": 3,
                        "status": "In progress"
                    },
                    {
                        "student": 10,
                        "course": 4,
                        "status": "In progress"
                    },
                    {
                        "student": 10,
                        "course": 12,
                        "status": "Completed"
                    },
                    {
                        "student": 10,
                        "course": 18,
                        "status": "Completed"
                    }
                ]
                 """;

        // When
        when(enrollmentRepository.listAllEnrollmentsForStudent(student_id))
                .thenReturn(enrollmentsOfStudent);

        // Then
        this.mockMvc.perform(get("/enrollment/get-from-student/{id}", student_id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(enrollmentPayload));
    }
    
    @Test
    @SneakyThrows
    void listAllEnrollmentsInCourse() {
        Integer course_id = 8;
        List<Enrollment> enrollmentsOfStudent = List.of(new Enrollment[]{
                Enrollment.builder().student(51).course(8).status("In progress").build(),
                Enrollment.builder().student(24).course(8).status("In progress").build(),
                Enrollment.builder().student(88).course(8).status("Completed").build(),
                Enrollment.builder().student(154).course(8).status("Completed").build()
        });
        String enrollmentPayload = """
                [
                    {
                        "student": 51,
                        "course": 8,
                        "status": "In progress"
                    },
                    {
                        "student": 24,
                        "course": 8,
                        "status": "In progress"
                    },
                    {
                        "student": 88,
                        "course": 8,
                        "status": "Completed"
                    },
                    {
                        "student": 154,
                        "course": 8,
                        "status": "Completed"
                    }
                ]
                 """;

        // When
        when(enrollmentRepository.listAllEnrollmentsInCourse(course_id))
                .thenReturn(enrollmentsOfStudent);

        // Then
        this.mockMvc.perform(get("/enrollment/get-from-course/{id}", course_id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(enrollmentPayload));
    }

    @Test
    @SneakyThrows
    void testSaveEnrollment_ValidUpdate() {
        // Given
        Integer student_id = 20, course_id = 5;

        Enrollment newEnrollment = Enrollment.builder()
                .student(student_id)
                .course(course_id)
                .status("Completed")
                .build();

        String newEnrollmentPayload = """
                {
                    "student": 20,
                    "course": 5,
                    "status": "Completed"
                }
                """;

        EnrollmentMatcher enrollmentPropertyMatcher = EnrollmentMatcher.builder().left(newEnrollment).build();

        // When
        when(enrollmentRepository.getEnrollmentById(student_id, course_id))
                .thenReturn(Optional.of(newEnrollment));

        when(enrollmentRepository.updateEnrollmentStatus(argThat(enrollmentPropertyMatcher)))
                .thenReturn(Optional.of(newEnrollment));

        when(enrollmentRepository.saveNewEnrollment(argThat(enrollmentPropertyMatcher)))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/enrollment/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newEnrollmentPayload))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(newEnrollmentPayload));
    }

    @Test
    @SneakyThrows
    void testSaveEnrollment_InvalidUpdateNonExistent() {
        // Given
        Integer student_id = 20, course_id = 5;

        Enrollment newEnrollment = Enrollment.builder()
                .student(student_id)
                .course(course_id)
                .status("Completed")
                .build();

        String newEnrollmentPayload = """
                {
                    "student": 55,
                    "course": 5,
                    "status": "Completed"
                }
                """;

        EnrollmentMatcher enrollmentPropertyMatcher = EnrollmentMatcher.builder().left(newEnrollment).build();

        // When
        when(enrollmentRepository.getEnrollmentById(student_id, course_id))
                .thenReturn(Optional.empty());

        when(enrollmentRepository.updateEnrollmentStatus(argThat(enrollmentPropertyMatcher)))
                .thenReturn(Optional.empty());

        when(enrollmentRepository.saveNewEnrollment(any()))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/enrollment/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newEnrollmentPayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testSaveEnrollment_InvalidProperties() {
        // Given
        Integer student_id = 20, course_id = 5;

        Enrollment newEnrollment = Enrollment.builder()
                .student(student_id)
                .course(course_id)
                .status("Completed")
                .build();

        String newEnrollmentPayload = """
                {
                    "student": "Twenty",
                    "course": "Five",
                    "status": "Completed"
                }
                """;
        EnrollmentMatcher enrollmentPropertyMatcher = EnrollmentMatcher.builder().left(newEnrollment).build();

        // When
        when(enrollmentRepository.getEnrollmentById(student_id, course_id))
                .thenReturn(Optional.empty());

        when(enrollmentRepository.updateEnrollmentStatus(argThat(enrollmentPropertyMatcher)))
                .thenReturn(Optional.empty());

        when(enrollmentRepository.saveNewEnrollment(argThat(enrollmentPropertyMatcher)))
                .thenReturn(Optional.of(newEnrollment));

        // Then
        this.mockMvc.perform(put("/enrollment/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newEnrollmentPayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testDeleteEnrollment_Valid() {
        // Given
        Integer student_id = 20, course_id = 5;

        Enrollment exampleEnrollment = Enrollment.builder()
                .student(20)
                .course(5)
                .status("Completed")
                .build();

        // When
        when(enrollmentRepository.getEnrollmentById(student_id, course_id))
                .thenReturn(Optional.of(exampleEnrollment));
        when(enrollmentRepository.deleteEnrollment(student_id, course_id))
                .thenReturn(true);

        // Then
        this.mockMvc.perform(delete("/enrollment/delete/{student}-{course}", student_id, course_id))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @SneakyThrows
    void testDeleteEnrollment_InvalidNonExistent() {
        // Given
        Integer student_id = 20, course_id = 5;

        // When
        when(enrollmentRepository.getEnrollmentById(student_id, course_id))
                .thenReturn(Optional.empty());
        when(enrollmentRepository.deleteEnrollment(student_id, course_id))
                .thenReturn(false);

        // Then
        this.mockMvc.perform(delete("/enrollment/delete/{student}-{course}", student_id, course_id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    
    /// HELPER FUNCTIONS

    private List<Enrollment> createExampleEnrollmentList() {
        Integer[] exampleStudentIds = {3,7,8,53,62,1,77,3,15,24};
        String[] exampleStatus = {"Completed", "In progress", "Completed", "Completed", "In progress", "Completed",
                "Completed", "In progress", "Completed", "Completed"};
        List<Enrollment> enrollmentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            enrollmentList.add(Enrollment.builder()
                    .student(exampleStudentIds[i])
                    .course(i + 1)
                    .status(exampleStatus[i])
                    .build());
        }
        return enrollmentList;
    }

}
