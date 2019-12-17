package org.swiftapps.nightmodeq

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HandlerCompat.postDelayed(Handler(), {
            NightModeManager.getMode().let { modeNow: NightModeManager.NightMode ->
                when (modeNow) {
                    NightModeManager.NightMode.yes -> rg.check(rb_yes.id)
                    NightModeManager.NightMode.no -> rg.check(rb_no.id)
                    NightModeManager.NightMode.auto -> rg.check(rb_auto.id)
                }
            }
        }, null, 500)


        listOf(rb_yes, rb_no, rb_auto).forEach {
            it.setOnClickListener { view ->
                val newMode: NightModeManager.NightMode = when (view.id) {
                    rb_yes.id -> NightModeManager.NightMode.yes
                    rb_no.id -> NightModeManager.NightMode.no
                    else -> NightModeManager.NightMode.auto
                }

                NightModeManager.setMode(newMode)
            }
        }

        btn_disable_battery_opt.setOnClickListener {
            val i = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setData(Uri.parse("package:$packageName"))
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }

        btn_toggle_service.setOnClickListener {
            DarkThemeTileService.startService(applicationContext, useForegroundService = true)
        }
    }
}
