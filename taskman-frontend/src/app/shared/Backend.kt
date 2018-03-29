package app.shared

import app.wrappers.axios.axios
import kotlinext.js.jsObject


object Backend {

    val fetchHostname by lazy {
        axios<String>(jsObject {
            url = "api/hostname"
        }).then {
            it.data
        }.catch {
            "?unknown?"
        }
    }

}