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
import com.example.sample_project.model.Degree;
import com.example.sample_project.repository.DegreeRepository;

import io.micrometer.common.lang.NonNull;
import lombok.SneakyThrows;

@SpringBootTest(classes = SampleProjectApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DegreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    DegreeRepository degreeRepository;

    //@Test
    @SneakyThrows
    void testListAllDegree_success() {

        // Given
        List<Degree> testDegreeList = List.of (
            anyDegree(1, "Computer Science"),
            anyDegree(2, "Law")
        );

        String degreeListPayload = """
                [
                    {
                        "id": 1,
                        "name": "Computer Science"
                    },
                    {
                        "id": 2,
                        "name": "Law"
                    }
                ]
                 """; 

        // When
        when(degreeRepository.listAllDegrees())
               .thenReturn(testDegreeList);

        // Then
        this.mockMvc.perform(get("/degree/list-all"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(degreeListPayload));
    }

    @Test
    @SneakyThrows
    void testGetDegreeById_success() {

        Integer id = 1;
        Optional<Degree> degreeOpt1 = Optional.of(anyDegree(1, "Computer Science"));

        String degreePayload = """
                {
                    "id": 1,
                    "name": "Computer Science"
                }
                 """; 

        // When
        when(degreeRepository.getDegreeById(id))
               .thenReturn(degreeOpt1);

        // Then
        this.mockMvc.perform(get("/degree/get/{id}",id))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(degreePayload));
    }

    public static Degree anyDegree(@NonNull Integer id, @NonNull String name) {
        return Degree.builder()
            .id(id)
            .name(name)
        .build();
    }
}