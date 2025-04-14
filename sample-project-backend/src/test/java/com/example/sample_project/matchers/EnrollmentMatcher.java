package com.example.sample_project.matchers;

import com.example.sample_project.model.Enrollment;
import lombok.Builder;
import lombok.Getter;
import org.mockito.ArgumentMatcher;

@Builder
@Getter
// EnrollmentMatcher makes it so that 2 different enrollment objects with the same property are considered the same
// even if they are distinct objects within Java.
public class EnrollmentMatcher implements ArgumentMatcher<Enrollment> {
    private Enrollment left;

    @Override
    public boolean matches(Enrollment right) {
        return left.getStudent().equals(right.getStudent()) &&
                left.getCourse().equals(right.getCourse()) &&
                left.getStatus().equals(right.getStatus());
    }
}
