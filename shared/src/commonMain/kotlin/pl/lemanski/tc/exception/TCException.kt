package pl.lemanski.tc.exception

/**
 * This is a base exception class for all exceptions in this project.
 */
sealed class TCException(override val message: String) : Exception(message)