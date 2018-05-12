package app.views.BackupsView

import app.bean.BackupFileBean
import app.components.bootstrap.spinner
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import react.*
import react.dom.div
import react.dom.h2
import kotlin.js.Promise

interface BackupsViewState : RState {
    var backups: Array<BackupFileBean>?
}


class BackupsView : RComponent<RProps, BackupsViewState>() {

    override fun componentDidMount() {
        fetchBackups()
    }

    private fun fetchBackups(): Promise<Unit> {
        return axios<Array<BackupFileBean>>(jsObject {
            url = "api/backups/list"
        }).then {
            setState { backups = it.data }
        }
    }

    private fun RBuilder.renderLoading() {
        div {
            spinner()
            +" Loading ..."
        }
    }

    private fun RBuilder.renderBackups() {
        state.backups?.forEach {
            div {
                +it.name
            }
        } ?: renderLoading()
    }

    override fun RBuilder.render() {
        h2 { +"Backups" }

        renderBackups()
    }

}


fun RBuilder.backupsView(handler: RHandler<RProps>) = child(BackupsView::class, handler)