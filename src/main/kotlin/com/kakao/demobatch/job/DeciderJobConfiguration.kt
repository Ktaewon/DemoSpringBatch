package com.kakao.demobatch.job

import com.kakao.demobatch.util.logger
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.lang.System.currentTimeMillis
import kotlin.random.Random

@Configuration
class DeciderJobConfiguration {
    private val logger = logger()

    @Bean
    fun deciderJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Job {
        return JobBuilder("deciderJob", jobRepository)
            .start(startStep(jobRepository, transactionManager))
            .next(decider()) // 홀수 | 짝수 구분
            .from(decider()) // decider의 상태가
                .on("ODD") // ODD라면
                .to(oddStep(jobRepository, transactionManager)) // oddStep로 간다.
            .from(decider()) // decider의 상태가
                .on("EVEN") // ODD라면
                .to(evenStep(jobRepository, transactionManager)) // evenStep로 간다.
            .end() // builder 종료
            .build()
    }

    @Bean
    fun startStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("startStep", jobRepository)
        .tasklet({ _, _ ->
            logger.info(">>>>> Start!")
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun evenStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("evenStep", jobRepository)
        .tasklet({ _, _ ->
            logger.info(">>>>> 짝수입니다.")
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun oddStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step = StepBuilder("oddStep", jobRepository)
        .tasklet({ _, _ ->
            logger.info(">>>>> 홀수입니다.")
            return@tasklet RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun decider() = OddDecider()

    class OddDecider: JobExecutionDecider {
        private val logger = logger()
        override fun decide(jobExecution: JobExecution, stepExecution: StepExecution?): FlowExecutionStatus {
            val rand: Random = Random(currentTimeMillis())

            val randomNumber: Int = rand.nextInt(50) + 1
            logger.info("랜덤숫자: {}", randomNumber)

            return if (randomNumber % 2 == 0) {
                FlowExecutionStatus("EVEN")
            } else {
                FlowExecutionStatus("ODD")
            }
        }
    }
}