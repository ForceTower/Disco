package dev.forcetower.unes.singer.domain.exception

class LoginDevException(cause: Throwable) : LoginFailedException("Something went wrong parsing the response", cause)