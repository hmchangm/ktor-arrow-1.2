package tw.idv.brandy

import arrow.core.Either
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.raise.*
import arrow.optics.optics

@optics
data class User(
    val username: String,
    val email: Email,
    val phoneNumber: PhoneNumber,
) {
    companion object {
        suspend fun of(username: String?, email: String, phoneNumber: String): Either<NonEmptyList<BizError>, User> =
            either {
                zipOrAccumulate(
                    { ensureNotNull(username) { NotValidField("username", username) } },
                    { Email(email).bind() },
                    { PhoneNumber(phoneNumber).bind() },
                ) { name, validEmail, validPhone ->
                    User(name, validEmail, validPhone)
                }
            }

        fun of1(username: String?, email: String, phoneNumber: String): Either<BizError, User> =
            either {
                ensureNotNull(username) { NotValidField("username", username) }
                val mail = Email(email).bind()
                val phone = PhoneNumber(phoneNumber).bind()
                User(username, mail, phone)
            }

        context(Raise<Nel<BizError>>)
        fun of2(username: String, email: String? = null, phoneNumber: String): User =
            zipOrAccumulate(
                { ensureNotNull(username) { NotValidField("username", username) } },
                { Email(email).bind() },
                { PhoneNumber(phoneNumber).bind() },
            ) { _, validEmail, validPhone ->
                User(username, validEmail, validPhone)
            }
    }
}

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        private val EMAIL_REGEX =
            "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".toRegex()

        operator fun invoke(value: String?): Either<BizError, Email> = either {
            value.orEmpty().also {
                ensure(EMAIL_REGEX.matches(it)) { NotValidField("Email", value) }
            }.let { Email(it) }
        }
    }
}

@JvmInline
value class PhoneNumber private constructor(val value: String) {
    companion object {
        private val PHONE_NUMBER_REGEX = """^(\+\d{2})?\s?(\d\s?)+$""".toRegex()
        operator fun invoke(value: String): Either<BizError, PhoneNumber> = either {
            ensure(PHONE_NUMBER_REGEX.matches(value)) { NotValidField("Phone Number", value) }
            PhoneNumber(value)
        }
    }
}
