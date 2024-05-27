package com.kakao.demobatch.job

import com.kakao.demobatch.model.Pay
import com.kakao.demobatch.util.logger
import org.springframework.batch.core.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class JdbcCursorItemReaderJobConfiguration(
    private val dataSource: javax.sql.DataSource,
) {
    private val logger = logger()
    private val chunkSize = 10

    @Bean
    fun jdbcCursorItemReaderJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Job =
        JobBuilder("jdbcCursorItemReaderJob", jobRepository)
            .start(jdbcCursorItemReaderStep(jobRepository, transactionManager))
            .build()

    @Bean
    fun jdbcCursorItemReaderStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ) = StepBuilder("jdbcCursorItemReaderStep", jobRepository)
        .chunk<Pay, Pay>(chunkSize, transactionManager)
        .reader(jdbcCursorItemReader())
        .writer(jdbcCursorItemWriter())
        .build()

    @Bean
    fun jdbcCursorItemReader(): JdbcCursorItemReader<Pay> =
        JdbcCursorItemReaderBuilder<Pay>()
            .fetchSize(chunkSize)
            .dataSource(dataSource)
            .rowMapper(DataClassRowMapper(Pay::class.java))
            .sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
            .name("jdbcCursorItemReader")
            .build()

    private fun jdbcCursorItemWriter(): ItemWriter<Pay?> =
        ItemWriter<Pay?> { list: Chunk<out Pay?> ->
            for (pay in list) {
                logger.info("Current Pay={}", pay)
            }
        }
}
