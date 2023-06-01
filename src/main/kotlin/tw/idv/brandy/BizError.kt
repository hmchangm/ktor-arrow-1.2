package tw.idv.brandy

sealed interface BizError
data class NotValidField(val field: String, val value: String?) : BizError
