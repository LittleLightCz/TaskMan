package app.components.poos

import app.components.task.pooImage
import kotlinx.html.title
import react.RBuilder
import react.dom.div
import react.dom.img

fun RBuilder.poos(poosCount: Int) {
    if (poosCount > 0) {
        div("poos") {
            repeat(poosCount) {
                img(src = pooImage) {
                    val exclamationMarks = "!".repeat(poosCount)
                    attrs.title = "Ouch, that stinks$exclamationMarks"
                }
            }
        }
    }

}