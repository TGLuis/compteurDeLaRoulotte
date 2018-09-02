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
        db.execSQL("CREATE TABLE '" + COUNTER_TABLE + "' ('" +
                PROJECT_NAME + "' TEXT NOT NULL REFERENCES PROJECT_TABLE, '" +
                COUNTER_NAME + "' TEXT NOT NULL, '" +
                ETAT + "' INTEGER NOT NULL, '" +
                TOURS + "' INTEGER NOT NULL, '" +
                MAX + "' INTEGER NOT NULL, '" +
                ORDER + "' INTEGER NOT NULL, '" +
                ATTACHED_MAIN + "' INTEGER NOT NULL, '" +
                COUNTER_ATTACHED + "' TEXT REFERENCES COUNTER_NAME, " +
                "UNIQUE ($PROJECT_NAME, $COUNTER_NAME) ON CONFLICT REPLACE);")
        db.execSQL("CREATE TABLE '" + RULE_TABLE + "' ('" +
                PROJECT_NAME + "' TEXT NOT NULL REFERENCES PROJECT_TABLE, '" +
                START + "' INTEGER NOT NULL, '" +
                NUM + "' INTEGER NOT NULL, " +
                "UNIQUE ($PROJECT_NAME, $NUM) ON CONFLICT REPLACE);" )
        db.execSQL("CREATE TABLE '$STEP_TABLE' (" +
                "'$PROJECT_NAME' TEXT NOT NULL REFERENCES PROJECT_TABLE, "+
                "'$NUM' INTEGER NOT NULL, "+
                "'$AUGMENTATION' INTEGER NOT NULL, "+
                "'$ORDER' INTEGER NOT NULL, "+
                "'$FIRST' INTEGER NOT NULL, "+
                "'$SECOND' INTEGER NOT NULL, "+
                "'$THIRD' INTEGER NOT NULL, "+
                "UNIQUE ($PROJECT_NAME, $NUM, $ORDER) ON CONFLICT REPLACE);")
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
        val myProjects = ArrayList<Project>()
        if (cursor.moveToFirst()) {
            do {
                val projectName = cursor.getString(0)
                val etat = cursor.getInt(1)
                val notes = cursor.getString(2)
                val proj = Project(projectName.replace('\r','\''))//astuce pour les guillemets
                proj.etat = etat
                proj.notes = notes.replace('\r','\'')
                
                val queryR = "SELECT $START, $NUM FROM $RULE_TABLE WHERE $PROJECT_NAME='$projectName';"
                val cursorR = db.rawQuery(queryR, null)
                if (cursorR.moveToFirst()){
                    do {
                        val start = cursorR.getInt(0)
                        val num = cursorR.getInt(1)
                        val myRule = Rule(start, num)

                        val queryS = "SELECT $ORDER, $AUGMENTATION, $FIRST, $SECOND, $THIRD FROM $STEP_TABLE WHERE $PROJECT_NAME='$projectName' AND $NUM=$num;"
                        val cursorS = db.rawQuery(queryS, null)
                        if(cursorS.moveToFirst()){
                            val myArr = ArrayList<Step>()
                            do{
                                val order = cursorS.getInt(0)
                                val augm = cursorS.getInt(1) == 1
                                val first = cursorS.getInt(2)
                                val second = cursorS.getInt(3)
                                val third = cursorS.getInt(4)
                                myArr.add(order, Step(augm, first, second, third))
                            }while(cursorS.moveToNext())
                            myRule.steps = myArr
                        }
                        proj.addRule(myRule)
                    } while(cursorR.moveToNext())
                }
                cursorR.close()
                
                val queryC = "SELECT $COUNTER_NAME, $ETAT, $TOURS, $MAX, $ORDER, $ATTACHED_MAIN, $COUNTER_ATTACHED FROM $COUNTER_TABLE WHERE $PROJECT_NAME='$projectName'"
                val cursorC = db.rawQuery(queryC, null)
                val tab = ArrayList<Tuple>()
                if(cursorC.moveToFirst()){
                    do {
                        val counterName = cursorC.getString(0).replace('\r','\'')
                        val etat = cursorC.getInt(1)
                        val tours = cursorC.getInt(2)
                        val max = cursorC.getInt(3)
                        val order = cursorC.getInt(4)
                        val attachedMain = cursorC.getInt(5)==1
                        val counterAttached = cursorC.getString(6)
                        val count = Counter(counterName, max, order, attachedMain, null)
                        count.etat = etat
                        count.tours = tours
                        if(counterAttached != NO_ATTACHED) {
                            tab.add(Tuple(count, counterAttached))
                        }
                        proj.addCounter(count)
                        if(attachedMain) {
                            proj.attach(count)
                        }
                    } while (cursorC.moveToNext())
                }
                tab.forEach {
                    it.c1.attach(proj.getCounter(it.c2)!!)
                }
                cursorC.close()

                proj.constructRappel()
                myProjects.add(proj)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return myProjects
    }

    fun addProjectDB(projectName: String, notes: String): Boolean{
        val db = this.writableDatabase
        val name = projectName.replace('\'','\r')
        db.execSQL("INSERT INTO $PROJECT_TABLE ($PROJECT_NAME, $ETAT, $NOTES) " +
                "VALUES ('$name', 0,'$notes');")
        return true
    }

    fun updateProjectDB(projectName: String, etat: Int, notes: String): Boolean{
        val db = this.writableDatabase
        val name = projectName.replace('\'','\r')
        val newnote = notes.replace('\'','\r')
        db.execSQL("UPDATE $PROJECT_TABLE " +
                " SET $NOTES='$newnote', $ETAT=$etat" +
                " WHERE $PROJECT_NAME='$name';")
        return true
    }

    fun deleteProjectDB(projectName: String): Boolean{
        val db = this.writableDatabase
        val name = projectName.replace('\'','\r')
        db.execSQL("DELETE FROM $PROJECT_TABLE WHERE $PROJECT_NAME='$name';")
        db.execSQL("DELETE FROM $RULE_TABLE WHERE $PROJECT_NAME='$name';")
        db.execSQL("DELETE FROM $COUNTER_TABLE WHERE $PROJECT_NAME='$name';")
        db.execSQL("DELETE FROM $STEP_TABLE WHERE $PROJECT_NAME='$name';")
        return true
    }

    fun addRuleDB(projectName: String, r: Rule): Boolean{
        val db = this.writableDatabase
        val name = projectName.replace('\'','\r')
        db.execSQL("INSERT INTO $RULE_TABLE ($PROJECT_NAME, $START, $NUM) "+
                "VALUES ('$name', ${r.start}, ${r.num});")
        for(s in 0 until r.steps.size){
            val augm = if(r.steps[s].augm) 1 else 0
            db.execSQL("INSERT INTO $STEP_TABLE ($PROJECT_NAME, $NUM, $ORDER, $AUGMENTATION, $FIRST, $SECOND, $THIRD) "+
                "VALUES ('$name', ${r.num}, $s, $augm, ${r.steps[s].one}, ${r.steps[s].two}, ${r.steps[s].three});")
        }
        return true
    }

    fun deleteRuleDB(projectName: String, r: Rule): Boolean{
        val db = this.writableDatabase
        val name = projectName.replace('\'','\r')
        db.execSQL("DELETE FROM $RULE_TABLE WHERE $PROJECT_NAME='$name' AND $NUM=${r.num};")
        for(s in 0 until r.steps.size){
            db.execSQL("DELETE FROM $STEP_TABLE WHERE $PROJECT_NAME='$name' AND $NUM=${r.num};")
        }
        return true
    }

    fun deleteStepDB(projectName: String, r: Rule, s: Step): Boolean{
        val db = this.writableDatabase
        val name = projectName.replace('\'','\r')
        val order = r.steps.indexOf(s)
        val augm = if(s.augm) 1 else 0
        db.execSQL("DELETE FROM $STEP_TABLE WHERE $PROJECT_NAME='$name' AND $NUM=${r.num} AND $AUGMENTATION=$augm "+
            "AND $ORDER=$order AND $FIRST=${s.one} AND $SECOND=${s.two} AND $THIRD=${s.three};")
        return true
    }

    fun addCounterDB(projectName: String, counterName: String, etat: Int, tours: Int, max: Int, order: Int, attached_main: Boolean, attachedCounter: Counter?): Boolean{
        val db = this.writableDatabase
        val att = if(attached_main) 1 else 0
        val name = projectName.replace('\'','\r')
        val nameC = counterName.replace('\'','\r')
        val counterAtt = if(attachedCounter == null)  NO_ATTACHED else attachedCounter.name
        db.execSQL("INSERT INTO $COUNTER_TABLE ( $PROJECT_NAME, $COUNTER_NAME, $ETAT, $TOURS, $MAX, $ORDER, $ATTACHED_MAIN, $COUNTER_ATTACHED ) "+
                "VALUES ( '$name', '$nameC', $etat, $tours, $max, $order, $att, '$counterAtt' );")
        return true
    }

    fun updateCounterDB(projectName: String, counterName: String, etat: Int, tours: Int, max: Int, order: Int, attached_main: Boolean, attachedCounter: Counter?): Boolean{
        val db = this.writableDatabase
        val att = if(attached_main) 1 else 0
        val name = projectName.replace('\'','\r')
        val nameC = counterName.replace('\'','\r')
        val counterAtt = if(attachedCounter == null)  NO_ATTACHED else attachedCounter.name
        db.execSQL("UPDATE $COUNTER_TABLE " +
                " SET $ETAT=$etat, $TOURS=$tours, $MAX=$max, $ORDER=$order, $ATTACHED_MAIN=$att, $COUNTER_ATTACHED='$counterAtt'" +
                " WHERE $PROJECT_NAME='$name' AND $COUNTER_NAME='$nameC';")
        return true
    }

    fun deleteCounterDB(projectName: String, counterName: String): Boolean{
        val db = this.writableDatabase
        val name = projectName.replace('\'','\r')
        val nameC = counterName.replace('\'','\r')
        db.execSQL("DELETE FROM $COUNTER_TABLE " +
                " WHERE $PROJECT_NAME='$name' AND $COUNTER_NAME='$nameC';")
        return true
    }


    companion object {
        private const val TAG = "===== MYDATABASE ====="
        private const val DATABASE_NAME = "database.sqlite"
        private const val DATABASE_VERSION = 1

        private const val PROJECT_TABLE = "project"
        private const val PROJECT_NAME = "projectName"
        private const val NOTES = "notes"
        private const val COUNTER_TABLE = "counter"
        private const val COUNTER_NAME = "counterName"
        private const val ETAT = "etat"
        private const val TOURS = "tours"
        private const val MAX = "max"
        private const val ATTACHED_MAIN = "attached"
        private const val COUNTER_ATTACHED = "counterAttached"
        private const val RULE_TABLE = "rules"
        private const val AUGMENTATION = "augmentation"
        private const val START = "start"
        private const val NUM = "numero"
        private const val STEP_TABLE = "steps"
        private const val ORDER = "ordr"
        private const val FIRST = "first"
        private const val SECOND = "second"
        private const val THIRD = "third"

        private const val NO_ATTACHED = "__--NO--__"
    }
}


/*
CREATE TABLE 'project' ('projectName' TEXT NOT NULL PRIMARY KEY, 'etat' INTEGER NOT NULL, 'notes' TEXT NOT NULL);
CREATE TABLE 'counter' ('projectName' TEXT NOT NULL REFERENCES project, 'counterName' TEXT NOT NULL PRIMARY KEY, 'etat' INTEGER NOT NULL, 'tours' INTEGER NOT NULL, 'max' INTEGER NOT NULL, 'attached' INTEGER NOT NULL, 'counterAttached' TEXT REFERENCES counterName);

 */
