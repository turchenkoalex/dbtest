package org.example

import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Logger
import javax.sql.DataSource

abstract class RDSTest(
    private val datasource: DataSource,
    private val interrupted: AtomicBoolean,
    private val latch: CountDownLatch,
) : Runnable {
    protected val log: Logger = Logger.getLogger(this::class.qualifiedName)

    open fun init() {}

    abstract fun test(iteration: Long)

    open fun finish() {}

    override fun run() {
        log.info { "Running test ${this::class.simpleName}" }
        var iteration = 0L

        try {
            init()

            while (!interrupted.get()) {
                try {
                    test(iteration++)
                } catch (e: SQLException) {
                    log.severe { "SQL error: ${e.message}" }
                } catch (e: Exception) {
                    log.severe { "Error: ${e.message}" }
                }

                if (iteration % 100 == 0L) {
                    log.info { "${this::class.simpleName} iteration: $iteration" }
                }
            }

            finish()
        } finally {
            log.info { "Test ${this::class.simpleName} is finished. Last iteration: $iteration" }
            latch.countDown()
        }
    }

    protected fun invokeStatement(vararg sqlList: String) {
        if (sqlList.isEmpty()) {
            return
        }

        transaction { connection ->
            sqlList.forEach { sql ->
                connection.createStatement().use { statement ->
                    @Suppress("SqlSourceToSinkFlow")
                    statement.execute(sql)
                }
            }
        }
    }

    protected fun transaction(block: (connection: Connection) -> Unit) {
        datasource.connection.use { conn ->
            block(conn)
            conn.commit()
        }
    }

}

fun createTable(name: String, fields: String): String {
    return """
do
$$
	begin
		if not exists (select 1 from information_schema.tables
				where table_schema = 'public' and table_name = '$name')
		then
			create table if not exists
				$name($fields);
		end if;
	end;
$$
    """.trimIndent()
}
