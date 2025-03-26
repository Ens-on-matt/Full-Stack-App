package com.example.sample_project.controller;

import com.example.sample_project.model.Course;
import com.example.sample_project.model.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.sample_project.model.Degree;
import com.example.sample_project.repository.DegreeRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.example.sample_project.controller.Response.internalServer500;
import static com.example.sample_project.controller.Response.notFound404;
import static java.lang.Math.min;

//@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/degree", produces = MediaType.APPLICATION_JSON_VALUE)
public class DegreeController {

    private final Logger LOGGER = LoggerFactory.getLogger(DegreeController.class);

    private final DegreeRepository degreeRepository;
    
    @GetMapping(value = "/list-all")
    public ResponseEntity<ResponseObject> listAllDegrees() 
    {
       LOGGER.info("Controller listAllDegrees called");

        List<Degree> degreeList = degreeRepository.listAllDegrees();
        DegreeListDTO response = new DegreeListDTO();
        response.addAll(degreeList);

        if (degreeList.size() > 0) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return notFound404("No degree found.");
        }
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<ResponseObject> getDegreeById (
        @PathVariable Integer id
    ) {
       LOGGER.info("Controller listAllDegrees called");

        Optional<Degree> degreeOpt = degreeRepository.getDegreeById(id);

        if (degreeOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(degreeOpt.get());
        } else {
            return notFound404("No degree found.");
        }
    }

    @GetMapping(value = "/get/page/{pageNo}/{pageSize}")
    public ResponseEntity<ResponseObject> listPageOfDegrees (@PathVariable Integer pageNo,
                                                           @PathVariable Integer pageSize) {
        LOGGER.info("Controller listDegreePage called");

        List<Degree> DegreeList = degreeRepository.getPageOfDegree(pageNo, pageSize, 0);
        int totalPages = degreeRepository.getSizeOfDegreeTable();
        DegreePageDTO pageResponse = new DegreePageDTO(DegreeList, totalPages);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pageResponse);
    }

    @GetMapping(value = "/search/{searchTerm}/{pageSize}")
    public ResponseEntity<ResponseObject> searchDegrees (@PathVariable String searchTerm,
                                                               @PathVariable Integer pageSize) {
        LOGGER.info("Controller searchDegrees called");
        List<Degree> degreeList = degreeRepository.searchDegrees(searchTerm, pageSize);
        DegreePageDTO response = new DegreePageDTO(degreeList, 1);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> saveDegree (@RequestBody Degree degree) {
        if (degree.getId() == -1) {
            return saveNewDegree(degree);
        } else {
            return updateDegree(degree);
        }
    }

    public ResponseEntity<ResponseObject> saveNewDegree (Degree degree) {
        LOGGER.info("Controller saveNewDegree called");

        Optional<Integer> degreeOpt = degreeRepository.saveNewDegree(degree);

        if (degreeOpt.isPresent()) {
            Degree updated_degree = degree.withId(degreeOpt.get());
            LOGGER.info("New degree member has id {}", degreeOpt.get());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(updated_degree);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    public ResponseEntity<ResponseObject> updateDegree (Degree degree) {
        LOGGER.info("Controller updateDegree called");

        Optional<Degree> degreeOpt = degreeRepository.updateDegreeMember(degree);

        if (degreeOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(degreeOpt.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }


    @DeleteMapping(value = "delete/{id}")
    public ResponseEntity<ResponseObject> deleteDegree (@PathVariable Integer id) {
        LOGGER.info("Controller deleteDegree called");

        Optional<Degree> degreeOpt = degreeRepository.getDegreeById(id);

        if (degreeOpt.isPresent()) {
            Degree degree_in_db = degreeOpt.get();
            LOGGER.info("Deleting {}", degree_in_db);
            if (degreeRepository.deleteDegree(id)) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(null);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        return notFound404("Degree member provided not found.");
    }

    @AllArgsConstructor
    @Getter
    static class DegreePageDTO implements ResponseObject {
        List<Degree> list;
        int totalElements;
    }

    @AllArgsConstructor
    @Getter
    static class DegreeListDTO extends ArrayList<Degree> implements ResponseObject {
    }
}