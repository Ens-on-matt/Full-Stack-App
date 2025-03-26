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
import com.example.sample_project.model.Staff;
import com.example.sample_project.repository.StaffRepository;

import io.micrometer.common.lang.NonNull;
import lombok.SneakyThrows;

@SpringBootTest(classes = SampleProjectApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    StaffRepository staffRepository;

    //@Test
    @SneakyThrows
    void testListAllStaff_success() {

        // Given
        List<Staff> testStaffList = List.of (
            anyStaff(1, "staff1"),
            anyStaff(2, "staff2")
        );

        String staffListPayload = """
                [
                    {
                        "id": 1,
                        "name": "staff1",
                        "email": "staff@example.com",
                        "phone_number": "041122223333",
                        "salary": 100000,
                        "job": "Lecturer"
                    },
                    {
                        "id": 2,
                        "name": "staff2",
                        "email": "staff@example.com",
                        "phone_number": "041122223333",
                        "salary": 100000,
                        "job": "Lecturer"
                    }
                ]
                 """; 

        // When
        when(staffRepository.listAllStaff())
               .thenReturn(testStaffList);

        // Then
        this.mockMvc.perform(get("/staff/list-all"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(staffListPayload));
    }

    @Test
    @SneakyThrows
    void testGetStaffById_success() {

        Integer id = 1;
        Optional<Staff> staffOpt1 = Optional.of(anyStaff(1, "staff1"));

        String staffPayload = """
                {
                    "id": 1,
                    "name": "staff1",
                    "email": "staff@example.com",
                    "phone_number": "041122223333",
                    "salary": 100000,
                    "job": "Lecturer"
                }
                 """; 

        // When
        when(staffRepository.getStaffById(id))
               .thenReturn(staffOpt1);

        // Then
        this.mockMvc.perform(get("/staff/get/{id}",id))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(staffPayload));
    }

    public static Staff anyStaff(@NonNull Integer id, @NonNull String name) {
        return Staff.builder()
            .id(id)
            .name(name)
            .email("staff@example.com")
            .phone_number("041122223333")
            .salary(100000)
            .job("Lecturer")                
        .build();
    }
}