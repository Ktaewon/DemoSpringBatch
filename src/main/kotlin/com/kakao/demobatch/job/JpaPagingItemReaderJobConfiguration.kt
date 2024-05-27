package com.kakao.demobatch.job

import com.kakao.demobatch.model.Pay
import com.kakao.demobatch.util.logger
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class JpaPagingItemReaderJobConfiguration {
    private val logger = logger()
    private val chunkSize = 10

    @Bean
    fun jpaPagingItemReaderJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        entityManagerFactory: EntityManagerFactory,
    ) = JobBuilder("jpaPagingItemReaderJob", jobRepository)
        .start(jpaPagingItemReaderStep(jobRepository, transactionManager, entityManagerFactory))
        .build()

    @Bean
    fun jpaPagingItemReaderStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        entityManagerFactory: EntityManagerFactory,
    ) = StepBuilder("jpaPagingItemReaderStep", jobRepository)
        .chunk<Pay, Pay>(chunkSize, transactionManager)
        .reader(jpaPagingItemReader(entityManagerFactory))
        .writer(jpaPagingItemWriter())
        .build()

    @Bean
    fun jpaPagingItemReader(entityManagerFactory: EntityManagerFactory) =
        JpaPagingItemReaderBuilder<Pay>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("SELECT p FROM Pay p WHERE amount >= 2000")
            .build()

    private fun jpaPagingItemWriter(): ItemWriter<Pay?> {
        return ItemWriter<Pay?> { list: Chunk<out Pay?> ->
            for (pay in list) {
                logger.info("Current Pay={}", pay)
            }
        }
    }
}
