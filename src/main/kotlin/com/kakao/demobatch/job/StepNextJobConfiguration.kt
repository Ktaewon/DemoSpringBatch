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
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class StepNextJobConfiguration {
    private val logger = logger()

    @Bean
    fun stepNextJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Job {
        return JobBuilder("stepNextJob", jobRepository)
            .start(step1(jobRepository, transactionManager))
            .next(step2(jobRepository, transactionManager))
            .next(step3(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun step1(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("step1", jobRepository)
        .tasklet ({ _, _ ->
            logger.info(">>>>> This is Step1")
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun step2(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("step2", jobRepository)
        .tasklet({ _, _ ->
            logger.info(">>>>> This is Step2")
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun step3(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("step3", jobRepository)
        .tasklet({ _, _ ->
            logger.info(">>>>> This is Step3")
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()
}