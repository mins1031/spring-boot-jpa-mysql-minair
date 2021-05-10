package com.minair.minair.basictest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class basicTest {

    @Test
    public void test(){
        LocalTime time = LocalTime.now();
        LocalDate date = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.now();

        System.out.println(time);
        System.out.println(date);
        System.out.println(localDateTime);

        List<String> test = new ArrayList<>();
        test.add("0");
        test.add("2");
        test.add("8");
        System.out.println(test);

    }
}
