package org.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import sun.misc.Signal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Logger

private val log = Logger.getGlobal()

fun main() {

    log.info {
        "For connection to the database, the following environment variables are used: " +
        "PG_HOST, PG_PORT, PG_DATABASE, PG_USER, PG_PASSWORD"
    }

    log.info("Starting tests")

    val datasource = createDataSource()
    val interrupted = AtomicBoolean(false)
    val latch = CountDownLatch(2)

    val executor = Executors.newFixedThreadPool(2)

    Signal.handle(Signal("INT")) {
        log.info("Interrupting tests")

        interrupted.set(true)
        latch.await()

        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)

        datasource.close()
    }

    executor.execute(InsertTest(datasource, interrupted, latch))
    executor.execute(UpdateTest(datasource, interrupted, latch))
}

private fun createDataSource(): HikariDataSource {
    val host = System.getenv("PG_HOST") ?: "localhost"
    val port = System.getenv("PG_PORT") ?: "5432"
    val database = System.getenv("PG_DATABASE") ?: "test"

    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://$host:$port/$database"
        username = System.getenv("PG_USER") ?: "postgres"
        password = System.getenv("PG_PASSWORD") ?: ""
        maximumPoolSize = 10
        isAutoCommit = false
        connectionTimeout = TimeUnit.SECONDS.toMillis(2)
        validationTimeout = TimeUnit.SECONDS.toMillis(1)
        transactionIsolation = "TRANSACTION_READ_COMMITTED"
    }
    return HikariDataSource(config)
}
