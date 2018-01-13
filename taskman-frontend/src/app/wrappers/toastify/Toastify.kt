package app.wrappers.toastify

interface ToastifyOptions {
    var text: String
    var duration: Int
    var destination: String
    var newWindow: Boolean
    var close: Boolean
    var gravity: String
    var positionLeft: Boolean
    var backgroundColor: String
}

interface ToastifyObject {
    fun showToast()
}

external fun Toastify(options: ToastifyOptions): ToastifyObject


