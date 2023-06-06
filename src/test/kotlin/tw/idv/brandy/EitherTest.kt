package tw.idv.brandy

import arrow.core.left
import arrow.core.right
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EitherTest {

    @Test
    fun example() {
        isPositive(-1) shouldBe MyError("-1 is not positive").left()
        isPositive(1) shouldBe 1.right()
    }

    @Test
    fun `smart constructor`() {
        PhoneNumber("xxx").shouldBeLeft()
        PhoneNumber("34324233").shouldBeRight()
    }
}
