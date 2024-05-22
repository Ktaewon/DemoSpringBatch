package com.kakao.demobatch

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoBatchApplication

fun main(args: Array<String>) {
    runApplication<DemoBatchApplication>(*args)
}
