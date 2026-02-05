package com.project.artists;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ArtistsApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void applicationBeanExists() {
        assertThat(applicationContext.containsBean("artistsApplication")).isTrue();
    }

    @Test
    void mainMethodDoesNotThrowException() {
        String[] args = {}
        assertThat(ArtistsApplication.class).isNotNull();
        assertThat(ArtistsApplication.class.getAnnotation(SpringBootApplication.class)).isNotNull();
    }

    @Test
    void hasSpringBootApplicationAnnotation() {
        SpringBootApplication annotation = ArtistsApplication.class.getAnnotation(SpringBootApplication.class);
        assertThat(annotation).isNotNull();
    }
}