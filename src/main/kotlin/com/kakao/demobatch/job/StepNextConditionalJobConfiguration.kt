package com.kakao.demobatch.job

import com.kakao.demobatch.util.logger
import org.springframework.batch.core.ExitStatus
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
class StepNextConditionalJobConfiguration {
    private val logger = logger()

    @Bean
    fun stepNextConditionalJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Job {
        return JobBuilder("stepNextConditionalJob", jobRepository)
            .start(conditionalJobStep1(jobRepository, transactionManager))
                .on("FAILED") // FAILED 일 경우
                .to(conditionalJobStep3(jobRepository, transactionManager)) // step3으로 이동한다.
                .on("*") // step3의 결과 관계 없이
                .end() // step3으로 이동하면 Flow를 종료한다.
            .from(conditionalJobStep1(jobRepository, transactionManager))
                .on("*") // FAILED 외에 모든 경우
                .to(conditionalJobStep2(jobRepository, transactionManager)) // step2로 이동한다.
                .next(conditionalJobStep3(jobRepository, transactionManager)) // step2가 정상 종료되면 step3으로 이동한다.
                .on("*") // step3의 결과 관계 없이
                .end() // step3으로 이동하면 Flow가 종료한다.
            .end() // job 종료
            .build()
    }

    @Bean
    fun conditionalJobStep1(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("conditionalJobStep1", jobRepository)
        .tasklet ({ contribution, _ ->
            logger.info(">>>>> This is stepNextConditionalJob Step1")
            /**
                ExitStatus를 FAILED로 지정한다.
                해당 status를 보고 flow가 진행된다.
             **/
            contribution.exitStatus = ExitStatus.FAILED
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun conditionalJobStep2(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("conditionalJobStep2", jobRepository)
        .tasklet({ _, _ ->
            logger.info(">>>>> This is stepNextConditionalJob Step2")
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun conditionalJobStep3(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("conditionalJobStep3", jobRepository)
        .tasklet({ _, _ ->
            logger.info(">>>>> This is stepNextConditionalJob Step3")
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()
}