
package com.tech.perfumos.data.network
class NetworkError(val errorCode: Int, override val message: String?) : Throwable(message)
