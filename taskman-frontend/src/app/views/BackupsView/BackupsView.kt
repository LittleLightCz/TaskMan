package app.views.BackupsView

import app.bean.BackupFileBean
import app.components.bootstrap.spinner
import app.wrappers.axios.axios
import app.wrappers.moment.moment
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*
import react.router.dom.RouteResultHistory
import kotlin.js.Promise

interface BackupsViewProps : RProps {
    var history: RouteResultHistory
}

interface BackupsViewState : RState {
    var backups: Array<BackupFileBean>?
}


class BackupsView : RComponent<BackupsViewProps, BackupsViewState>() {

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
        state.backups?.run {
            ul("list-group backup-list-group") {
                sortedByDescending { it.lastModified }
                    .forEach { renderBackupItem(it) }
            }
        } ?: renderLoading()
    }

    private fun RBuilder.renderBackupItem(backup: BackupFileBean) {
        li("list-group-item backup-list-item") {
            strong("backup-name text-left") { +backup.name }
            span { +moment(backup.lastModified).format("MMMM Do hh:mm").toString() }
            renderRestoreButton()
        }
    }

    private fun RBuilder.renderRestoreButton() {
        div("btn btn-success ml-2") {
            attrs.onClickFunction = { props.history.push("/") }
            i("fa fa-undo") {}
            +" Restore"
        }
    }

    private fun RBuilder.renderBackButton() {
        div("btn btn-dark m-2") {
            attrs.onClickFunction = { props.history.push("/") }
            i("fa fa-reply") {}
            +" Back"
        }
    }

    override fun RBuilder.render() {
        div("m-1") {
            h2 { +"Backups" }

            renderBackups()

            renderBackButton()
        }
    }

}


fun RBuilder.backupsView(handler: RHandler<BackupsViewProps>) = child(BackupsView::class, handler)