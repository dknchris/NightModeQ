package org.swiftapps.nightmodeq

import android.util.Log
import com.topjohnwu.superuser.Shell

@Suppress("ObjectPropertyName", "EnumEntryName")
object NightModeManager {

    private const val logTag = "NightModeManager"

    enum class NightMode {
        yes, no, auto
    }

    init {
        // Magisk shell configuration
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR)
        Shell.Config.verboseLogging(BuildConfig.DEBUG)
    }

    private var _shell: Shell? = null

    private val rootShell: Shell?
        get() = try {
            _shell?.takeIf { it.isRoot && it.isAlive }
                ?: Shell.newInstance("su").takeIf { it.isRoot }.also { _shell = it }
        } catch (e: Exception) {
            Log.e(logTag, "rootShell", e)
            null
        }

    fun toggleNightMode(): NightMode {
        // Improve this and get the current night mode in case it is set to auto
        val currentMode = getMode()
        val newMode = if (currentMode != NightMode.yes) NightMode.yes else NightMode.no
        setMode(newMode)
        return newMode
    }

    fun setMode(newMode: NightMode) {
        if (getMode() == newMode) return

        rootShell?.let { shell: Shell ->
            shell.newJob().add("cmd uimode night $newMode").exec()
        }
    }

    fun getMode(): NightMode {
        rootShell?.let { shell: Shell ->
            val output = mutableListOf<String>()
            shell.newJob().add("cmd uimode night").to(output).exec()
            output.firstOrNull()?.let { out: String ->
                return NightMode.valueOf(out.substringAfterLast(" "))
            }
        }

        return NightMode.no
    }
}