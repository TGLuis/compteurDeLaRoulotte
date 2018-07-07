package library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabase (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("DROP TABLE IF EXISTS '$DATABASE_NAME';")
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var db = this.writableDatabase
        this.deleteDb(db)
        this.onCreate(db)
    }

    private fun deleteDb(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS '$DATABASE_NAME';")
    }

    fun open(): Boolean {
        try {
            this.writableDatabase
        } catch (t: Throwable) {
            return false
        }
        return true
    }


    companion object {
        private const val TAG = "===== MYDATABASE ====="
        private const val DATABASE_NAME = "database.sqlite"
        private const val DATABASE_VERSION = 0
    }
}