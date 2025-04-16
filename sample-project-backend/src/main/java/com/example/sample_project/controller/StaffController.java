package com.example.sample_project.controller;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.sample_project.controller.Response.notFound404;

import com.example.sample_project.model.Staff;
import com.example.sample_project.repository.StaffRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/staff", produces = MediaType.APPLICATION_JSON_VALUE)
public class StaffController {

    private final Logger LOGGER = LoggerFactory.getLogger(StaffController.class);

    private final StaffRepository staffRepository;
    
    @GetMapping(value = "/list-all")
    public ResponseEntity<ResponseObject> listAllStaff() 
    {
       LOGGER.info("Controller listAllStaff called");

        List<Staff> staffList = staffRepository.listAllStaff();
        StaffListDTO response = new StaffListDTO();
        response.addAll(staffList);

        if (staffList.size() > 0) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return notFound404("No staff found.");
        }
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<ResponseObject> getStaffById (
        @PathVariable Integer id
    ) {
       LOGGER.info("Controller getStaffById called");

        Optional<Staff> staffOpt = staffRepository.getStaffById(id);

        if (staffOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(staffOpt.get());
        } else {
            return notFound404("No staff found.");
        }
    }


    @GetMapping(value = "/get/page/{pageNo}/{pageSize}")
    public ResponseEntity<ResponseObject> listPageOfStaff (@PathVariable Integer pageNo,
    @PathVariable Integer pageSize) {
        LOGGER.info("Controller listStaffPage called");
        
        List<Staff> staffList = staffRepository.getPageOfStaff(pageNo, pageSize, 0);
        int totalPages = staffRepository.getSizeOfStaffTable();
        StaffPageDTO pageResponse = new StaffPageDTO(staffList, totalPages);
  
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(pageResponse);
    }

    @GetMapping(value = "/search/{searchTerm}/{pageSize}")
    public ResponseEntity<ResponseObject> searchStaffMembers (@PathVariable String searchTerm,
                                                              @PathVariable Integer pageSize) {
        LOGGER.info("Controller searchStaffMembers called");

        List<Staff> staffList = staffRepository.searchStaffMembers(searchTerm, pageSize);
        StaffPageDTO response = new StaffPageDTO(staffList, 1);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping(value = "/list-all-professors")
    public ResponseEntity<ResponseObject> getAllProfessors () {
        LOGGER.info("Controller getAllProfessors called");
        List<Staff> staffList = staffRepository.listAllProfessors();
        StaffListDTO response = new StaffListDTO();
        response.addAll(staffList);

        if (!staffList.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return notFound404("No professors found.");
        }
    }

    @GetMapping(value = "/searchProfessor/{pageSize}")
    public ResponseEntity<ResponseObject> searchProfessors (@RequestParam(required = false) String searchTerm,
                                                              @PathVariable Integer pageSize) {
        LOGGER.info("Controller searchProfessors called");
        List<Staff> staffList;

        if (searchTerm == null || searchTerm.isEmpty()) {
            // No search term
            staffList = staffRepository.getPageOfProfessors(0, pageSize, 0);
        } else {
            // Search term provided
            staffList = staffRepository.searchProfessors(searchTerm, pageSize);

        }
        StaffPageDTO response = new StaffPageDTO(staffList, 1);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> saveStaffMember (@RequestBody Staff staff) {
        if (staff.getId() == -1) {
            return saveNewStaffMember(staff);
        } else {
            return updateStaffMember(staff);
        }
    }

    public ResponseEntity<ResponseObject> saveNewStaffMember (Staff staff) {
        LOGGER.info("Controller saveNewStaffMember called");

        Optional<Integer> staffOpt = staffRepository.saveNewStaffMember(staff);

        if (staffOpt.isPresent()) {
            Staff updated_staff = staff.withId(staffOpt.get());
            LOGGER.info("New staff member has id {}", staffOpt.get());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(updated_staff);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    public ResponseEntity<ResponseObject> updateStaffMember (Staff staff) {
        LOGGER.info("Controller updateStaffMember called");

        Optional<Staff> staffOpt = staffRepository.updateStaffMember(staff);

        if (staffOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(staffOpt.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @DeleteMapping(value = "delete/{id}")
    public ResponseEntity<ResponseObject> deleteStaffMember (@PathVariable Integer id) {
        LOGGER.info("Controller deleteStaffMember called");

        Optional<Staff> staffOpt = staffRepository.getStaffById(id);

        if (staffOpt.isPresent()) {
            Staff staff_in_db = staffOpt.get();
            LOGGER.info("Deleting {}", staff_in_db);
            if (staffRepository.deleteStaffMember(id)) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(null);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        return notFound404("Staff member provided not found.");
    }

    @AllArgsConstructor
    @Getter
    static class StaffPageDTO implements ResponseObject {
        List<Staff> list;
        int totalElements;
    }

    @AllArgsConstructor
    @Getter
    static class StaffListDTO extends ArrayList<Staff> implements ResponseObject {
    }
}