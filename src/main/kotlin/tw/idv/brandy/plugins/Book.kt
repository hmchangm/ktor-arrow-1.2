package tw.idv.brandy.plugins

import arrow.core.*
import arrow.core.raise.*
import com.fasterxml.jackson.databind.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class Book private constructor(
    val title: String,
    val authors: NonEmptyList<Author>,
) {
    companion object {
        fun of(
            title: String,
            authors: Iterable<String>,
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
            ) { _, authorsNel ->
                Book(title, authorsNel)
            }
        }

        suspend fun of2(
            title: String,
            authors: Iterable<String>,
        ): Either<BookValidationError, Book> = either<BookValidationError, Book> {
            ensure(title.isNotEmpty()) { EmptyTitle }
            ensureNotNull(authors.toNonEmptyListOrNull()) { NoAuthors }
            Book(title, TODO())
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
            Book.of("TSMC", listOf("Morries", "Chang")).let {
                call.respond(it)
            }
        }
        get("/book/bad") {
            Book.of("", listOf("Morries", "", "hgjkg", "")).let {
                call.respond(it.leftOrNull().toString())
            }
        }
    }
}
