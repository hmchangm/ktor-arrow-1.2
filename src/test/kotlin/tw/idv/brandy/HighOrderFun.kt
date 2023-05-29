package tw.idv.brandy

import arrow.core.merge
import arrow.core.partially1
import arrow.fx.coroutines.parZip
import arrow.fx.coroutines.raceN
import kotlin.test.Test

typealias UserId = String
data class User(val name: String, val avatar: String)
class HighOrderFun {

    @Test
    fun `collection example 1`() {
        val toBeHonest: (Int) -> Int = { it * 10 }

        listOf(1, 2, 3, 4, 5)
            .map { it * 2 }.map(toBeHonest)
            .filter { it > 77 }
            .let(::println)
    }

    @Test
    fun `bmi curry 1`() {
        val bmi: (Double) -> (Double) -> Double = { h -> { w -> h / w } }
        val bmi183 = bmi(183.0)
        listOf(70.0, 82.0, 73.0, 77.0, 69.0)
            .map(bmi183).also(::println)
    }

    @Test
    fun `bmi curry 2`() {
        fun bmi(h: Double, w: Double) = h / w
        val bmi183part = ::bmi.partially1(183.0)
        listOf(70.0, 82.0, 73.0, 77.0, 69.0)
            .map(bmi183part).also(::println)
    }

    fun getUserName(x: UserId): String = TODO()
    fun getAvatar(x: UserId): String = TODO()

    // @Test
    suspend fun `test get user`() {
        getUser("brandy")
    }

    suspend fun getUser(id: UserId): User =
        parZip(
            { getUserName(id) },
            { getAvatar(id) },
        ) { name, avatar -> User(name, avatar) }

    fun downloadFrom(x: String): String = TODO()
    suspend fun file(server1: String, server2: String) =
        raceN(
            { downloadFrom(server1) },
            { downloadFrom(server2) },
        ).merge()
}
