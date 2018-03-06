package app.components.error

import react.RBuilder
import react.dom.div


fun RBuilder.error(errorMessage: String?) {
    errorMessage?.let {
        div("alert alert-danger error-message") {
            +it
        }
    }
}