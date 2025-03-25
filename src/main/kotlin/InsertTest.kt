package org.example

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.sql.DataSource

/**
 * This class is used to test the load of RDS instances.
 *
 * insert into insert_data (name, value) values ('insert_test_$NUM', '$insert_test_$NUM')
 * delete from insert_data where name = 'insert_test_$NUM'
 *
 * In one transaction
 */
class InsertTest(
    datasource: DataSource,
    interrupted: AtomicBoolean,
    latch: CountDownLatch
) : RDSTest(datasource, interrupted, latch) {

    override fun init() {
        invokeStatement(
            createTable("insert_data", "name VARCHAR(255) NOT NULL PRIMARY KEY, value VARCHAR(255) NOT NULL")
        )
    }

    override fun test(iteration: Long) {
        val name = "insert_test_$iteration"
        invokeStatement(
            "insert into insert_data (name, value) values ('$name', '$name')",
            "delete from insert_data where name = '$name'",
        )
        TimeUnit.MILLISECONDS.sleep(10)
    }
}
