package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDate;
import java.time.Month;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;
    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindStudentByEmail() {
        // given
        Student jamila = new Student(
                3L,
                "Jamila",
                "jamila@gmail.com",
                LocalDate.of(2001, Month.JANUARY, 5)
        );
        underTest.save(jamila);
        // when
        boolean exists = underTest.findStudentByEmail(jamila.getEmail()).isPresent();
        // then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldNotFindStudentByEmail() {
        // given
        Student jamila = new Student(
                3L,
                "Jamila",
                "jamila@gmail.com",
                LocalDate.of(2001, Month.JANUARY, 5)
        );
        // when
        boolean notExists = underTest.findStudentByEmail(jamila.getEmail()).isPresent();
        // then
        assertThat(notExists).isFalse();
    }
}