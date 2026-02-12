package com.origin;

import com.origin.entity.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Main {

    @Autowired
    private FriendRepository friendRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }
}