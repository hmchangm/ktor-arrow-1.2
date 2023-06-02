package tw.idv.brandy.plugins

sealed interface BookValidationError
object EmptyTitle : BookValidationError
object NoAuthors : BookValidationError
data class EmptyAuthor(val index: Int) : BookValidationError
