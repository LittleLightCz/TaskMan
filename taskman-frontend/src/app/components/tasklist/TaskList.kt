package app.components.tasklist

import app.bean.TaskBean
import app.components.task.task
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import kotlin.browser.window
import kotlin.js.Promise

interface TaskListState: RState {
    var tasks: List<TaskBean>?
    var showAddTask: Boolean
}

class TaskList: RComponent<RProps, TaskListState>() {

    private var tasksRefreshIntervalID = 0

    override fun TaskListState.init() {
        tasks = null
        showAddTask = false
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
            setState { tasks = it.data.asList() }
        }
    }

    private fun getTasks() = state.tasks ?: emptyList()

    private fun getActiveTasks() = getTasks().filter { !it.deleted && it.completedDate == null }

    private fun handleAddNewTaskClick(event: Event) = setState { showAddTask = true }

    private fun RBuilder.renderTasks() {
        renderAddTask()

        h2("tasklist-title") { +"Task List" }

        getActiveTasks().forEachIndexed { index, taskBean ->
            task(taskBean, index.toString())
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
