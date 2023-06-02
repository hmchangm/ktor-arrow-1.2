package tw.idv.brandy

import arrow.core.Either
import arrow.core.Nel
import arrow.core.raise.*
import arrow.core.toOption
import arrow.optics.optics

@optics data class User(
    val username: String,
    val email: Email,
    val phoneNumber: PhoneNumber,
) {
    companion object {
        fun of(
            username: String,
            email: String? = null,
            phoneNumber: String,
        ): Either<Nel<BizError>, User> = either {
            zipOrAccumulate(
                { ensureNotNull(username) { NotValidField("username", username) } },
                { Email.of2(email).bind() },
                { PhoneNumber(phoneNumber).bind() },
            ) { _, validEmail, validPhone ->
                User(username, validEmail, validPhone)
            }
        }
    }
}

val EMAIL_REGEX = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".toRegex()

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        fun of(value: String?): Either<BizError, Email> = either {
            value.orEmpty().also {
                ensure(EMAIL_REGEX.matches(it)) { NotValidField("Email", value) }
            }.let { Email(it) }
        }

        fun of2(value: String?): Either<BizError, Email> = either {
            value.toOption().fold(
                ifEmpty = { raise(NotValidField("Email", value)) },
                ifSome = {
                    ensure(EMAIL_REGEX.matches(it)) { NotValidField("Email", it) }
                    Email(it)
                },
            )
        }
    }
}

val PHONENUMBER_REGEX = """^(\+\d{2})?\s?(\d\s?)+$""".toRegex()

@JvmInline
value class PhoneNumber private constructor(val value: String) {

    companion object {
        operator fun invoke(value: String): Either<BizError, PhoneNumber> = either {
            ensure(PHONENUMBER_REGEX.matches(value)) { NotValidField("Phone Number", value) }
            PhoneNumber(value)
        }
    }
}
