package io.github.kiranshny.wifiinfo

import timber.log.Timber

class HyperlinkedDebugTree(private val showMethodName: Boolean = true) : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        with(element) {
            return "($fileName:$lineNumber)$methodName()"
        }
    }
}