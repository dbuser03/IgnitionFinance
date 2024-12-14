package com.unimib.ignitionfinance.data.remote.service.excpetion


class FirestoreServiceException(message: String, cause: Throwable) : Exception(message, cause)