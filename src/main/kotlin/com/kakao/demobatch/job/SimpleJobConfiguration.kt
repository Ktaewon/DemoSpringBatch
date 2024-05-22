package com.kakao.demobatch.job

import com.kakao.demobatch.util.logger
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
            .start(simpleStep1(jobRepository, transactionManager, null))
            .next(simpleStep2(jobRepository, transactionManager, null))
            .build()
    }

    @Bean
    @JobScope
    fun simpleStep1(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        @Value("#{jobParameters[requestDate]}") requestDate: String?,
    ): Step = StepBuilder("simpleStep1", jobRepository)
        .tasklet ({ _, _ ->
            logger.info(">>>>> This is Step1")
            logger.info(">>>>> requestDate = {}", requestDate)
            return@tasklet RepeatStatus.FINISHED
//            throw IllegalArgumentException("step1에서 실패합니다.")
        }, transactionManager)
        .build()

    @Bean
    @JobScope
    fun simpleStep2(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        @Value("#{jobParameters[requestDate]}") requestDate: String?,
    ): Step = StepBuilder("simpleStep2", jobRepository)
        .tasklet({ _, _ ->
            logger.info(">>>>> This is Step2")
            logger.info(">>>>> requestDate = {}", requestDate)
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()
}