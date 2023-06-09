package tw.idv.brandy.plugins

import arrow.core.*
import arrow.core.raise.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import tw.idv.brandy.*
import tw.idv.brandy.repo.Mailer
import tw.idv.brandy.repo.UserRepo

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

        get("/user/sendAndMail") {
            runBlocking {
                User.of("Brandy", "aabbdd@mail.com", "031142")
                    .mapLeft { formValidToEither(it) }
                    .flatMap { UserRepo.save(it) }
                    .tap { println(it) }.onRight { }
                    .flatMap { Mailer.mail(it) }
                    .fold(
                        ifRight = { call.respond(it) },
                        ifLeft = { call.respond(HttpStatusCode.InternalServerError, it) },
                    )
            }

            runBlocking {
                either {
                    val brandy = User.of("Brandy", "aabbdd@mail.com", "0311332")
                        .mapLeft { FormValidError(it) }.bind()
                    println(brandy)
                    UserRepo.save(brandy).bind()
                    Mailer.mail(brandy).bind()
                }.fold(
                    ifRight = { with(call) { respondBase(it) } },
                    ifLeft = { with(call) { respondError(it) } },
                )
            }
        }
    }
}

fun formValidToEither(it: NonEmptyList<BizError>) = FormValidError(it)

context (ApplicationCall)
suspend fun respondError(it: BizError) {
    when (it) {
        is DatabaseError -> {
            respond(HttpStatusCode.InternalServerError, "Database problem ${it.e.message}")
        }
        is MailError -> {
            respond(HttpStatusCode.InternalServerError, "Cannot send mail for ${it.email}")
        }

        is FormValidError -> TODO()
        is NotValidField -> TODO()
    }
}

context (ApplicationCall)
suspend fun respondBase(it: Any) {
    respond(it)
}

context (Raise<Nel<BizError>>)
suspend fun arrow2Style() {
    val brandy = User.of2("Brandy", "aabbdd@mail.com", "031142253")
    println(brandy)
    save(brandy)
    mail(brandy)
}

context (Raise<Nel<BizError>>)
suspend fun save(user: User): User = TODO()
context (Raise<Nel<BizError>>)
suspend fun mail(user: User): User = TODO()
