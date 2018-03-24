package app.components.error

import react.RBuilder
import react.dom.div


fun RBuilder.error(error: Any?) {
    error?.let {
        div("alert alert-danger error-message") {
            +getErrorMessage(error)
        }
    }
}

private fun getErrorMessage(error: Any) = when (error) {
    is String -> error
    else -> {
        console.log("Received unknown error object (${jsTypeOf(error)}):")
        console.log(error)
        error.toString()
    }
}
