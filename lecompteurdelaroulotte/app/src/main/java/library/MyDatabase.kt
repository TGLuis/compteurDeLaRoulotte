package library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabase (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    inner class Tuple {
        var c1: Counter
        var c2: String

        constructor(a: Counter, b: String){
            c1 = a
            c2 = b
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("DROP TABLE IF EXISTS '$DATABASE_NAME';")
        db.execSQL("CREATE TABLE '" + PROJECT_TABLE + "' ('" +
                PROJECT_NAME + "' TEXT NOT NULL PRIMARY KEY, '" +
                ETAT + "' INTEGER NOT NULL, '" +
                NOTES + "' TEXT NOT NULL);")
        db.execSQL("CREATE TABLE '" + RULE_TABLE + "' ('" +
                PROJECT_NAME + "' TEXT NOT NULL REFERENCES PROJECT_TABLE, '" +
                AUGMENTATION + "' INTEGER NOT NULL, '" +
                FIRST + "' INTEGER NOT NULL, '" +
                SECOND + "' INTEGER NOT NULL, '" +
                THIRD + "' INTEGER NOT NULL);" )
        db.execSQL("CREATE TABLE '" + COUNTER_TABLE + "' ('" +
                PROJECT_NAME + "' TEXT NOT NULL REFERENCES PROJECT_TABLE, '" +
                COUNTER_NAME + "' TEXT NOT NULL, '" +
                ETAT + "' INTEGER NOT NULL, '" +
                TOURS + "' INTEGER NOT NULL, '" +
                MAX + "' INTEGER NOT NULL, '" +
                ATTACHED_MAIN + "' INTEGER NOT NULL, '" +
                COUNTER_ATTACHED + "' TEXT REFERENCES COUNTER_NAME, " +
                "UNIQUE ($PROJECT_NAME, $COUNTER_NAME) ON CONFLICT REPLACE);")
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val db = this.writableDatabase
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

    fun getAllProjects(): ArrayList<Project>{
        val db = this.writableDatabase
        val query = "SELECT $PROJECT_NAME, $ETAT, $NOTES FROM $PROJECT_TABLE;"
        val cursor = db.rawQuery(query, null)
        var myProjects: ArrayList<Project>? = null
        myProjects = ArrayList()
        if (cursor.moveToFirst()) {
            do {
                val projectName = cursor.getString(0)
                val etat = cursor.getInt(1)
                val notes = cursor.getString(2)
                val proj = Project(projectName)
                proj.etat = etat
                proj.notes = notes
                val query2 = "SELECT $AUGMENTATION, $FIRST, $SECOND, $THIRD FROM $RULE_TABLE WHERE $PROJECT_NAME='$projectName';"
                val cursor2 = db.rawQuery(query2, null)
                if (cursor2.moveToFirst()){
                    do {
                        val augm = cursor2.getInt(0)==1
                        val first = cursor2.getInt(1)
                        val second = cursor2.getInt(2)
                        val third = cursor2.getInt(3)
                        proj.addRule(Rule(augm, first, second, third))
                    } while(cursor2.moveToNext())
                }
                cursor2.close()
                
                val query3 = "SELECT $COUNTER_NAME, $ETAT, $TOURS, $MAX, $ATTACHED_MAIN, $COUNTER_ATTACHED FROM $COUNTER_TABLE WHERE $PROJECT_NAME='$projectName'"
                val cursor3 = db.rawQuery(query3, null)
                val tab = ArrayList<Tuple>()
                if(cursor3.moveToFirst()){
                    do {
                        val counterName = cursor3.getString(0)
                        val etat = cursor3.getInt(1)
                        val tours = cursor3.getInt(2)
                        val max = cursor3.getInt(3)
                        val attachedMain = cursor3.getInt(4)==1
                        val counterAttached = cursor3.getString(5)
                        val count = Counter(counterName, max, attachedMain, null)
                        count.etat = etat
                        count.tours = tours
                        if(counterAttached != NO_ATTACHED) {
                            tab.add(Tuple(count, counterAttached))
                        }
                        proj.addCounter(count)
                        if(attachedMain) {
                            proj.attach(count)
                        }
                    } while (cursor3.moveToNext())
                }
                tab.forEach {
                    it.c1.attach(proj.getCounter(it.c2)!!)
                }
                cursor3.close()

                myProjects.add(proj)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return myProjects
    }

    fun addProjectDB(projectName: String, notes: String): Boolean{
        val db = this.writableDatabase
        db.execSQL("INSERT INTO $PROJECT_TABLE ($PROJECT_NAME, $ETAT, $NOTES) " +
                "VALUES ('$projectName', 0,'$notes');")
        return true
    }

    fun updateProjectDB(projectName: String, etat: Int, notes: String): Boolean{
        val db = this.writableDatabase
        db.execSQL("UPDATE $PROJECT_TABLE " +
                " SET $NOTES='$notes', $ETAT=$etat" +
                " WHERE $PROJECT_NAME='$projectName';")
        return true
    }

    fun deleteProjectDB(projectName: String): Boolean{
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $PROJECT_TABLE WHERE $PROJECT_NAME='$projectName';")
        db.execSQL("DELETE FROM $RULE_TABLE WHERE $PROJECT_NAME='$projectName';")
        db.execSQL("DELETE FROM $COUNTER_TABLE WHERE $PROJECT_NAME='$projectName';")
        return true
    }

    fun addRuleDB(projectName: String, augmentation: Boolean, first: Int, second: Int, third: Int): Boolean{
        val db = this.writableDatabase
        val augm = if(augmentation) 1 else 0
        db.execSQL("INSERT INTO $RULE_TABLE ($PROJECT_NAME, $AUGMENTATION, $FIRST, $SECOND, $THIRD) "+
                "VALUES ('$projectName', $augm, $first, $second, $third);")
        return true
    }

    fun deleteRuleDB(projectName: String, augmentation: Boolean, first: Int, second: Int, third: Int): Boolean{
        val db = this.writableDatabase
        val augm = if(augmentation) 1 else 0
        db.execSQL("DELETE FROM $RULE_TABLE WHERE $PROJECT_NAME='$projectName' AND $AUGMENTATION=$augm"+
                " AND $FIRST=$first AND $SECOND=$second AND $THIRD=$third;")
        return true
    }

    fun addCounterDB(projectName: String, counterName: String, etat: Int, tours: Int, max: Int, attached_main: Boolean, attachedCounter: Counter?): Boolean{
        val db = this.writableDatabase
        val att = if(attached_main) 1 else 0
        val counterAtt = if(attachedCounter == null)  NO_ATTACHED else attachedCounter.name
        db.execSQL("INSERT INTO $COUNTER_TABLE ( $PROJECT_NAME, $COUNTER_NAME, $ETAT, $TOURS, $MAX, $ATTACHED_MAIN, $COUNTER_ATTACHED ) "+
                "VALUES ( '$projectName', '$counterName', $etat, $tours, $max, $att, '$counterAtt' );")
        return true
    }

    fun updateCounterDB(projectName: String, counterName: String, etat: Int, tours: Int, max: Int, attached_main: Boolean, attachedCounter: Counter?): Boolean{
        val db = this.writableDatabase
        val att = if(attached_main) 1 else 0
        val counterAtt = if(attachedCounter == null)  NO_ATTACHED else attachedCounter.name
        db.execSQL("UPDATE $COUNTER_TABLE " +
                " SET $ETAT=$etat, $TOURS=$tours, $MAX=$max, $ATTACHED_MAIN=$att, $COUNTER_ATTACHED='$counterAtt'" +
                " WHERE $PROJECT_NAME='$projectName' AND $COUNTER_NAME='$counterName';")
        return true
    }

    fun deleteCounterDB(projectName: String, counterName: String): Boolean{
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $COUNTER_TABLE " +
                " WHERE $PROJECT_NAME='$projectName' AND $COUNTER_NAME='$counterName';")
        return true
    }


    companion object {
        private const val TAG = "===== MYDATABASE ====="
        private const val DATABASE_NAME = "database.sqlite"
        private const val DATABASE_VERSION = 1

        private const val PROJECT_TABLE = "project"
        private const val PROJECT_NAME = "projectName"
        private const val NOTES = "notes"
        private const val RULE_TABLE = "rules"
        private const val AUGMENTATION = "augmentation"
        private const val FIRST = "first"
        private const val SECOND = "second"
        private const val THIRD = "third"
        private const val COUNTER_TABLE = "counter"
        private const val COUNTER_NAME = "counterName"
        private const val ETAT = "etat"
        private const val TOURS = "tours"
        private const val MAX = "max"
        private const val ATTACHED_MAIN = "attached"
        private const val COUNTER_ATTACHED = "counterAttached"

        private const val NO_ATTACHED = "__--NO--__"
    }
}


/*
CREATE TABLE 'project' ('projectName' TEXT NOT NULL PRIMARY KEY, 'etat' INTEGER NOT NULL, 'notes' TEXT NOT NULL);
CREATE TABLE 'rules' ('projectName' TEXT NOT NULL REFERENCES project, 'augmentation' INTEGER NOT NULL, 'first' INTEGER NOT NULL, 'second' INTEGER NOT NULL, 'third' INTEGER NOT NULL);
CREATE TABLE 'counter' ('projectName' TEXT NOT NULL REFERENCES project, 'counterName' TEXT NOT NULL PRIMARY KEY, 'etat' INTEGER NOT NULL, 'tours' INTEGER NOT NULL, 'max' INTEGER NOT NULL, 'attached' INTEGER NOT NULL, 'counterAttached' TEXT REFERENCES counterName);

 */
