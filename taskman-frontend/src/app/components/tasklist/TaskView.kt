package app.components.tasklist

import app.bean.TaskBean
import app.components.bootstrap.spinner
import app.components.task.task
import app.components.taskeditor.taskEditor
import app.components.tasklist.View.ACTIVE_TASKS
import app.components.tasklist.View.FINISHED_TASKS
import app.extensions.getActiveTasks
import app.extensions.getFinishedTasks
import app.extensions.getSuspendedTasks
import app.wrappers.axios.axios
import app.wrappers.moment.moment
import kotlinext.js.jsObject
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.router.dom.RouteResultHistory
import kotlin.browser.window
import kotlin.js.Promise

@JsModule("src/images/cat.png")
external val catImage: dynamic

enum class View {
    ACTIVE_TASKS, FINISHED_TASKS
}

interface TaskViewState: RState {
    var tasks: Array<TaskBean>?
    var showAddTask: Boolean
    var view: View
}

interface TaskViewProps: RProps {
    var history: RouteResultHistory
}

class TaskView: RComponent<TaskViewProps, TaskViewState>() {

    private var tasksRefreshIntervalID = 0

    override fun TaskViewState.init() {
        tasks = null
        showAddTask = false
        view = ACTIVE_TASKS
    }

    override fun componentDidMount() {
        fetchTasks()

        tasksRefreshIntervalID = window.setInterval({
            fetchTasks()
        }, 5000)
    }

    override fun componentWillUnmount() {
        window.clearInterval(tasksRefreshIntervalID)
    }

    private fun fetchTasks(): Promise<Unit> {
        return axios<Array<TaskBean>>(jsObject {
            url = "api/tasks"
        }).then {
            setState { tasks = it.data }
        }
    }

    private fun getTasks() = state.tasks ?: emptyArray()

    private fun handleAddNewTaskClick(event: Event) = setState { showAddTask = true }

    private fun RBuilder.renderNavigationBar() {
        div("pt-5 pb-1") {
            ul("nav nav-tabs") {
                navigationBarItem("Task List", ACTIVE_TASKS)
                navigationBarItem("Finished Tasks", FINISHED_TASKS)
            }
        }
    }

    private fun RBuilder.navigationBarItem(itemName: String, view: View) {
        fun activeIf(view: View) = if (state.view == view) "active" else ""

        li("nav-item") {
            a(classes = "nav-link ${activeIf(view)}", href = "#") {
                attrs.onClickFunction = { setState { this.view = view } }
                +itemName
            }
        }
    }

    private fun RBuilder.renderActiveTasks() {
        renderTasks(getTasks().getActiveTasks(), "There are no tasks. Let's add some!")
    }

    private fun RBuilder.renderFinishedTasks() {
        val finishedTasks = getTasks().getFinishedTasks()

        if (finishedTasks.isEmpty()) {
            h5 { +"There are no finishedTasks. Let's finish some!" }
        } else {
            finishedTasks.groupBy { getDaysAgoGroupName(it) }
                .toList()
                .sortedByDescending { (_, tasks) -> tasks.firstOrNull()?.completedDate }
                .forEach { (group, tasks) ->
                    h2("mt-2") { +group }
                    renderTasksIndexed(tasks)
                }
        }

    }

    private fun getDaysAgoGroupName(task: TaskBean): String {
        val now = moment().startOf("day")
        val completedDate = moment(task.completedDate).startOf("day")

        val daysAgo = now
                .diff(completedDate, "days")
                .toString()
                .toInt()

        return when(daysAgo) {
            0 -> "Today"
            1 -> "Yesterday"
            else -> completedDate.format("dddd Do").toString()
        }
    }

    private fun RBuilder.renderTasks(tasks: List<TaskBean>, messageOnEmpty: String) {
        if (tasks.isEmpty()) {
            h5 { +messageOnEmpty }
        } else {
            renderTasksIndexed(tasks)
        }
    }

    private fun RBuilder.renderTasksIndexed(tasks: List<TaskBean>) {
        tasks.forEachIndexed { index, taskBean ->
            task(
                taskBean,
                index + 1,
                onChanged = { fetchTasks() }
            )
        }
    }

    private fun RBuilder.renderSuspendedTasks() {
        val tasks = getTasks().getSuspendedTasks()

        if (!tasks.isEmpty()) {
            div("mt-3") {
                h3 { +"Suspended tasks" }
                img(src = catImage, classes = "mb-1") {
                    attrs.width = "50px"
                }
                renderTasksIndexed(tasks)
            }
        }
    }

    private fun RBuilder.renderTopButtons() {
        if (state.showAddTask) {
            h3 { +"Give that boy a task!" }
            taskEditor {
                setState { showAddTask = false }
                fetchTasks()
            }
        } else {
            div("tasklist-top-buttons") {
                renderShowFellowsButton()
                renderAddTaskButton()
            }
        }
    }

    private fun RBuilder.renderShowFellowsButton() {
        button(type = ButtonType.button, classes = "btn btn-dark") {
            attrs.onClickFunction = { props.history.push("/fellows") }
            i("fa fa-child") {}
            +" Fellows"
        }
    }

    private fun RBuilder.renderAddTaskButton() {
        button(type = ButtonType.button, classes = "btn btn-success add-new-task") {
            attrs.onClickFunction = ::handleAddNewTaskClick
            i("fa fa-plus") {}
            +" Add new task!"
        }
    }

    override fun RBuilder.render() {
        div("tasklist") {
            if (state.tasks == null) {
                h3("tasks-loading") {
                    +"Loading tasks ... "
                    spinner()
                }
            } else {
                renderTopButtons()

                renderNavigationBar()

                when (state.view) {
                    ACTIVE_TASKS -> {
                        renderActiveTasks()
                        renderSuspendedTasks()
                    }
                    FINISHED_TASKS -> renderFinishedTasks()
                }
            }
        }
    }

}

fun RBuilder.tasksView(handler: RHandler<TaskViewProps>) = child(TaskView::class, handler)
