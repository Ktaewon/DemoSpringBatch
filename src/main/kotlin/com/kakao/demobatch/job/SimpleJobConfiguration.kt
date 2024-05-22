package com.kakao.demobatch.job

import com.kakao.demobatch.util.logger
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class SimpleJobConfiguration {

    private val logger = logger()

    @Bean
    fun simpleJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Job {
        return JobBuilder("simpleJob", jobRepository)
            .start(simpleStep1(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun simpleStep1(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("simpleStep1", jobRepository)
        .tasklet ({ _, _ ->
            logger.info(">>>>> This is Step1")
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()
}