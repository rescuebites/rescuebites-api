package com.rescuebites.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.rescuebites.api.config.TestEmailConfig;

@SpringBootTest
@Import(TestEmailConfig.class)
class RescueBitesTests {

    @Test
    void contextLoads() {
    }

}
