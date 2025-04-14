package com.example.sample_project.matchers;

import com.example.sample_project.model.Student;
import lombok.Builder;
import lombok.Getter;
import org.mockito.ArgumentMatcher;

@Builder
@Getter
// StudentMatcher makes it so that 2 different student objects with the same property are considered the same
// even if they are distinct objects within Java.
public class StudentMatcher implements ArgumentMatcher<Student> {
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
