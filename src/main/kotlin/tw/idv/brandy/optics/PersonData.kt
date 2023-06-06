package tw.idv.brandy.optics

import arrow.optics.*

@optics data class Person(val name: String, val address: Address) {
    companion object
}

@optics data class Address(val street: Street, val city: City) {
    companion object
}

@optics data class Street(val name: String, val number: Int?) {
    companion object
}

@optics data class City(val name: String, val country: String) {
    companion object
}

fun Person.toMiaoLiCountry(): Person =
    this.copy(
        address = address.copy(
            city = address.city.copy(
                country = "MiaoLi",
            ),
        ),
    )

fun Person.capitalizeCountryModify(): Person =
    Person.address.city.country.modify(this) {
        it.replaceFirstChar(Char::uppercase)
    }
fun Person.capitalizeCountryCopy(): Person =
    this.copy {
        Person.address.city.country transform {
            it.replaceFirstChar(Char::uppercase)
        }
    }

fun Person.rename(name: String) = this.copy(name = name)

fun Person.toMiaoLiCountryModify(): Person =
    Person.address.city.country.modify(this) { "MiaoLi" }

fun Person.toMiaoLiCountryCopy(): Person =
    this.copy { Person.address.city.country set "MiaoLi" }

fun Person.moveToAmsterdamCopy(): Person = copy {
    Person.address.city.name set "Amsterdam"
    Person.address.city.country set "Netherlands"
}

fun example() {
    val me = Person(
        "Alejandro",
        Address(
            Street("Kotlinstraat", 1),
            City("Hilversum", "Netherlands"),
        ),
    )

    val meAtTheCapital = Person.address.city.name.set(me, "Amsterdam")
}
