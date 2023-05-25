package io.micronaut.crac

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.crac.Context
import org.crac.Resource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
@Property(name = "spec.name", value = OrderedResourceCheckpointSimulatorTest.SPEC_NAME)
class OrderedResourceCheckpointSimulatorTest {
    companion object {
        const val SPEC_NAME = "OrderedResourceCheckpointSimulatorTest"
    }

    //tag::test[]
    @field:Inject
    lateinit var ctx: BeanContext

    @Test
    fun testCustomOrderedResourceUsingCheckpointSimulator() {
        val myBean = ctx.getBean(ResourceBean::class.java)
        val checkpointSimulator = ctx.getBean(CheckpointSimulator::class.java) // <1>
        Assertions.assertTrue(myBean.isRunning)

        checkpointSimulator.runBeforeCheckpoint() // <2>
        Assertions.assertFalse(myBean.isRunning)

        checkpointSimulator.runAfterRestore() // <3>
        Assertions.assertTrue(myBean.isRunning)
    }
    //end::test[]

    @Requires(property = "spec.name", value = SPEC_NAME)
    //tag::bean[]
    @Singleton
    internal class ResourceBean {
        var isRunning = true // <1>
            private set

        fun stop() {
            isRunning = false
        }

        fun start() {
            isRunning = true
        }
    }
    //end::bean[]

    @Requires(property = "spec.name", value = SPEC_NAME)
    //tag::resource[]
    @Singleton
    internal class ResourceBeanResource(private val resourceBean: ResourceBean) : OrderedResource { // <1>

        @Throws(Exception::class)
        override fun beforeCheckpoint(context: Context<out Resource?>?) { // <2>
            resourceBean.stop()
        }

        @Throws(Exception::class)
        override fun afterRestore(context: Context<out Resource?>?) { // <3>
            resourceBean.start()
        }
    }
    //end::resource[]
}
