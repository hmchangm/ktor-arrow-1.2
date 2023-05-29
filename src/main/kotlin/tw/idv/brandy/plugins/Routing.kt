package tw.idv.brandy.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import tw.idv.brandy.optics.Person
import tw.idv.brandy.optics.Person.Companion
import tw.idv.brandy.optics.address
import tw.idv.brandy.optics.city
import tw.idv.brandy.optics.country

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    fun Person.capitalizeCountryModify(): Person =
        Person.address.city.country.modify(this) { it.capitalize() }

}
