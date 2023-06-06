package tw.idv.brandy

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right

@JvmInline
value class PositiveInt(val int: Int)
data class MyError(val message: String)

fun isPositive(i: Int): Either<MyError, Int> = either {
    ensure(i > 0) { MyError("$i is not positive") }
    i
}

// this is the type we want to construct
@JvmInline value class Age(val age: Int)

// these are the potential problems
sealed interface AgeProblem {
    object InvalidAge : AgeProblem
    object NotLegalAdult : AgeProblem
}

// validation returns either problems or the constructed value
fun validAdult(age: Int): Either<AgeProblem, Age> = when {
    age < 0 -> AgeProblem.InvalidAge.left()
    age < 18 -> AgeProblem.NotLegalAdult.left()
    else -> Age(age).right()
}
