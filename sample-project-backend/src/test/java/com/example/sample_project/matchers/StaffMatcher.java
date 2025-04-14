package com.example.sample_project.matchers;

import com.example.sample_project.model.Staff;
import lombok.Builder;
import lombok.Getter;
import org.mockito.ArgumentMatcher;

@Builder
@Getter
// StaffMatcher makes it so that 2 different staff objects with the same property are considered the same
// even if they are distinct objects within Java.
public class StaffMatcher implements ArgumentMatcher<Staff> {
    private Staff left;

    @Override
    public boolean matches(Staff right) {
        return left.getId().equals(right.getId()) &&
                left.getName().equals(right.getName()) &&
                left.getEmail().equals(right.getEmail()) &&
                left.getPhone_number().equals(right.getPhone_number()) &&
                left.getSalary().equals(right.getSalary());
    }
}