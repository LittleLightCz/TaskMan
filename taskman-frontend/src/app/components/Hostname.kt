package app.components

import app.wrappers.axios.axios
import kotlinext.js.jsObject
import react.*
import react.dom.i
import react.dom.small
import react.dom.span

interface HostnameState : RState {
    var hostname: String?
}

class Hostname : RComponent<RProps, HostnameState>() {

    override fun componentDidMount() {
        loadHostname()
    }

    private fun loadHostname() {
        axios<String>(jsObject {
            url = "api/hostname"
        }).then {
            setState {
                hostname = it.data
            }
        }.catch { err ->
            setState { hostname = "?unknown?" }
        }
    }

    override fun RBuilder.render() {
        val hostname = state.hostname

        span {
            if (hostname == null) {
                small {
                    +" "
                    i("fa fa-spinner fa-spin") {}
                }
            } else {
                small { +" on " }
                +hostname
            }
        }
    }

}

fun RBuilder.hostname() = child(Hostname::class) {}
