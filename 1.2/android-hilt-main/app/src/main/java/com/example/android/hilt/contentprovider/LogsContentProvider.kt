package com.example.android.hilt.contentprovider

import android.content.*
import android.database.Cursor
import android.net.Uri
import com.example.android.hilt.data.LogDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

private const val LOGS_TABLE = "logs"
private const val AUTHORITY = "com.example.android.hilt.provider"
private const val CODE_LOGS_DIR = 1
private const val CODE_LOGS_ITEM = 2

class LogsContentProvider : ContentProvider() {

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface LogsContentProviderEntryPoint{
        fun logDao(): LogDao
    }

    private fun getLogDao(appContext: Context): LogDao{
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            LogsContentProviderEntryPoint::class.java
        )
        return hiltEntryPoint.logDao()
    }

    private val matcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, LOGS_TABLE, CODE_LOGS_DIR)
        addURI(AUTHORITY, "$LOGS_TABLE/*", CODE_LOGS_ITEM)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code: Int = matcher.match(uri)
        return if (code == CODE_LOGS_DIR || code == CODE_LOGS_ITEM){
            val appContext = context?.applicationContext ?: throw  IllegalStateException()
            val logDao: LogDao = getLogDao(appContext)

            val cursor: Cursor? = if(code == CODE_LOGS_DIR){
                logDao.selectAllLogsCursor()
            }else{
                logDao.selectLogById(ContentUris.parseId(uri))
            }
            cursor?.setNotificationUri(appContext.contentResolver, uri)
            cursor

        }else{
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }



    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        throw UnsupportedOperationException("Only reading operations are allowed")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        throw UnsupportedOperationException("Only reading operations are allowed")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        throw UnsupportedOperationException("Only reading operations are allowed")
    }

    override fun getType(p0: Uri): String? {
        throw UnsupportedOperationException("Only reading operations are allowed")
    }
}