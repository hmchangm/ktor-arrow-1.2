package tw.idv.brandy.repo

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import tw.idv.brandy.DatabaseError
import tw.idv.brandy.MailError
import tw.idv.brandy.User

object UserRepo {

    val save: suspend (User) -> Either<DatabaseError, User> = { u ->
        either {
            catch({ u }) {
                raise(DatabaseError(it))
            }
        }
    }

    val saveOld: suspend (User) -> Either<DatabaseError, User> = { user ->
        Either.catch {
            user
        }.mapLeft {
            DatabaseError(it)
        }
    }
}

object Mailer {

    suspend fun mail(u: User): Either<MailError, User> = either {
        catch({ u }) {
            raise(MailError(u.email))
        }
    }
}
