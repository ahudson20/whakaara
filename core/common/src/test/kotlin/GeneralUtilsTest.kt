import androidx.compose.ui.graphics.Color
import com.whakaara.core.GeneralUtils
import com.whakaara.core.GeneralUtils.Companion.toColorInt
import org.junit.Assert.assertEquals
import org.junit.Test

class GeneralUtilsTest {
    @Test
    fun `convert string to colour`() {
        // Given
        val string = "{\"value\":-4294967296}"

        // When
        val colour = GeneralUtils.convertStringToColour(string = string)

        // Then
        assertEquals(Color.White, colour)
    }

    @Test
    fun `convert float to colour int`() {
        // Given
        val float = 123.1F

        // When
        val value = float.toColorInt()

        // Then
        assertEquals(value, 31391)
    }
}
