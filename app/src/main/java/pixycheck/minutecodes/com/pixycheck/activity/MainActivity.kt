@file:Suppress("ObjectLiteralToLambda")

package pixycheck.minutecodes.com.pixycheck.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector
import android.view.View
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import pixycheck.minutecodes.com.pixycheck.R
import pixycheck.minutecodes.com.pixycheck.`interface`.DoubleTapEventForwarder
import pixycheck.minutecodes.com.pixycheck.constant.*
import pixycheck.minutecodes.com.pixycheck.gesture.GestureDetect
import pixycheck.minutecodes.com.pixycheck.helper.ColorDisplay
import pixycheck.minutecodes.com.pixycheck.java_files.DialogDismissListener
import pixycheck.minutecodes.com.pixycheck.java_files.NegativeButtonNotifier
import pixycheck.minutecodes.com.pixycheck.java_files.NeutralButtonNotifier
import pixycheck.minutecodes.com.pixycheck.java_files.PositiveButtonNotifier
import pixycheck.minutecodes.com.pixycheck.wrapper.AlertDialogWrapper

class MainActivity : Activity(), DoubleTapEventForwarder, View.OnClickListener {
    private var currentTilePosition = 0
    private lateinit var preferenceFile: SharedPreferences
    private lateinit var colorDisplay: ColorDisplay
    private lateinit var alertDialogWrapper: AlertDialogWrapper

    private fun loadPreference() {
        preferenceFile = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    private fun wasOneTimeMsgShown(): Boolean = preferenceFile.contains(FIRST_TIME_SHOW)

    private fun messageShown() = preferenceFile.edit().putBoolean(FIRST_TIME_SHOW, true).apply()

    override fun onDoubleTapped() {
        ++currentTilePosition
        when (currentTilePosition) {
            in 1..8 -> colorDisplay.changeColorTile(currentTilePosition)
            9 -> showMessageIfFirstInstall().also {
                currentTilePosition = 0
                colorDisplay.changeColorTile(currentTilePosition)
            }
        }
    }

    override fun onClick(p0: View?) {}

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putInt(TILE_POSITION, currentTilePosition)
            putBoolean(TAP_INFO_SHOWN, infoTextView.visibility == View.GONE)
            putBoolean(DIALOG_SHOWING, alertDialogWrapper.isDialogActive)
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        currentTilePosition = savedInstanceState.getInt(TILE_POSITION).also {
            colorDisplay.changeColorTile(it)
        }

        if (savedInstanceState.getBoolean(TAP_INFO_SHOWN))
            infoTextView.visibility = View.GONE

        if (savedInstanceState.getBoolean(DIALOG_SHOWING))
            showOneTimeMessage()

        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorDisplay = ColorDisplay(colorTileView)
        alertDialogWrapper = AlertDialogWrapper(this)
        loadPreference()

        val gestureDetector = GestureDetector(this, GestureDetect(this))
        val touchListener = View.OnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        colorTileView.apply {
            setOnTouchListener(touchListener)
            setOnClickListener(this@MainActivity)
            performClick()
        }
    }

    override fun onResume() {
        super.onResume()

        hideNavBars()
        setBrightness(1F)
        hideInfoTextView()
    }

    private fun hideInfoTextView() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                if (infoTextView.visibility == View.VISIBLE)
                    infoTextView.visibility = View.GONE
            }
        }, 2000)
    }


    private fun hideNavBars() {
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    private fun tryStartActivity(uriString: String) = try {
        openPlaySore(uriString)
    } catch (e: ActivityNotFoundException) {
        showErrorToast(getString(R.string.store_error)).also { hideNavBars() }
    }

    private fun showErrorToast(msg: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, msg, duration).show()
    }

    private fun openPlaySore(uriString: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).run {
            startActivity(this)
        }
    }

    private fun showOneTimeMessage() {
        alertDialogWrapper.apply {
            buildDialog()
        }.also {
            it.setupNegativeButton(getString(R.string.dialog_dismiss), NegativeButtonNotifier { hideNavBars() })
            it.setupPositiveButton(getString(R.string.dialog_rate), PositiveButtonNotifier { tryStartActivity(PIXYCHECK_URI) })
            it.setupNeutralButton(getString(R.string.dialog_donate), NeutralButtonNotifier { tryStartActivity(PIXYCHECK_DONATE_URI) })
        }.dismissListener(DialogDismissListener {
            messageShown()
        }).showDialog()
    }

    @Suppress("SameParameterValue")
    private fun setBrightness(value: Float) = window.attributes.apply { screenBrightness = value }

    private fun showMessageIfFirstInstall() {
        if (!wasOneTimeMsgShown())
            showOneTimeMessage()
    }

}