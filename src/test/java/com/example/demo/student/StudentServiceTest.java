package com.example.demo.student;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @InjectMocks
    private StudentService underTest;
    @Mock
    private StudentRepository studentRepository;
    @Captor
    private ArgumentCaptor<Student> studentCaptor;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudentsSuccessfully() {
        //when
        underTest.getStudents();
        //then
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudentSuccessfully() {
        //given
        Long studentId = 1L;
        Student jamila = new Student(
                3L,
                "Jamila",
                "jamila@gmail.com",
                LocalDate.of(2001, Month.JANUARY, 5)
        );
        //when
        underTest.addNewStudent(jamila);
        //then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentArgumentCaptor.capture());
        Student capturedStudent = studentArgumentCaptor.getValue();
        Assertions.assertThat(capturedStudent).isEqualTo(jamila);

    }

    @Test
    void willThrowIllegalStateExceptionWhenEmailIsAlreadyTaken() {
        //given
        Student student = new Student(
                "Jamila",
                "jamila@gmail.com",
                LocalDate.of(2001, Month.JANUARY, 5)
        );
        //when
        given(studentRepository.findStudentByEmail(student.getEmail())).willReturn(Optional.of(student));
        //then
        assertThatThrownBy(() -> underTest.addNewStudent(student))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Sorry email is already taken...");
        verify(studentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudentSuccessfully() {
        //given
        long id = 10;
        given(studentRepository.existsById(id)).willReturn(true);
        //when
        underTest.deleteStudent(id);
        //then
        verify(studentRepository).deleteById(id);
    }

    @Test
    void willThrowIllegalStateExceptionWhenStudentDoNotExistsWhenDeleteStudent() {
        //given
        long id = 10;
        given(studentRepository.existsById(id)).willReturn(false);
        //when
        //then
        assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Student with id " + id + " does not exists...");
        verify(studentRepository, never()).deleteById(anyLong());
    }

    @Test
    void canUpdateStudentSuccessfully() {
        //given
        Long studentId = 1L;
        String studentName = "Kalle";
        String studentEmail = "kalle@gmail.com";
        Student existingStudent = new Student(
                studentId,
                "Old name",
                "old@gmail.com",
                LocalDate.of(2001, Month.JANUARY, 5)
        );
        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.findStudentByEmail(studentEmail)).thenReturn(null);
        //when
        underTest.updateStudent(studentId, studentName, studentEmail);
        //then
        verify(studentRepository).save(studentCaptor.capture());
        Student updatedStudent = studentCaptor.getValue();
        Assertions.assertThat(updatedStudent.getId()).isEqualTo(studentId);
        Assertions.assertThat(updatedStudent.getName()).isEqualTo(studentName);
        Assertions.assertThat(updatedStudent.getEmail()).isEqualTo(studentEmail);
    }

    @Test
    void willThrowIllegalStateExceptionWhenStudentNameIsInvalid() {
        //given
        Long studentId = 1L;
        String invalidStudentName = ""; //Invalid name
        String studentEmail = "kalle@gmail.com";
        Student existingStudent = new Student(
                studentId,
                "Old name",
                "old@gmail.com",
                LocalDate.of(2001, Month.JANUARY, 5)
        );
        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        //when
        //then
        assertThatThrownBy(() -> underTest.updateStudent(studentId, invalidStudentName, studentEmail))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("studentName i already taken or student name length has to be more than 0 or studentName is null...");
    }

    @Test
    void willThrowIllegalStateExceptionWhenStudentEmailIsInvalid() {
        //given
        Long studentId = 1L;
        String invalidStudentName = "Kalle"; //Invalid name
        String invalidStudentEmail = "";
        Student existingStudent = new Student(
                studentId,
                "Old name",
                "old@gmail.com",
                LocalDate.of(2001, Month.JANUARY, 5)
        );
        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        //when
        //then
        assertThatThrownBy(() -> underTest.updateStudent(studentId, invalidStudentName, invalidStudentEmail))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("studentEmail i already taken or studentEmail length has to be more than 0");
    }

    @Test
    void willThrowIllegalStateExceptionWhenStudentDoNotExistsWhenUpdateStudent() {
        //given
        long id = 10;
        given(studentRepository.existsById(id)).willReturn(false);
        //when
        //then
        assertThatThrownBy(() -> underTest.updateStudent(id, "Kalle", "kalle@gmail.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Student with id " + id + " does not exists...");
        verify(studentRepository, never()).save(any());
    }
}