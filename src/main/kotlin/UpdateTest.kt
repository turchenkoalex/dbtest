package org.example

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.sql.DataSource

/**
 * This class is used to test the load of RDS instances.
 *
 * update update_data set value = value + 1 where name = 'update_test'
 */
class UpdateTest(
    datasource: DataSource,
    interrupted: AtomicBoolean,
    latch: CountDownLatch
) : RDSTest(datasource, interrupted, latch) {

    override fun init() {
        invokeStatement(
            createTable("update_data", "name VARCHAR(255) NOT NULL PRIMARY KEY, value BIGINT NOT NULL"),
            "insert into update_data (name, value) values ('update_test', 0) on conflict do nothing"
        )
    }

    override fun test(iteration: Long) {
        invokeStatement("update update_data set value = value + 1 where name = 'update_test'")
        TimeUnit.MILLISECONDS.sleep(10)
    }

}
