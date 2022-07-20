package com.example.android.kotlincoroutines.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.kotlincoroutines.main.MainNetwork
import com.example.android.kotlincoroutines.main.Title
import com.example.android.kotlincoroutines.main.TitleDao
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TitleDaoFake(initialTitle: String) : TitleDao {

    private val insertedForNext = Channel<Title>(capacity = Channel.BUFFERED)

    override suspend fun insertTitle(title: Title) {
        insertedForNext.trySend(title)
        _titleLiveData.value = title
    }

    private val _titleLiveData = MutableLiveData<Title?>(Title(initialTitle))

    override val titleLiveData: LiveData<Title?>
        get() = _titleLiveData

    suspend fun nextInsertedOrNull(timeout: Long = 2_000): String? {
        var result: String? = null
        runBlocking {
            // wait for the next insertion to complete
            try {
                withTimeout(timeout) {
                    result = insertedForNext.receive().title
                }
            } catch (ex: TimeoutCancellationException) {
                // ignore
            }
        }
        return result
    }
}

class MainNetworkFake(var result: String) : MainNetwork {
    override suspend fun fetchNextTitle() = result
}

class MainNetworkCompletableFake() : MainNetwork {
    private var completable = CompletableDeferred<String>()

    override suspend fun fetchNextTitle() = completable.await()

    fun sendCompletionToAllCurrentRequests(result: String) {
        completable.complete(result)
        completable = CompletableDeferred()
    }

    fun sendErrorToCurrentRequests(throwable: Throwable) {
        completable.completeExceptionally(throwable)
        completable = CompletableDeferred()
    }

}

typealias MakeCompilerHappyForStarterCode = FakeCallForRetrofit<String>

/**
 * This class only exists to make the starter code compile. Remove after refactoring retrofit to use
 * suspend functions.
 */
class FakeCallForRetrofit<T> : Call<T> {
    override fun enqueue(callback: Callback<T>) {
        // nothing
    }

    override fun isExecuted() = false

    override fun clone(): Call<T> {
        return this
    }

    override fun isCanceled() = true

    override fun cancel() {
        // nothing
    }

    override fun execute(): Response<T> {
        TODO("Not implemented")
    }

    override fun request(): Request {
        TODO("Not implemented")
    }

    override fun timeout(): Timeout {
        TODO("Not yet implemented")
    }

}