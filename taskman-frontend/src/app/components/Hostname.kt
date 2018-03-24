package app.components

import app.components.bootstrap.spinner
import app.wrappers.axios.axios
import app.wrappers.clipboard.copyToClipboard
import app.wrappers.toastify.Toastify
import kotlinext.js.jsObject
import kotlinx.html.title
import react.*
import react.dom.small
import react.dom.span
import kotlin.browser.window

interface HostnameState : RState {
    var hostname: String?
    var showUrlCopiedToClipboard: Boolean
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

    private fun getUrl() = "http://${state.hostname}:7900"

    private fun taskManUrlCopiedToClipboard() {
        setState { showUrlCopiedToClipboard = true }
        window.setTimeout({
            setState { showUrlCopiedToClipboard = false }
        }, 3000)
    }

    override fun RBuilder.render() {
        val hostname = state.hostname

        span {
            if (hostname == null) {
                small {
                    +" "
                    spinner()
                }
            } else {
                small { +" on " }

                copyToClipboard {
                    attrs {
                        text = getUrl()
                        onCopy = ::taskManUrlCopiedToClipboard
                    }
                    span("clickable") {
                        attrs.title = "Click to copy the URL to clipboard"
                        +hostname
                    }
                }

                if (state.showUrlCopiedToClipboard) {
                    Toastify(jsObject {
                        text = "Url ${getUrl()} copied to clipboard!"
                        duration = 3000
                        backgroundColor = "#28a745"
                    }).showToast()
                }
            }
        }
    }

}

fun RBuilder.hostname() = child(Hostname::class) {}
