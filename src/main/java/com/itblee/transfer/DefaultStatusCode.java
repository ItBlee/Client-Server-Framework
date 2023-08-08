package com.itblee.transfer;

public enum DefaultStatusCode implements StatusCode {
    OK,
    CREATED,
    BAD_REQUEST,
    UNAUTHENTICATED,
    FORBIDDEN,
    NOT_FOUND,
    CONFLICT,
    TIMEOUT,
    INTERNAL_SERVER_ERROR
}
