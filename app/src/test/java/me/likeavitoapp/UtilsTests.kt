package me.likeavitoapp

import me.likeavitoapp.model.expectOutput
import me.likeavitoapp.model.withTests
import org.junit.Test


class UtilsTests {

    @Test
    fun `check format(isRemove = false)`(){
        val delimiter = '/'
        val cursor = '|'
        val mask = "##/##"

        fun test(value: String): String {
            var result = format(
                text = value.toString(),
                mask = mask,
                cursor = cursor,
                delimiter = delimiter,
                removeOneChar = false
            )

            return result
        }

        withTests(
            realInput = "1234|",
            outputMaker = { test(it) },
            testsEnabled = true,
            withAssert = true,
            testCases = listOf(
                " " expectOutput "|",
                " ,-1234|" expectOutput "12/3|",
                ",- ." expectOutput "|",
                "" expectOutput "|",
                "|" expectOutput "|",
                "1234|" expectOutput "12/34|",
                "123|4" expectOutput "12/3|4",
                "12|34" expectOutput "12/|34",
                "1|234" expectOutput "1|2/34",
                "|1234" expectOutput "|12/34",
                "12|3" expectOutput "12/|3",
                "123|" expectOutput "12/3|",
                "|123" expectOutput "|12/3",
                "|12" expectOutput "|12",
                "12|" expectOutput "12/|",
                "1|" expectOutput "1|",
                "|1" expectOutput "|1",
            )
        )
    }

    @Test
    fun `check format(isRemove = true)`(){
        val delimiter = '/'
        val cursor = '|'
        val mask = "##/##"

        fun test(value: String): String {
            var result = format(
                text = value.toString(),
                mask = mask,
                cursor = cursor,
                delimiter = delimiter,
                removeOneChar = true
            )

            return result
        }

        withTests(
            realInput = "1234|",
            outputMaker = { test(it) },
            testsEnabled = true,
            withAssert = true,
            testCases = listOf(
                " " expectOutput "|",
                " ,-1234|" expectOutput "12/3|",
                ",- ." expectOutput "|",
                "" expectOutput "|",
                "|" expectOutput "|",
                "1234|" expectOutput "12/3|",
                "123|4" expectOutput "12/|4",
                "12|34" expectOutput "1|3/4",
                "1|234" expectOutput "|23/4",
                "|1234" expectOutput "|12/34",
                "12|3" expectOutput "1|3",
                "123|" expectOutput "12/|",
                "|123" expectOutput "|12/3",
                "|12" expectOutput "|12",
                "12|" expectOutput "1|",
                "1|" expectOutput "|",
                "|1" expectOutput "|1",
            )
        )
    }
}