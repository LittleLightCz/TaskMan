package app.components.tasklist

import app.bean.TaskBean
import app.bean.isCompleted
import app.bean.isNotCompleted
import app.components.task.task
import app.components.tasklist.View.FINISHED_TASKS
import app.components.tasklist.View.ACTIVE_TASKS
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import kotlin.browser.window
import kotlin.js.Promise

enum class View {
    ACTIVE_TASKS, FINISHED_TASKS
}

interface TaskListState: RState {
    var tasks: Array<TaskBean>?
    var showAddTask: Boolean
    var view: View
}

class TaskList: RComponent<RProps, TaskListState>() {

    private var tasksRefreshIntervalID = 0

    override fun TaskListState.init() {
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

    private fun getActiveTasks() = getTasks().filter { !it.deleted && it.isNotCompleted() }
            .sortedWith(
                    compareBy({ it.priority }, { it.createdDate })
            )

    private fun getFinishedTasks() = getTasks().filter { it.deleted || it.isCompleted() }
            .sortedByDescending { it.completedDate }

    private fun handleAddNewTaskClick(event: Event) = setState { showAddTask = true }

    private fun RBuilder.renderNavigationBar() {
        fun activeIf(view: View) = if (state.view == view) "active" else ""

        div("pt-5 pb-1") {
            ul("nav nav-tabs") {
                li("nav-item") {
                    a(classes = "nav-link ${activeIf(ACTIVE_TASKS)}", href = "#") {
                        attrs.onClickFunction = { setState { view = ACTIVE_TASKS }}
                        +"Task List"
                    }
                }
                li("nav-item") {
                    a(classes = "nav-link ${activeIf(FINISHED_TASKS)}", href = "#") {
                        attrs.onClickFunction = { setState { view = FINISHED_TASKS }}
                        +"Finished Tasks"
                    }
                }
            }
        }
    }

    private fun RBuilder.renderTasks() {
        renderAddTask()
        renderNavigationBar()

        val tasks = when (state.view) {
            ACTIVE_TASKS -> getActiveTasks()
            FINISHED_TASKS -> getFinishedTasks()
        }

        if (tasks.isEmpty()) {
            h5 { +"There are no tasks. Let's add some!" }
        } else {
            tasks.forEachIndexed { index, taskBean ->
                task(
                        taskBean,
                        index + 1,
                        onChanged = { fetchTasks() }
                )
            }
        }
    }

    private fun RBuilder.renderAddTask() {
        if (state.showAddTask) {
            addNewTask {
                setState { showAddTask = false }
                fetchTasks()
            }
        } else {
            button(type = ButtonType.button, classes = "btn btn-success pull-right") {
                attrs.onClickFunction = ::handleAddNewTaskClick
                i("fa fa-plus") {}
                +" Add new task!"
            }
        }
    }

    override fun RBuilder.render() {
        div("tasklist") {
            if (state.tasks == null) {
                h3("tasks-loading") {
                    +"Loading tasks ... "
                    i("fa fa-spinner fa-spin") { }
                }
            } else {
                renderTasks()
            }
        }
    }

}

fun RBuilder.taskList() = child(TaskList::class) {}
