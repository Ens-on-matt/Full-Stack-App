package com.example.sample_project.controller;

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

    @Test
    @SneakyThrows
    void testListAllDegree() {

        // Given
        List<Degree> testDegreeList = createExampleDegreeList();

        String degreeListPayload = """
                [
                    {
                        "id": 1,
                        "name": "Science"
                    },
                    {
                        "id": 2,
                        "name": "Engineering"
                    },
                    {
                        "id": 3,
                        "name": "Computer Science"
                    },
                    {
                        "id": 4,
                        "name": "Economics"
                    },
                    {
                        "id": 5,
                        "name": "Medicine"
                    },
                    {
                        "id": 6,
                        "name": "Law"
                    },
                    {
                        "id": 7,
                        "name": "Business"
                    },
                    {
                        "id": 8,
                        "name": "Arts"
                    },
                    {
                        "id": 9,
                        "name": "Education"
                    },
                    {
                        "id": 10,
                        "name": "Music"
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
    void testGetDegreeById_Valid() {

        Integer id = 1;
        Optional<Degree> degreeOpt1 = Optional.of(createExampleDegreeList().get(0));

        String degreePayload = """
                {
                    "id": 1,
                    "name": "Science"
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

    @Test
    @SneakyThrows
    void testGetDegreeById_Invalid() {

        Integer id = 500;

        // When
        when(degreeRepository.getDegreeById(id))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(get("/degree/get/{id}",id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    @SneakyThrows
    void testGetPageOfDegree() {

        // Given
        Integer pageNumber = 0, pageSize = 8, offset = 0;
        List<Degree> degreeList = createExampleDegreeList();
        degreeList.remove(9);
        degreeList.remove(8);

        String degreeListPayload = """
                { "list": [
                        {
                            "id": 1,
                            "name": "Science"
                        },
                        {
                            "id": 2,
                            "name": "Engineering"
                        },
                        {
                            "id": 3,
                            "name": "Computer Science"
                        },
                        {
                            "id": 4,
                            "name": "Economics"
                        },
                        {
                            "id": 5,
                            "name": "Medicine"
                        },
                        {
                            "id": 6,
                            "name": "Law"
                        },
                        {
                            "id": 7,
                            "name": "Business"
                        },
                        {
                            "id": 8,
                            "name": "Arts"
                        }
                    ],
                    "totalElements" : 8
                }
                 """;

        // When
        when(degreeRepository.getPageOfDegree(pageNumber, pageSize, offset))
                .thenReturn(degreeList);

        when(degreeRepository.getSizeOfDegreeTable())
                .thenReturn(degreeList.size());

        // Then
        this.mockMvc.perform(get("/degree/get/page/{pageNo}/{pageSize}", pageNumber, pageSize))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(degreeListPayload));
    }

    @Test
    @SneakyThrows
    void testSearchDegrees() {

        // Given
        String searchTerm = "Computer";
        Integer pageSize = 10;

        List<Degree> degreeList = createExampleDegreeList();
        List<Degree> searchFilteredDegreeList = new ArrayList<>();
        searchFilteredDegreeList.add(degreeList.get(2));

        String degreeListPayload = """
                { "list": [
                        {
                            "id": 3,
                            "name": "Computer Science"
                        }
                    ],
                    "totalElements" : 1
                }
                 """;

        // When
        when(degreeRepository.searchDegrees(searchTerm, pageSize))
                .thenReturn(searchFilteredDegreeList);

        // Then
        this.mockMvc.perform(get("/degree/search/{searchTerm}/{pageSize}",searchTerm, pageSize))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(degreeListPayload));
    }

    @Test
    @SneakyThrows
    void testSaveDegree_ValidUpdate() {
        // Given
        Degree newDegree = Degree.builder()
                .id(15)
                .name("Philosophy")
                .build();

        String newDegreePayload = """
                {
                    "id": 15,
                    "name": "Philosophy"
                }
                """;

        DegreeControllerTest.DegreeMatcher degreePropertyMatcher = DegreeControllerTest.DegreeMatcher.builder().left(newDegree).build();

        // When
        when(degreeRepository.updateDegree(argThat(degreePropertyMatcher)))
                .thenReturn(Optional.of(newDegree));

        when(degreeRepository.saveNewDegree(argThat(degreePropertyMatcher)))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/degree/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newDegreePayload))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(newDegreePayload));
    }

    @Test
    @SneakyThrows
    void testSaveDegree_InvalidUpdateNonExistent() {
        // Given
        Degree newDegree = Degree.builder()
                .id(15)
                .name("Philosophy")
                .build();

        String newDegreePayload = """
                {
                    "id": 25,
                    "name": "Philosophy",
                }
                """;

        DegreeControllerTest.DegreeMatcher degreePropertyMatcher = DegreeControllerTest.DegreeMatcher.builder().left(newDegree).build();

        // When
        when(degreeRepository.updateDegree(argThat(degreePropertyMatcher)))
                .thenReturn(Optional.empty());

        when(degreeRepository.saveNewDegree(any()))
                .thenReturn(Optional.empty());

        // Then
        this.mockMvc.perform(put("/degree/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newDegreePayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testSaveDegree_InvalidProperties() {
        // Given
        Degree newDegree = Degree.builder()
                .id(15)
                .name("Philosophy")
                .build();

        String newDegreePayload = """
                {
                    "id": "Fifteen",
                    "name": "Philosophy",
                }
                """;

        DegreeControllerTest.DegreeMatcher degreePropertyMatcher = DegreeControllerTest.DegreeMatcher.builder().left(newDegree).build();

        // When
        when(degreeRepository.updateDegree(argThat(degreePropertyMatcher)))
                .thenReturn(Optional.empty());

        when(degreeRepository.saveNewDegree(argThat(degreePropertyMatcher)))
                .thenReturn(Optional.of(newDegree.getId()));

        // Then
        this.mockMvc.perform(put("/degree/put").contentType(MediaType.APPLICATION_JSON)
                        .content(newDegreePayload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testDeleteDegree_Valid() {
        // Given
        Integer id = 20;

        Degree exampleDegree = Degree.builder()
                .id(20)
                .name("Philosophy")
                .build();

        // When
        when(degreeRepository.getDegreeById(id))
                .thenReturn(Optional.of(exampleDegree));
        when(degreeRepository.deleteDegree(id))
                .thenReturn(true);

        // Then
        this.mockMvc.perform(delete("/degree/delete/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @SneakyThrows
    void testDeleteDegree_InvalidNonExistent() {
        // Given
        Integer id = 5;

        // When
        when(degreeRepository.getDegreeById(id))
                .thenReturn(Optional.empty());
        when(degreeRepository.deleteDegree(id))
                .thenReturn(false);

        // Then
        this.mockMvc.perform(delete("/degree/delete/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    /// HELPER FUNCTIONS

    private List<Degree> createExampleDegreeList() {
        String[] exampleNames = {"Science", "Engineering", "Computer Science", "Economics", "Medicine", "Law",
                "Business", "Arts", "Education", "Music"};

        List<Degree> degreeList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            degreeList.add(Degree.builder()
                    .id(i + 1)
                    .name(exampleNames[i])
                    .build());
        }
        return degreeList;
    }

    @Builder
    @Getter
    // DegreeMatcher makes it so that 2 different degree objects with the same property are considered the same
    // even if they are distinct objects within Java.
    private static class DegreeMatcher implements ArgumentMatcher<Degree> {
        private Degree left;

        @Override
        public boolean matches(Degree right) {
            return left.getId().equals(right.getId()) &&
                    left.getName().equals(right.getName());
        }
    }

}