package com.example.rumpy.command_line_runner;

import com.example.rumpy.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
//        var user = new User();
//        log.info("User Entity Record: {}", user.getEntityRecord());
    }
}//end class MyRunner
