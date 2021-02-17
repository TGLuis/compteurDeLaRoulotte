package library

import android.content.Context
import android.content.SharedPreferences
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
    private const val SCREEN_ON = "screen_on"
    private const val VOLUME_ON = "volume_on"
    private const val LANGUAGE = "language"
    private lateinit var sharedPref: SharedPreferences
    private lateinit var f: File

    fun init(c: Context) {
        context = c as MainActivity
        sharedPref = context.getSharedPreferences("CompteurDeLaRoulotte_preferences", Context.MODE_PRIVATE)
        try {
            val fileName = "config.properties"
            f = File(context.filesDir.path + "/" + fileName)
            if (f.exists()) {
                val properties = Properties()
                properties.load(FileReader(f))
                val screenOn = properties.getProperty(SCREEN_ON).toBoolean()
                val volumeOn = properties.getProperty(VOLUME_ON).toBoolean()
                val language = properties.getProperty(LANGUAGE)
                setPrefScreen(screenOn)
                setPrefVolume(volumeOn)
                setPrefLanguage(language)
                f.delete()
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Unable to find the config file: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to open config file. " + e.message)
            //e.printStackTrace()
        }
    }

    fun setPrefScreen(isScreenOn: Boolean) {
        sharedPref.edit().putBoolean(SCREEN_ON, isScreenOn).apply()
    }

    fun getPrefScreen(): Boolean {
        return sharedPref.getBoolean(SCREEN_ON, false)
    }

    fun setPrefVolume(isVolumeOn: Boolean) {
        sharedPref.edit().putBoolean(VOLUME_ON, isVolumeOn).apply()
    }

    fun getPrefVolume(): Boolean {
        return sharedPref.getBoolean(VOLUME_ON, true)
    }

    fun setPrefLanguage(language: String) {
        if (!languagesAvailable().contains(language)) {
            Log.e("HELPER", "Language not known $language")
        } else {
            sharedPref.edit().putString(LANGUAGE, language).apply()
        }
    }

    fun getPrefLanguage(): String {
        return sharedPref.getString(LANGUAGE, null)?: "-"
    }

    fun isLanguageAvailable(language: String): Boolean {
        return languagesAvailable().contains(language)
    }

    fun getLanguage(): String {
        return LocaleManager(context).language.toString()
    }

    fun languagesAvailable(): List<String> {
        return listOf("-", "FR", "NL", "EN")
    }
}