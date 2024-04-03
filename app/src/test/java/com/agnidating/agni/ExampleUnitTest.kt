package com.agnidating.agni

import com.agnidating.agni.utils.isValidPersonName
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun validPersonName() {
        assertEquals(true, "Ajay Asija".trim().isValidPersonName())
    }
}