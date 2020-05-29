package org.jetbrains.plugins.feature.suggester.suggesters

import com.intellij.testFramework.fixtures.IdeaTestExecutionPolicy
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import junit.framework.TestCase
import org.jetbrains.plugins.feature.suggester.FeatureSuggestersManagerListener
import org.jetbrains.plugins.feature.suggester.PopupSuggestion
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class FeatureSuggesterTest : LightJavaCodeInsightFixtureTestCase() {

    class TestSuggestersExecutionPolicy : IdeaTestExecutionPolicy() {
        override fun getName(): String = "TestSuggestersExecutionPolicy"

        override fun runInDispatchThread(): Boolean = false
    }

    init {
        System.setProperty("idea.test.execution.policy", TestSuggestersExecutionPolicy::class.java.name)
    }

    override fun getTestDataPath(): String {
        return "src/test/resources/testData"
    }

    fun testFeatureFound(doTestActions: FeatureSuggesterTest.() -> Unit, assert: (PopupSuggestion) -> Boolean) {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val testPassed = AtomicBoolean(false)
        project.messageBus.connect()
            .subscribe(FeatureSuggestersManagerListener.TOPIC, object : FeatureSuggestersManagerListener {
                override fun featureFound(suggestion: PopupSuggestion) {
                    lock.withLock {
                        val result = assert(suggestion)
                        testPassed.set(result)
                        condition.signal()
                    }
                }
            })

        doTestActions()
        lock.withLock {
            condition.await(10, TimeUnit.SECONDS)
        }
        if (!testPassed.get()) {
            TestCase.fail()
        }
    }

}