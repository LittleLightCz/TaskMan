package app.components.tasklist

import app.bean.TaskBean
import app.components.task.task
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import kotlinx.html.ButtonType
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
//        fetchTasks()

        tasksRefreshIntervalID = window.setInterval({
            fetchTasks()
        }, 3000)
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

    fun getActiveTasks() = (state.tasks ?: emptyList()).filter { !it.deleted && it.completedDate == null }

    private fun RBuilder.renderTasks() {
        renderAddTask()

        h2("tasklist-title") { +"Task List" }

        getActiveTasks().forEachIndexed { index, taskBean ->
            task(taskBean, index.toString())
        }
    }

    private fun RBuilder.renderAddTask() {
        if (state.showAddTask) {
            //addNewTask()
        } else {
            button(type = ButtonType.button, classes = "btn btn-success pull-right") {
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
