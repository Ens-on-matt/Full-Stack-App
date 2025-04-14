package com.example.sample_project.controller;

import com.example.sample_project.matchers.CourseMatcher;
import com.example.sample_project.model.Degree;
import com.example.sample_project.model.Staff;
import com.example.sample_project.model.Student;
import lombok.Builder;
import lombok.Getter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
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

    @Test
    @SneakyThrows
    void testListAllCourse() {

        // Given
        List<Course> testCourseList = createExampleCourseList();

        String courseListPayload = """
                [
                    {
                        "id": 1,
                        "professor_id": 29,
                        "name": "Surgical Techniques",
                        "degree_id": 5
                    },
                    {
                        "id": 2,
                        "professor_id": 5,
                        "name": "Principles of Management",
                        "degree_id": 7
                    },
                    {
                        "id": 3,
                        "professor_id": 1,
                        "name": "Principles of Biochemistry",
                        "degree_id": 1
                    },
                    {
                        "id": 4,
                        "professor_id": 5,
                        "name": "Data Structures and Algorithms",
                        "degree_id": 3
                    },
                    {
                        "id": 5,
                        "professor_id": 29,
                        "name": "Clinical Skills and Practices",
                        "degree_id": 5
                    },
                    {
                        "id": 6,
                        "professor_id": 30,
                        "name": "Microbiology and Infectious Diseases",
                        "degree_id": 5
                    },
                    {
                        "id": 7,
                        "professor_id": 1,
                        "name": "Astrophysics",
                        "degree_id": 1
                    },
                    {
                        "id": 8,
                        "professor_id": 14,
                        "name": "Genetics and Evolution",
                        "degree_id": 1
                    },
                    {
                        "id": 9,
                        "professor_id": 1,
                        "name": "Physics of the Universe",
                        "degree_id": 1
                    },
                    {
                        "id": 10,
                        "professor_id": 1,
                        "name": "Geology and Earth Systems",
                        "degree_id": 1
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
    void testGetCourseById_Valid() {

        Integer id = 1;
        Optional<Course> courseOpt1 = Optional.of(createExampleCourseList().get(0));

        String coursePayload = """
                {
                    "id": 1,
                    "professor_id": 29,
                    "name": "Surgical Techniques",
                    "degree_id": 5
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

    @Test
    @SneakyThrows
    void testGetCourseById_Invalid() {

        Integer id = 500;

        // When
        when(courseRepository.getCourseById(id))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(get("/course/get/{id}",id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testGetPageOfCourse() {

        // Given
        Integer pageNumber = 0, pageSize = 8, offset = 0;
        List<Course> courseList = createExampleCourseList();
        courseList.remove(9);
        courseList.remove(8);

        String courseListPayload = """
                { "list": [
                        {
                            "id": 1,
                            "professor_id": 29,
                            "name": "Surgical Techniques",
                            "degree_id": 5
                        },
                        {
                            "id": 2,
                            "professor_id": 5,
                            "name": "Principles of Management",
                            "degree_id": 7
                        },
                        {
                            "id": 3,
                            "professor_id": 1,
                            "name": "Principles of Biochemistry",
                            "degree_id": 1
                        },
                        {
                            "id": 4,
                            "professor_id": 5,
                            "name": "Data Structures and Algorithms",
                            "degree_id": 3
                        },
                        {
                            "id": 5,
                            "professor_id": 29,
                            "name": "Clinical Skills and Practices",
                            "degree_id": 5
                        },
                        {
                            "id": 6,
                            "professor_id": 30,
                            "name": "Microbiology and Infectious Diseases",
                            "degree_id": 5
                        },
                        {
                            "id": 7,
                            "professor_id": 1,
                            "name": "Astrophysics",
                            "degree_id": 1
                        },
                        {
                            "id": 8,
                            "professor_id": 14,
                            "name": "Genetics and Evolution",
                            "degree_id": 1
                        }
                    ],
                    "totalElements" : 8
                }
                 """;

        // When
        when(courseRepository.getPageOfCourse(pageNumber, pageSize, offset))
                .thenReturn(courseList);

        when(courseRepository.getSizeOfCourseTable())
                .thenReturn(courseList.size());

        // Then
        this.mockMvc.perform(get("/course/get/page/{pageNo}/{pageSize}", pageNumber, pageSize))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(courseListPayload));
    }

    @Test
    @SneakyThrows
    void testSearchCourses() {

        // Given
        String searchTerm = "Principles";
        Integer pageSize = 10;

        List<Course> courseList = createExampleCourseList();
        List<Course> searchFilteredCourseList = new ArrayList<>();
        searchFilteredCourseList.add(courseList.get(1));
        searchFilteredCourseList.add(courseList.get(2));

        String courseListPayload = """
                { "list": [
                        {
                            "id": 2,
                            "professor_id": 5,
                            "name": "Principles of Management",
                            "degree_id": 7
                        },
                        {
                            "id": 3,
                            "professor_id": 1,
                            "name": "Principles of Biochemistry",
                            "degree_id": 1
                        }
                    ],
                    "totalElements" : 1
                }
                 """;

        // When
        when(courseRepository.searchCourses(searchTerm, pageSize))
                .thenReturn(searchFilteredCourseList);

        // Then
        this.mockMvc.perform(get("/course/search/{searchTerm}/{pageSize}",searchTerm, pageSize))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(courseListPayload));
    }

    @Test
    @SneakyThrows
    void testGetCoursesRequiredForDegree() {

        Integer degree_id = 5;
        List<Course> courseList = List.of(new Course[]{
                Course.builder().id(1).professor_id(29).name("Surgical Techniques").degree_id(5).build(),
                Course.builder().id(5).professor_id(29).name("Clinical Skills and Practices").degree_id(5).build(),
                Course.builder().id(6).professor_id(30).name("Microbiology and Infectious Diseases").degree_id(5).build()
        });

        String coursePayload = """
                [
                    {
                        "id": 1,
                        "professor_id": 29,
                        "name": "Surgical Techniques",
                        "degree_id": 5
                    },
                    {
                        "id": 5,
                        "professor_id": 29,
                        "name": "Clinical Skills and Practices",
                        "degree_id": 5
                    },
                    {
                        "id": 6,
                        "professor_id": 30,
                        "name": "Microbiology and Infectious Diseases",
                        "degree_id": 5
                    }
                ]
                 """;

        // When
        when(courseRepository.getCoursesRequiredForDegree(degree_id))
                .thenReturn(courseList);

        // Then
        this.mockMvc.perform(get("/course/courses-for-degree/{id}",degree_id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(coursePayload));
    }


    @Test
    @SneakyThrows
    void testSaveCourse_ValidUpdate() {
        // Given
        Course newCourse = Course.builder()
                .id(20)
                .name("Contemporary Art")
                .professor_id(14)
                .degree_id(8)
                .build();

        String newCoursePayload = """
                {
                    "id": 20,
                    "name": "Contemporary Art",
                    "professor_id": 14,
                    "degree_id": 8
                }
                """;

        CourseMatcher coursePropertyMatcher = CourseMatcher.builder().left(newCourse).build();

        // When
        when(courseRepository.updateCourse(argThat(coursePropertyMatcher)))
                .thenReturn(Optional.of(newCourse));

        when(courseRepository.saveNewCourse(argThat(coursePropertyMatcher)))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/course/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newCoursePayload))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(newCoursePayload));
    }

    @Test
    @SneakyThrows
    void testSaveCourse_InvalidUpdateNonExistent() {
        // Given
        Course newCourse = Course.builder()
                .id(20)
                .name("Contemporary Art")
                .professor_id(14)
                .degree_id(8)
                .build();

        String newCoursePayload = """
                {
                    "id": 37,
                    "name": "Renaissance Art",
                    "professor_id": 1,
                    "degree_id": 8
                }
                """;

        CourseMatcher coursePropertyMatcher = CourseMatcher.builder().left(newCourse).build();

        // When
        when(courseRepository.updateCourse(argThat(coursePropertyMatcher)))
                .thenReturn(Optional.empty());

        when(courseRepository.saveNewCourse(any()))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/course/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newCoursePayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testSaveCourse_InvalidProperties() {
        // Given
        Course newCourse = Course.builder()
                .id(20)
                .name("Contemporary Art")
                .professor_id(14)
                .degree_id(8)
                .build();

        String newCoursePayload = """
                {
                    "id": "Twenty",
                    "name": "Contemporary Art",
                    "professor_id": 14,
                    "degree_id": 8
                }
                """;

        CourseMatcher coursePropertyMatcher = CourseMatcher.builder().left(newCourse).build();

        // When
        when(courseRepository.updateCourse(argThat(coursePropertyMatcher)))
                .thenReturn(Optional.empty());

        when(courseRepository.saveNewCourse(argThat(coursePropertyMatcher)))
                .thenReturn(Optional.of(newCourse.getId()));

        // Then
        this.mockMvc.perform(put("/course/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newCoursePayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testDeleteCourse_Valid() {
        // Given
        Integer id = 20;

        Course exampleCourse = Course.builder()
                .id(20)
                .name("Contemporary Art")
                .professor_id(14)
                .degree_id(8)
                .build();

        // When
        when(courseRepository.getCourseById(id))
                .thenReturn(Optional.of(exampleCourse));
        when(courseRepository.deleteCourse(id))
                .thenReturn(true);

        // Then
        this.mockMvc.perform(delete("/course/delete/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @SneakyThrows
    void testDeleteCourse_InvalidNonExistent() {
        // Given
        Integer id = 5;

        // When
        when(courseRepository.getCourseById(id))
                .thenReturn(Optional.empty());
        when(courseRepository.deleteCourse(id))
                .thenReturn(false);

        // Then
        this.mockMvc.perform(delete("/course/delete/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    
    /// HELPER FUNCTIONS

    private List<Course> createExampleCourseList() {
        String[] exampleNames = {"Surgical Techniques", "Principles of Management", "Principles of Biochemistry",
                "Data Structures and Algorithms", "Clinical Skills and Practices", "Microbiology and Infectious Diseases",
                "Astrophysics", "Genetics and Evolution", "Physics of the Universe", "Geology and Earth Systems"};
        Integer[] exampleProfessorIds = {29, 5, 1, 5, 29, 30, 1, 14, 1, 1};
        Integer[] exampleDegreesIds = {5, 7, 1, 3, 5, 5, 1, 1, 1, 1};

        List<Course> courseList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            courseList.add(Course.builder()
                    .id(i + 1)
                    .name(exampleNames[i])
                    .professor_id(exampleProfessorIds[i])
                    .degree_id(exampleDegreesIds[i])
                    .build());
        }
        return courseList;
    }
}