package library

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.ninenox.kotlinlocalemanager.LocaleManager
import tgl.lecompteurdelaroulotte.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.util.*

object Helper {
    private const val TAG = "==== HELPER ===="
    lateinit var context: MainActivity
    private lateinit var resources: Resources
    private lateinit var properties: Properties
    private lateinit var f: File

    fun init(c: Context) {
        context = c as MainActivity
        resources = context.resources
        properties = Properties()
        try {
            val fileName = "config.properties"
            f = File(context.filesDir.path + "/" + fileName)
            if (f.exists()) {
                properties.load(FileReader(f))
            } else {
                f.setReadable(true)
                f.setWritable(true)
                f.createNewFile()
                init_elements()
                Log.v(TAG, properties.toString())
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Unable to find the config file: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to open config file. " + e.message)
            //e.printStackTrace()
        }
    }

    fun init_elements() {
        setConfigValue("screen_on", false.toString())
        setConfigValue("volume_on", true.toString())
        setConfigValue("language", "-")
    }

    fun getConfigValue(name: String): String? {
        return properties.getProperty(name)
    }

    fun setConfigValue(name: String, value: String) {
        properties.setProperty(name, value)
        properties.store(FileOutputStream(f), "This is an optional comment.")
    }

    fun isLanguageAvailable(language: String): Boolean {
        return languagesAvailable().contains(language)
    }

    fun setLocale() {
        when (context.language) {
            "NL" -> context.setNewLocale("NL")
            "FR" -> context.setNewLocale("FR")
            else -> context.setNewLocale("EN")
        }
    }

    fun getLanguage(): String {
        return LocaleManager(context).language.toString()
    }

    fun languagesAvailable(): List<String> {
        return listOf("FR", "NL", "EN")
    }

    fun saveProperties() {
        setConfigValue("screen_on", context.screenOn.toString())
        setConfigValue("volume_on", context.volumeOn.toString())
        setConfigValue("language", context.language)
    }
}