package tw.idv.brandy

import arrow.core.NonEmptyList

sealed interface BizError

data class NotValidField(val field: String, val value: String?) : BizError
data class FormValidError(val nelError: NonEmptyList<BizError>) : BizError

data class MailError(val email: Email) : BizError
data class DatabaseError(val e: Throwable) : BizError
