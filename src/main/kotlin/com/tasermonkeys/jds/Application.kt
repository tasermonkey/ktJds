package com.tasermonkeys.jds

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.elasticsearch.client.Client
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@EnableAutoConfiguration
@Configuration
@SpringBootApplication
open class Application {
    @Bean
    open fun elasticsearchClient(): Client? {
        return null;
    }

    @Bean
    open fun objectMapperBuilder(): Jackson2ObjectMapperBuilder
            = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule())
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
