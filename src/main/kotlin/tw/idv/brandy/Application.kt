package tw.idv.brandy

import io.ktor.server.application.*
import tw.idv.brandy.plugins.bookRoute
import tw.idv.brandy.plugins.configureMonitoring
import tw.idv.brandy.plugins.configureRouting
import tw.idv.brandy.plugins.configureSerialization

fun main() {
//    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureRouting()
    bookRoute()
}
