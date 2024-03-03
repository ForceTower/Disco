package dev.forcetower.unes.reactor.domain.dto

data class BaseResponse(val ok: Boolean, val data: Any?, val message: String? = null, val error: String?) {
    companion object {
        fun ok(data: Any, message: String? = null): BaseResponse {
            return BaseResponse(
                true,
                data,
                message,
                null
            )
        }

        fun okge(message: String? = null): BaseResponse {
            return BaseResponse(
                true,
                null,
                message,
                null
            )
        }
    }
}
