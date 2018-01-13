@file:JsModule("react-copy-to-clipboard")
package app.wrappers.clipboard

import react.RClass
import react.RProps

external interface CopyToClipboardProps: RProps {
    var text: String
    var onCopy: () -> Unit
}

@JsName("CopyToClipboard")
external val copyToClipboard: RClass<CopyToClipboardProps>


