package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private static void usingMongoTemplateAndQuery(StudentRepository repository, MongoTemplate mongoTemplate, Student student) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(student.getEmail()));
        List<Student> students = mongoTemplate.find(query, Student.class);

        if (!students.isEmpty()) {
            System.out.println("Already exist " + student);
            throw new IllegalStateException("found many students with email " + student.getEmail());
        }
        System.out.println("Inserting student " + student);
        repository.insert(student);
    }

    @Bean
    CommandLineRunner runner(StudentRepository repository,
                             MongoTemplate mongoTemplate) {
        return args -> {
            Address address = new Address(
                    "England",
                    "06420",
                    "London"
            );
            Student student = new Student(
                    "Jamile",
                    "Ahmed",
                    "ahemt@gmail.com",
                    Gender.FEMALE,
                    address,
                    List.of("Computer Science", "Maths"),
                    BigDecimal.TEN,
                    LocalDateTime.now()
            );
//            usingMongoTemplateAndQuery(repository, mongoTemplate, student);
            repository.findStudentByEmailEquals(student.getEmail())
                    .ifPresentOrElse(s -> {
                        System.out.println("Already exist " + student);
                        throw new IllegalStateException("found many students with email " + student.getEmail());
                    }, () -> {
                        System.out.println("Inserting student " + student);
                        repository.insert(student);
                    });
        };
    }
}
