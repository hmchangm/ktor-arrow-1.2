package tw.idv.brandy

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import tw.idv.brandy.plugins.Book
import kotlin.test.Test

class ValidationTest {

    @Test
    fun `user valid`() {
        User.of("Brandy", null, "03242342223").let(::println)
        User.of("Brandy", "hmchangm@gmail.com", "03242342223").let(::println)
        User.of("", "hmchangma", "").let(::println)
    }

    @Test
    fun `book either`() {
        Book("aaaa", listOf("BBB")).let(::println)
        Book("", listOf()).let(::println)
        Book("aaaa", listOf()).let(::println)
        Book("aaaa", listOf("", "CCC", "", "GSDD", "")).let(::println)
    }

    fun one(): Either<String, Int> = "error-1".left()
    fun two(): Either<NonEmptyList<String>, Int> = nonEmptyListOf("error-2", "error-3").left()

    @Test
    fun example() =
        either<NonEmptyList<String>, Int> {
            zipOrAccumulate(
                { one().bind() },
                { two().bindNel() },
            ) { x, y -> x + y }
        }.also(::println).let { Unit }
}
