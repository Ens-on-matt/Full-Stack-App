package com.example.sample_project.matchers;

import com.example.sample_project.model.Course;
import lombok.Builder;
import lombok.Getter;
import org.mockito.ArgumentMatcher;

@Builder
@Getter
// CourseMatcher makes it so that 2 different course objects with the same property are considered the same
// even if they are distinct objects within Java.
public class CourseMatcher implements ArgumentMatcher<Course> {
    private Course left;

    @Override
    public boolean matches(Course right) {
        return left.getId().equals(right.getId()) &&
                left.getName().equals(right.getName()) &&
                left.getProfessor_id().equals(right.getProfessor_id()) &&
                left.getDegree_id().equals(right.getDegree_id());
    }
}