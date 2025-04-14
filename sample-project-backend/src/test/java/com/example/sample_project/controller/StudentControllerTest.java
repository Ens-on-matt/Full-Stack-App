package com.example.sample_project.controller;

import com.example.sample_project.model.Staff;
import com.example.sample_project.model.Student;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.sample_project.SampleProjectApplication;
import com.example.sample_project.repository.StudentRepository;

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
    void testListAllStudent() {

        // Given
        List<Student> testStudentList = createExampleStudentList();

        String studentListPayload = """
                [
                    {
                        "id": 1,
                        "name": "Lily Stuart",
                        "email": "lily.s@xyz.com",
                        "phone_number": "04401697852",
                        "degree_id": 5
                    },
                    {
                        "id": 2,
                        "name": "Dylan Phelps",
                        "email": "dylan.p@xyz.com",
                        "phone_number": "04609104601",
                        "degree_id": 3
                    },
                    {
                        "id": 3,
                        "name": "Cooper Reeves",
                        "email": "cooper.r@xyz.com",
                        "phone_number": "04288322886",
                        "degree_id": 5
                    },
                    {
                        "id": 4,
                        "name": "Sydney Garza",
                        "email": "sydney.g@xyz.com",
                        "phone_number": "04041513782",
                        "degree_id": 9
                    },
                    {
                        "id": 5,
                        "name": "Grant Olsen",
                        "email": "grant.o@xyz.com",
                        "phone_number": "04194322746",
                        "degree_id": 9
                    },
                    {
                        "id": 6,
                        "name": "George Sherman",
                        "email": "george.s@xyz.com",
                        "phone_number": "04651439197",
                        "degree_id": 4
                    },
                    {
                        "id": 7,
                        "name": "Timothy Howe",
                        "email": "timothy.h@xyz.com",
                        "phone_number": "04246069104",
                        "degree_id": 2
                    },
                    {
                        "id": 8,
                        "name": "Ella Beasley",
                        "email": "ella.b@xyz.com",
                        "phone_number": "04538024649",
                        "degree_id": 5
                    },
                    {
                        "id": 9,
                        "name": "Gracie Colon",
                        "email": "gracie.c@xyz.com",
                        "phone_number": "04812525180",
                        "degree_id": 5
                    },
                    {
                        "id": 10,
                        "name": "Aiden Edwards",
                        "email": "aiden.e@xyz.com",
                        "phone_number": "04948159174",
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
    void testGetStudentById_Valid() {

        Integer id = 1;
        Optional<Student> studentOpt1 = Optional.of(createExampleStudentList().get(0));

        String studentPayload = """
                {
                    "id": 1,
                    "name": "Lily Stuart",
                    "email": "lily.s@xyz.com",
                    "phone_number": "04401697852",
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
    
    @Test
    @SneakyThrows
    void testGetStudentById_Invalid() {

        Integer id = 500;
        
        // When
        when(studentRepository.getStudentById(id))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(get("/student/get/{id}",id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testGetPageOfStudent() {

        // Given
        Integer pageNumber = 0, pageSize = 8, offset = 0;
        List<Student> studentList = createExampleStudentList();
        studentList.remove(9);
        studentList.remove(8);

        String studentListPayload = """
                { "list": [
                        {
                            "id": 1,
                            "name": "Lily Stuart",
                            "email": "lily.s@xyz.com",
                            "phone_number": "04401697852",
                            "degree_id": 5
                        },
                        {
                            "id": 2,
                            "name": "Dylan Phelps",
                            "email": "dylan.p@xyz.com",
                            "phone_number": "04609104601",
                            "degree_id": 3
                        },
                        {
                            "id": 3,
                            "name": "Cooper Reeves",
                            "email": "cooper.r@xyz.com",
                            "phone_number": "04288322886",
                            "degree_id": 5
                        },
                        {
                            "id": 4,
                            "name": "Sydney Garza",
                            "email": "sydney.g@xyz.com",
                            "phone_number": "04041513782",
                            "degree_id": 9
                        },
                        {
                            "id": 5,
                            "name": "Grant Olsen",
                            "email": "grant.o@xyz.com",
                            "phone_number": "04194322746",
                            "degree_id": 9
                        },
                        {
                            "id": 6,
                            "name": "George Sherman",
                            "email": "george.s@xyz.com",
                            "phone_number": "04651439197",
                            "degree_id": 4
                        },
                        {
                            "id": 7,
                            "name": "Timothy Howe",
                            "email": "timothy.h@xyz.com",
                            "phone_number": "04246069104",
                            "degree_id": 2
                        },
                        {
                            "id": 8,
                            "name": "Ella Beasley",
                            "email": "ella.b@xyz.com",
                            "phone_number": "04538024649",
                            "degree_id": 5
                        }
                    ],
                    "totalElements" : 8
                }
                 """;

        // When
        when(studentRepository.getPageOfStudents(pageNumber, pageSize, offset))
                .thenReturn(studentList);

        when(studentRepository.getSizeOfStudentTable())
                .thenReturn(studentList.size());

        // Then
        this.mockMvc.perform(get("/student/get/page/{pageNo}/{pageSize}", pageNumber, pageSize))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(studentListPayload));
    }

    @Test
    @SneakyThrows
    void testSearchStudents() {

        // Given
        String searchTerm = "Scoops";
        Integer pageSize = 10;

        List<Student> studentList = createExampleStudentList();
        List<Student> searchFilteredStudentList = new ArrayList<>();
        searchFilteredStudentList.add(studentList.get(2));

        String studentListPayload = """
                { "list": [
                        {
                            "id": 3,
                            "name": "Cooper Reeves",
                            "email": "cooper.r@xyz.com",
                            "phone_number": "04288322886",
                            "degree_id": 5
                        }
                    ],
                    "totalElements" : 1
                }
                 """;

        // When
        when(studentRepository.searchStudents(searchTerm, pageSize))
                .thenReturn(searchFilteredStudentList);

        // Then
        this.mockMvc.perform(get("/student/search/{searchTerm}/{pageSize}",searchTerm, pageSize))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(studentListPayload));
    }

    @Test
    @SneakyThrows
    void testSaveStudent_ValidUpdate() {
        // Given
        Student newStudent = Student.builder()
                .id(20)
                .name("Grayson Pugh")
                .email("grayson.p@xyz.com")
                .phone_number("551343132")
                .degree_id(5)
                .build();

        String newStudentPayload = """
                {
                    "id": 20,
                    "name": "Grayson Pugh",
                    "email": "grayson.p@xyz.com",
                    "phone_number": "551343132",
                    "degree_id": 5
                }
                """;

        StudentControllerTest.StudentMatcher studentPropertyMatcher = StudentControllerTest.StudentMatcher.builder().left(newStudent).build();

        // When
        when(studentRepository.updateStudent(argThat(studentPropertyMatcher)))
                .thenReturn(Optional.of(newStudent));

        when(studentRepository.saveNewStudent(argThat(studentPropertyMatcher)))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/student/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newStudentPayload))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(newStudentPayload));
    }

    @Test
    @SneakyThrows
    void testSaveStudent_InvalidUpdateNonExistent() {
        // Given
        Student newStudent = Student.builder()
                .id(20)
                .name("Grayson Pugh")
                .email("grayson.p@xyz.com")
                .phone_number("551343132")
                .degree_id(5)
                .build();

        String newStudentPayload = """
                {
                    "id": 100,
                    "name": "Jeremy Button",
                    "email": "j.e.button@xyz.xyz",
                    "phone_number": "111111111",
                    "degree_id": 2
                }
                """;

        StudentControllerTest.StudentMatcher studentPropertyMatcher = StudentControllerTest.StudentMatcher.builder().left(newStudent).build();

        // When
        when(studentRepository.updateStudent(argThat(studentPropertyMatcher)))
                .thenReturn(Optional.empty());

        when(studentRepository.saveNewStudent(any()))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/student/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newStudentPayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testSaveStudent_InvalidProperties() {
        // Given
        Student newStudent = Student.builder()
                .id(20)
                .name("Grayson Pugh")
                .email("grayson.p@xyz.com")
                .phone_number("551343132")
                .degree_id(5)
                .build();

        String newStudentPayload = """
                {
                    "id": 20,
                    "name": "Grayson Pugh",
                    "email": "grayson.p@xyz.com",
                    "phone_number": "551343132",
                    "degree_id": "Five"
                }
                """;

        StudentControllerTest.StudentMatcher studentPropertyMatcher = StudentControllerTest.StudentMatcher.builder().left(newStudent).build();

        // When
        when(studentRepository.updateStudent(argThat(studentPropertyMatcher)))
                .thenReturn(Optional.empty());

        when(studentRepository.saveNewStudent(argThat(studentPropertyMatcher)))
                .thenReturn(Optional.of(newStudent.getId()));

        // Then
        this.mockMvc.perform(put("/student/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newStudentPayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testDeleteStudent_Valid() {
        // Given
        Integer id = 20;

        Student exampleStudent = Student.builder()
                .id(20)
                .name("Grayson Pugh")
                .email("grayson.p@xyz.com")
                .phone_number("551343132")
                .degree_id(5)
                .build();

        // When
        when(studentRepository.getStudentById(id))
                .thenReturn(Optional.of(exampleStudent));
        when(studentRepository.deleteStudent(id))
                .thenReturn(true);

        // Then
        this.mockMvc.perform(delete("/student/delete/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @SneakyThrows
    void testDeleteStudent_InvalidNonExistent() {
        // Given
        Integer id = 5;

        // When
        when(studentRepository.getStudentById(id))
                .thenReturn(Optional.empty());
        when(studentRepository.deleteStudent(id))
                .thenReturn(false);

        // Then
        this.mockMvc.perform(delete("/student/delete/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    /// HELPER FUNCTIONS

    private List<Student> createExampleStudentList() {
        String[] exampleNames = {"Lily Stuart", "Dylan Phelps", "Cooper Reeves", "Sydney Garza", "Grant Olsen",
                "George Sherman", "Timothy Howe", "Ella Beasley", "Gracie Colon", "Aiden Edwards"};
        String[] exampleEmails = {"lily.s@xyz.com", "dylan.p@xyz.com", "cooper.r@xyz.com", "sydney.g@xyz.com",
                "grant.o@xyz.com", "george.s@xyz.com", "timothy.h@xyz.com", "ella.b@xyz.com", "gracie.c@xyz.com",
                "aiden.e@xyz.com"};
        String[] examplePhoneNumber = {"04401697852", "04609104601", "04288322886", "04041513782", "04194322746",
                "04651439197", "04246069104", "04538024649", "04812525180", "04948159174"};
        Integer[] exampleDegreesIds = {5, 3, 5, 9, 9, 4, 2, 5, 5, 5};

        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            studentList.add(Student.builder()
                    .id(i + 1)
                    .name(exampleNames[i])
                    .email(exampleEmails[i])
                    .phone_number(examplePhoneNumber[i])
                    .degree_id(exampleDegreesIds[i])
                    .build());
        }
        return studentList;
    }

    @Builder
    @Getter
    // StudentMatcher makes it so that 2 different student objects with the same property are considered the same
    // even if they are distinct objects within Java.
    private static class StudentMatcher implements ArgumentMatcher<Student> {
        private Student left;

        @Override
        public boolean matches(Student right) {
            return left.getId().equals(right.getId()) &&
                    left.getName().equals(right.getName()) &&
                    left.getEmail().equals(right.getEmail()) &&
                    left.getPhone_number().equals(right.getPhone_number()) &&
                    left.getDegree_id().equals(right.getDegree_id());
        }
    }

}