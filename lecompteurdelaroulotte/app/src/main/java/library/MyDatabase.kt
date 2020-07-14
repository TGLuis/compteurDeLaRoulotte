package library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import lufra.lecompteurdelaroulotte.MainActivity

class MyDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    inner class Tuple(a: Counter, b: String) {
        var c1: Counter = a
        var c2: String = b

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("DROP TABLE IF EXISTS '$DATABASE_NAME';")
        db.execSQL("CREATE TABLE '$PROJECT_TABLE' (" +
                "'$PROJECT_NAME' TEXT NOT NULL PRIMARY KEY, " +
                "'$ETAT' INTEGER NOT NULL, " +
                "'$ARCHIVED' INTEGER NOT NULL, " +
                "'$PDF' TEXT, " +
                "'$NOTES' TEXT NOT NULL);")
        db.execSQL("CREATE TABLE '$COUNTER_TABLE' (" +
                "'$PROJECT_NAME' TEXT NOT NULL REFERENCES $PROJECT_TABLE, " +
                "'$COUNTER_NAME' TEXT NOT NULL, " +
                "'$ETAT' INTEGER NOT NULL, " +
                "'$MAX' INTEGER NOT NULL, " +
                "'$ORDER' INTEGER NOT NULL, " +
                "'$ATTACHED_MAIN' INTEGER NOT NULL, " +
                "'$COUNTER_ATTACHED' TEXT REFERENCES $COUNTER_NAME, " +
                "UNIQUE ($PROJECT_NAME, $COUNTER_NAME) ON CONFLICT REPLACE);")
        db.execSQL("CREATE TABLE '$RULE_TABLE' (" +
                "'$PROJECT_NAME' TEXT NOT NULL REFERENCES $PROJECT_TABLE, " +
                "'$START' INTEGER NOT NULL, " +
                "'$NUM' INTEGER NOT NULL, " +
                "'$COMMENT' TEXT NOT NULL, " +
                "'$COUNTER_ATTACHED' TEXT REFERENCES $COUNTER_NAME, " +
                "UNIQUE ($PROJECT_NAME, $NUM) ON CONFLICT REPLACE);")
        db.execSQL("CREATE TABLE '$STEP_TABLE' (" +
                "'$PROJECT_NAME' TEXT NOT NULL REFERENCES $PROJECT_TABLE, " +
                "'$NUM' INTEGER NOT NULL, " +
                "'$AUGMENTATION' INTEGER NOT NULL, " +
                "'$ORDER' INTEGER NOT NULL, " +
                "'$FIRST' INTEGER NOT NULL, " +
                "'$SECOND' INTEGER NOT NULL, " +
                "'$THIRD' INTEGER NOT NULL, " +
                "UNIQUE ($PROJECT_NAME, $NUM, $ORDER) ON CONFLICT REPLACE);")
        db.execSQL("CREATE TABLE '$COMMENT_TABLE' (" +
                "'$PROJECT_NAME' TEXT NOT NULL REFERENCES $PROJECT_TABLE, " +
                "'$NUM' INTEGER NOT NULL, " +
                "'$COMMENT' TEXT NOT NULL, " +
                "'$COUNTER_ATTACHED' TEXT REFERENCES $COUNTER_NAME, " +
                "'$START' INTEGER NOT NULL, " +
                "'$END' INTEGER NOT NULL, " +
                "UNIQUE ($PROJECT_NAME, $NUM) ON CONFLICT REPLACE);")
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val db = this.writableDatabase
        this.deleteDb(db)
        this.onCreate(db)
    }

    private fun deleteDb(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS '$DATABASE_NAME';")
    }

    fun getAllProjects(context: MainActivity): ArrayList<Project> {
        val db = this.writableDatabase
        val query = "SELECT $PROJECT_NAME, $ETAT, $ARCHIVED, $NOTES, $PDF FROM $PROJECT_TABLE;"
        val cursor = db.rawQuery(query, null)
        val myProjects = ArrayList<Project>()
        if (cursor.moveToFirst()) {
            do {
                val projectName = cursor.getString(0)
                val etat = cursor.getInt(1)
                val archived = cursor.getInt(2) == 1
                val notes = cursor.getString(3)
                val pdfString = cursor.getString(4).replace('\r', '\'')
                val proj = Project(context, projectName.replace('\r', '\''))//little tricks
                proj.etat = etat
                proj.archived = archived
                proj.notes = notes.replace('\r', '\'')
                if (pdfString != NONE)
                    proj.setPdfFromString(pdfString)

                retrieveCounters(db, proj, projectName)
                retrieveRules(db, proj, projectName)
                retrieveComments(db, proj, projectName)

                proj.constructRappel()
                myProjects.add(proj)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return myProjects
    }

    private fun retrieveCounters(db: SQLiteDatabase, proj: Project, projectName: String) {
        val query = "SELECT $COUNTER_NAME, $ETAT, $MAX, $ORDER, $ATTACHED_MAIN, $COUNTER_ATTACHED FROM $COUNTER_TABLE WHERE $PROJECT_NAME='$projectName';"
        val cursor = db.rawQuery(query, null)
        val tab = ArrayList<Tuple>()
        if (cursor.moveToFirst()) {
            do {
                val counterName = cursor.getString(0).replace('\r', '\'')
                val etat = cursor.getInt(1)
                val max = cursor.getInt(2)
                val order = cursor.getInt(3)
                val attachedMain = cursor.getInt(4) == 1
                val counterAttached = cursor.getString(5)
                val count = Counter(counterName, max, order, attachedMain, null)
                count.etat = etat
                if (counterAttached != NONE) {
                    tab.add(Tuple(count, counterAttached))
                }
                proj.addCounter(count)
                if (attachedMain) {
                    proj.attach(count)
                }
            } while (cursor.moveToNext())
        }
        tab.forEach {
            it.c1.attach(proj.getCounter(it.c2)!!)
        }
        cursor.close()
    }

    private fun retrieveRules(db: SQLiteDatabase, proj: Project, projectName: String) {
        val query = "SELECT $START, $NUM,$COUNTER_ATTACHED, $COMMENT FROM $RULE_TABLE WHERE $PROJECT_NAME='$projectName';"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val start = cursor.getInt(0)
                val num = cursor.getInt(1)
                val myRule = Rule(num, start)
                myRule.counter = cursor.getString(2).replace('\r', '\'')
                myRule.comment = cursor.getString(3).replace('\r', '\'')

                val queryS = "SELECT $ORDER, $AUGMENTATION, $FIRST, $SECOND, $THIRD FROM $STEP_TABLE WHERE $PROJECT_NAME='$projectName' AND $NUM=$num;"
                val cursorS = db.rawQuery(queryS, null)
                if (cursorS.moveToFirst()) {
                    val myArr = ArrayList<Step>()
                    do {
                        val order = cursorS.getInt(0)
                        val augm = cursorS.getInt(1) == 1
                        val first = cursorS.getInt(2)
                        val second = cursorS.getInt(3)
                        val third = cursorS.getInt(4)
                        myArr.add(order, Step(augm, first, second, third))
                    } while (cursorS.moveToNext())
                    myRule.steps = myArr
                }
                cursorS.close()
                proj.addRule(myRule)
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun retrieveComments(db: SQLiteDatabase, proj: Project, projectName: String) {
        val query = "SELECT $NUM, $COMMENT, $START, $END, $COUNTER_ATTACHED FROM $COMMENT_TABLE WHERE $PROJECT_NAME='$projectName';"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val num = cursor.getInt(0)
                val start = cursor.getInt(2)
                val end = cursor.getInt(3)
                val com = Comment(num, start, end)
                com.comment = cursor.getString(1).replace('\r', '\'')
                com.counter = cursor.getString(4).replace('\r', '\'')
                proj.addComment(com)
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    fun addProjectDB(project: Project): Boolean {
        val db = this.writableDatabase
        val name = project.name.replace('\'', '\r')
        val notes = project.notes.replace('\'', '\r')
        var pdfString = project.pdf?.toString()?.replace('\'', '\r')
        if (pdfString == null) pdfString = NONE
        db.execSQL("INSERT INTO $PROJECT_TABLE ($PROJECT_NAME, $ETAT, $ARCHIVED, $NOTES, $PDF) " +
                "VALUES ('$name', 0, 0, '$notes', '$pdfString');")
        return true
    }

    fun updateProjectDB(project: Project): Boolean {
        val db = this.writableDatabase
        val name = project.name.replace('\'', '\r')
        val newnote = project.notes.replace('\'', '\r')
        val arch = if (project.archived) 1 else 0
        var pdfString = project.pdf?.toString()?.replace('\'', '\r')
        if (pdfString == null) pdfString = NONE
        db.execSQL("UPDATE $PROJECT_TABLE " +
                " SET $NOTES='$newnote', $ETAT=${project.etat}, $ARCHIVED=${arch}, $PDF='${pdfString}'" +
                " WHERE $PROJECT_NAME='$name';")
        return true
    }

    fun deleteProjectDB(project: Project): Boolean {
        val db = this.writableDatabase
        val name = project.name.replace('\'', '\r')
        db.execSQL("DELETE FROM $PROJECT_TABLE WHERE $PROJECT_NAME='$name';")
        db.execSQL("DELETE FROM $RULE_TABLE WHERE $PROJECT_NAME='$name';")
        db.execSQL("DELETE FROM $COUNTER_TABLE WHERE $PROJECT_NAME='$name';")
        db.execSQL("DELETE FROM $STEP_TABLE WHERE $PROJECT_NAME='$name';")
        return true
    }

    fun isRuleInDB(project: Project, r: Rule): Boolean {
        val db = this.writableDatabase
        var ok = false
        val projectName = project.name.replace('\'', '\r')
        val cursor = db.rawQuery("SELECT $COMMENT FROM $RULE_TABLE WHERE " +
                "$NUM=${r.num} AND $PROJECT_NAME='$projectName';", null)
        if (cursor.moveToFirst()) {
            ok = true
        }
        cursor.close()
        return ok
    }

    fun isCommentInDB(project: Project, c: Comment): Boolean {
        val db = this.writableDatabase
        var ok = false
        val projectName = project.name.replace('\'', '\r')
        val cursor = db.rawQuery("SELECT $COMMENT FROM $COMMENT_TABLE WHERE " +
                "$NUM=${c.num} AND $PROJECT_NAME='$projectName';", null)
        if (cursor.moveToFirst()) {
            ok = true
        }
        cursor.close()
        return ok
    }

    fun addRuleDB(project: Project, r: Rule): Boolean {
        val db = this.writableDatabase
        val name = project.name.replace('\'', '\r')
        val comment = r.comment.replace('\'', '\r')
        val counter = r.counter.replace('\'', '\r')
        db.execSQL("INSERT INTO $RULE_TABLE ($PROJECT_NAME, $START, $NUM, $COMMENT, $COUNTER_ATTACHED) " +
                "VALUES ('$name', ${r.start}, ${r.num}, '$comment', '$counter');")
        for (s in 0 until r.steps.size) {
            val augm = if (r.steps[s].augm) 1 else 0
            db.execSQL("INSERT INTO $STEP_TABLE ($PROJECT_NAME, $NUM, $ORDER, $AUGMENTATION, $FIRST, $SECOND, $THIRD) " +
                    "VALUES ('$name', ${r.num}, $s, $augm, ${r.steps[s].one}, ${r.steps[s].two}, ${r.steps[s].three});")
        }
        return true
    }

    fun deleteRuleDB(project: Project, r: Rule): Boolean {
        val db = this.writableDatabase
        val name = project.name.replace('\'', '\r')
        db.execSQL("DELETE FROM $RULE_TABLE WHERE $PROJECT_NAME='$name' AND $NUM=${r.num};")
        for (s in 0 until r.steps.size) {
            db.execSQL("DELETE FROM $STEP_TABLE WHERE $PROJECT_NAME='$name' AND $NUM=${r.num};")
        }
        return true
    }

    fun deleteStepDB(project: Project, r: Rule, s: Step): Boolean {
        val db = this.writableDatabase
        val name = project.name.replace('\'', '\r')
        val order = r.steps.indexOf(s)
        val augm = if (s.augm) 1 else 0
        db.execSQL("DELETE FROM $STEP_TABLE WHERE $PROJECT_NAME='$name' AND $NUM=${r.num} AND $AUGMENTATION=$augm " +
                "AND $ORDER=$order AND $FIRST=${s.one} AND $SECOND=${s.two} AND $THIRD=${s.three};")
        return true
    }

    fun addCommentDB(project: Project, c: Comment) {
        val db = this.writableDatabase
        val name = project.name.replace('\'', '\r')
        val comment = c.comment.replace('\'', '\r')
        val counter = c.counter.replace('\'', '\r')
        db.execSQL("INSERT INTO $COMMENT_TABLE($PROJECT_NAME, $NUM, $START, $END, $COUNTER_ATTACHED, $COMMENT) " +
                "VALUES ('$name', ${c.num}, ${c.start}, ${c.end}, '${counter}', '${comment}');")
    }

    fun deleteCommentDB(project: Project, c: Comment) {
        val db = this.writableDatabase
        val name = project.name.replace('\'', '\r')
        db.execSQL("DELETE FROM $COMMENT_TABLE WHERE $PROJECT_NAME='$name' AND $NUM=${c.num};")
    }

    fun addCounterDB(project: Project, counterName: String, etat: Int, max: Int, order: Int, attached_main: Boolean, attachedCounter: Counter?): Boolean {
        val db = this.writableDatabase
        val att = if (attached_main) 1 else 0
        val name = project.name.replace('\'', '\r')
        val nameC = counterName.replace('\'', '\r')
        val counterAtt = attachedCounter?.name ?: NONE
        db.execSQL("INSERT INTO $COUNTER_TABLE ( $PROJECT_NAME, $COUNTER_NAME, $ETAT, $MAX, $ORDER, $ATTACHED_MAIN, $COUNTER_ATTACHED ) " +
                "VALUES ( '$name', '$nameC', $etat, $max, $order, $att, '$counterAtt' );")
        return true
    }

    fun updateCounterDB(project: Project, counterName: String, etat: Int, max: Int, order: Int, attached_main: Boolean, attachedCounter: Counter?): Boolean {
        val db = this.writableDatabase
        val att = if (attached_main) 1 else 0
        val name = project.name.replace('\'', '\r')
        val nameC = counterName.replace('\'', '\r')
        val counterAtt = attachedCounter?.name ?: NONE
        db.execSQL("UPDATE $COUNTER_TABLE " +
                " SET $ETAT=$etat, $MAX=$max, $ORDER=$order, $ATTACHED_MAIN=$att, $COUNTER_ATTACHED='$counterAtt'" +
                " WHERE $PROJECT_NAME='$name' AND $COUNTER_NAME='$nameC';")
        return true
    }

    fun deleteCounterDB(project: Project, counterName: String): Boolean {
        val db = this.writableDatabase
        val name = project.name.replace('\'', '\r')
        val nameC = counterName.replace('\'', '\r')
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
        private const val ARCHIVED = "archived"
        private const val PDF = "pdfURI"
        private const val COUNTER_TABLE = "counter"
        private const val COUNTER_NAME = "counterName"
        private const val ETAT = "etat"
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
        private const val COMMENT = "comment"
        private const val COMMENT_TABLE = "commentaires"
        private const val END = "end"

        private const val NONE = "__--NO--__"
    }
}
