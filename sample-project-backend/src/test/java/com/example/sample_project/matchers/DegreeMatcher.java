package com.example.sample_project.matchers;

import com.example.sample_project.model.Degree;
import lombok.Builder;
import lombok.Getter;
import org.mockito.ArgumentMatcher;

@Builder
@Getter
// DegreeMatcher makes it so that 2 different degree objects with the same property are considered the same
// even if they are distinct objects within Java.
public class DegreeMatcher implements ArgumentMatcher<Degree> {
    private Degree left;

    @Override
    public boolean matches(Degree right) {
        return left.getId().equals(right.getId()) &&
                left.getName().equals(right.getName());
    }
}