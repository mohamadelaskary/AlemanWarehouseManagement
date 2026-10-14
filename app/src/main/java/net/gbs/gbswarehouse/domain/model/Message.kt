package net.gbs.gbswarehouse.domain.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Represents a user-facing message that may come either from an Android string
 * resource (so it is translated into the currently selected app language) or as
 * a real/raw string (e.g. a message returned by the backend API).
 *
 * ViewModels must never call `context.getString(...)` directly: the injected
 * application context is not locale aware and would always resolve to the
 * default resources. Instead a ViewModel exposes a [Message] and Compose
 * resolves it through [text].
 */
sealed class Message {

    /** A message backed by an Android string resource. */
    class Resource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : Message() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Resource) return false
            return resId == other.resId && args.contentEquals(other.args)
        }

        override fun hashCode(): Int = 31 * resId + args.contentHashCode()
    }

    /** A message backed by a plain, already-formatted string (usually from the API). */
    data class Real(
        val value: String
    ) : Message()

    /** A message built by concatenating several parts (resources and/or real strings). */
    data class Concatenated(
        val parts: List<Message>
    ) : Message()

    /** Resolves this message to a localized string. Must be called inside a Composable. */
    val text: String
        @Composable
        get() = when (this) {
            is Resource -> stringResource(
                resId,
                *args.map { if (it is Message) it.text else it }.toTypedArray()
            )
            is Real -> value
            is Concatenated -> {
                val builder = StringBuilder()
                for (part in parts) builder.append(part.text)
                builder.toString()
            }
        }

    /** Concatenates this message with another message. */
    operator fun plus(other: Message): Message {
        val left = if (this is Concatenated) parts else listOf(this)
        val right = if (other is Concatenated) other.parts else listOf(other)
        return Concatenated(left + right)
    }

    /** Concatenates a real string to this message. */
    operator fun plus(other: String): Message = this + Real(other)

    /** Concatenates any other value (number, etc.) to this message. */
    operator fun plus(other: Any): Message = this + Real(other.toString())

    companion object {

        /** Creates a resource message. */
        fun res(@StringRes resId: Int, vararg args: Any): Message = Resource(resId, *args)

        /** Creates a real (raw) message. Accepts a nullable string. */
        fun real(value: String?): Message = Real(value.orEmpty())

        /**
         * Uses [value] when it is not blank, otherwise falls back to the resource
         * [resId]. Handy for `apiMessage ?: R.string.default_message` cases.
         */
        fun of(value: String?, @StringRes resId: Int, vararg args: Any): Message =
            if (value.isNullOrBlank()) Resource(resId, *args) else Real(value)
    }
}
