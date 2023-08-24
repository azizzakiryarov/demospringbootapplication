package com.example.demo.student;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public void addNewStudent(Student student) {

        Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
        if (studentOptional.isPresent()) {
            throw new IllegalStateException("Sorry email is already taken...");
        }
        studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalStateException(
                    "Student with id " + studentId + " does not exists..."
            );
        }
        studentRepository.deleteById(studentId);
    }

    @Transactional
    public void updateStudent(Long studentId, String studentName, String studentEmail) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalStateException(
                    "Student with id " + studentId + " does not exists..."
            );
        }

        Optional<Student> studentOptionalUpdated = studentRepository.findById(studentId).map(student -> {

            if (Objects.nonNull(studentName) && studentName.length() > 0 && !Objects.equals(student.getName(), studentName)) {
                student.setName(studentName);
            } else {
                throw new IllegalStateException("studentName i already taken or student name length has to be more than 0 or studentName is null...");
            }

            if (Objects.nonNull(studentEmail) && studentEmail.length() > 0 && !Objects.equals(studentRepository.findStudentByEmail(studentEmail), studentEmail)) {
                student.setEmail(studentEmail);
            } else {
                throw new IllegalStateException("studentEmail i already taken or studentEmail length has to be more than 0");
            }
            return student;
        });

        studentRepository.save(studentOptionalUpdated.get());
    }
}