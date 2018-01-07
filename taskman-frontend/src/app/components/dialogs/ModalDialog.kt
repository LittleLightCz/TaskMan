package app.components.dialogs

import react.RBuilder
import react.dom.div
import react.dom.h5
import react.dom.p


fun RBuilder.modalDialog(header: String, text: String, renderButtons: RBuilder.() -> Unit) {
    div("modal") {
        div("modal-dialog") {
            div("modal-content") {
                div("modal-header") {
                    h5 { +header }
                }
                div("modal-body") {
                    p { +text }
                }
                div("modal-footer") {
                    renderButtons()
                }
            }
        }
    }
}