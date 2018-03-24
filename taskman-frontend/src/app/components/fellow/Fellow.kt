package app.components.fellow

import app.bean.FellowBean
import react.*
import react.dom.div
import react.dom.h5

interface FellowProps : RProps {
    var fellow: FellowBean
}

interface FellowState : RState {

}

class Fellow : RComponent<FellowProps, FellowState>() {

    override fun RBuilder.render() {
        div("fellow border border-black rounded m-2") {
            h5("bg-black text-white m-0 p-1") { +props.fellow.url }

            div {
                +"Content ..."
            }
        }
    }

}

fun RBuilder.fellow(handler: RHandler<FellowProps>) = child(Fellow::class, handler)