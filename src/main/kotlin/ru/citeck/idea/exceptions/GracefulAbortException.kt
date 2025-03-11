package ru.citeck.idea.exceptions

/**
 * Exception for graceful cancellation of an activity.
 * This exception should not trigger an error message.
 */
class GracefulAbortException(message: String) : RuntimeException(message)
