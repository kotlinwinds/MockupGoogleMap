package co.winds

import android.app.Application
import android.content.Context


class KaryaKartaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext


       /* val fabric = Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build()
        Fabric.with(fabric)
*/

    }
    companion object {
        @get:Synchronized
        var appContext: Context? = null
            private set


    }





}
