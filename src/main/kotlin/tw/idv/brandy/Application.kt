package tw.idv.brandy

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import tw.idv.brandy.plugins.*

fun main() {
    Book("aaaa", listOf("BBB")).let(::println)
    Book("", listOf()).let(::println)
    Book("aaaa", listOf()).let(::println)
    Book("aaaa", listOf("","CCC","","GSDD","")).let(::println)
//    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureRouting()
    bookRoute()
}
