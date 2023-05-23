package tw.idv.brandy.plugins

import arrow.core.*
import arrow.core.raise.*
import io.ktor.serialization.jackson.*
import com.fasterxml.jackson.databind.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.server.routing.*


data class Book private constructor(
    val title: String, val authors: NonEmptyList<Author>
) {
    companion object {
        operator fun invoke(
            title: String, authors: Iterable<String>
        ): Either<NonEmptyList<BookValidationError>, Book> = either<NonEmptyList<BookValidationError>, Book> {
            zipOrAccumulate(
                { ensure(title.isNotEmpty()) { EmptyTitle } },
                {
                    val validatedAuthors = mapOrAccumulate(authors.withIndex()) {
                        Author(it.value)
                            .recover { _ -> raise(EmptyAuthor(it.index)) }
                    }.bindAll()
                    ensureNotNull(validatedAuthors.toNonEmptyListOrNull()) { NoAuthors }
                },
                { ensure(title.isNotEmpty()) { EmptyTitle } },
            ) { _, authorsNel, _ ->
                Book(title, authorsNel)
            }
        }
    }
}

data class Author private constructor(val name: String) {
    companion object {
        operator fun invoke(name: String): Either<EmptyTitle, Author> = either {
            ensure(name.isNotEmpty()) { EmptyTitle }
            Author(name)
        }

    }
}


fun Application.bookRoute() {

    routing {
        get("/book/good") {
            Book("TSMC", listOf("Morries", "Chang")).let {
                call.respond(it)
            }
        }
        get("/book/bad") {
            Book("", listOf("Morries", "", "hgjkg", "")).let {
                call.respond(it.leftOrNull().toString())
            }
        }

    }
}

