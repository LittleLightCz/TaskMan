package app.extensions

import kotlin.js.Promise


fun <T> Promise<T>.axiosCatch(errorHandler: (String) -> Unit) = catch { e: dynamic ->
    errorHandler(e.response.data)
}
