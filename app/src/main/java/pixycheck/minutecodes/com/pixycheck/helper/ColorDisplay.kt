package pixycheck.minutecodes.com.pixycheck.helper

import android.graphics.Color
import android.view.View

class ColorDisplay(private val colorTile: View) {
    private val colorArray = arrayOf(Color.RED, Color.GREEN, Color.BLUE,
            Color.BLACK, Color.YELLOW, Color.WHITE,
            Color.CYAN, Color.DKGRAY, Color.MAGENTA)

    fun changeColorTile(cyclePosition: Int) = colorTile.setTileColor(cyclePosition)

    private fun View.setTileColor(position: Int) = setBackgroundColor(colorArray[position])
}