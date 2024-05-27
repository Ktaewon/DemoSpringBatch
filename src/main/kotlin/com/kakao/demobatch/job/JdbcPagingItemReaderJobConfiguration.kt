package com.kakao.demobatch.job

import com.kakao.demobatch.model.Pay
import com.kakao.demobatch.util.logger
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class JdbcPagingItemReaderJobConfiguration {
    private val logger = logger()
    private val chunkSize = 10

    @Bean
    fun jdbcPagingItemReaderJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        dataSource: DataSource,
    ) = JobBuilder("jdbcPagingItemReaderJob", jobRepository)
        .start(jdbcPagingItemReaderStep(jobRepository, transactionManager, dataSource))
        .build()

    @Bean
    fun jdbcPagingItemReaderStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        dataSource: DataSource,
    ) = StepBuilder("jdbcPagingItemReaderStep", jobRepository)
        .chunk<Pay, Pay>(chunkSize, transactionManager)
        .reader(jdbcPagingItemReader(dataSource))
        .writer(jdbcPagingItemWriter())
        .build()

    @Bean
    fun jdbcPagingItemReader(dataSource: javax.sql.DataSource): JdbcPagingItemReader<Pay> {
        val parameterValues = hashMapOf<String, Any>()
        parameterValues["amount"] = 2000

        return JdbcPagingItemReaderBuilder<Pay>()
            .pageSize(chunkSize)
            .fetchSize(chunkSize)
            .dataSource(dataSource)
            .rowMapper(DataClassRowMapper(Pay::class.java))
            .queryProvider(createQueryProvider(dataSource))
            .parameterValues(parameterValues)
            .name("jdbcPagingItemReader")
            .build()
    }

    private fun jdbcPagingItemWriter(): ItemWriter<Pay?> =
        ItemWriter<Pay?> { list: Chunk<out Pay?> ->
            for (pay in list) {
                logger.info("Current Pay={}", pay)
            }
        }

    private fun createQueryProvider(dataSource: DataSource): PagingQueryProvider {
        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setDataSource(dataSource) // Database에 맞는 PagingQueryProvider를 선택하기 위해
        queryProvider.setSelectClause("id, amount, tx_name, tx_date_time")
        queryProvider.setFromClause("from pay")
        queryProvider.setWhereClause("where amount >= :amount")

        val sortKeys: MutableMap<String, Order> = HashMap<String, Order>(1)
        sortKeys["id"] = Order.ASCENDING

        queryProvider.setSortKeys(sortKeys)

        return queryProvider.getObject()
    }
}
