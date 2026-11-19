package moe.shizuku.manager.ui.compose

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * TV Focus & 10-Foot Design System extensions for Shimika.
 */

/**
 * Detects whether the current device is running as a television.
 */
@Composable
fun isTelevision(): Boolean {
    val context = LocalContext.current
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
    if (uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
        return true
    }
    val config = LocalConfiguration.current
    val packageManager = context.packageManager
    return packageManager.hasSystemFeature("android.software.leanback") ||
            (config.uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION
}

/**
 * Safe content margins for Android TV overscan protection.
 */
val TvSafeContentPadding: PaddingValues = PaddingValues(
    horizontal = 32.dp,
    vertical = 24.dp
)

/**
 * Reusable TV focus highlight modifier.
 * Applies:
 * - D-pad focusability
 * - Subtle scale expansion (default 1.03x)
 * - High-contrast glowing focus border
 * - Focus change event listener
 */
@Composable
fun Modifier.tvFocusHighlight(
    shape: Shape = RoundedCornerShape(16.dp),
    focusedScale: Float = 1.03f,
    borderWidth: Dp = 2.5.dp,
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor: Color = Color.Transparent,
    onFocusChange: ((Boolean) -> Unit)? = null
): Modifier {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusedScale else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "tvFocusScale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) focusedBorderColor else unfocusedBorderColor,
        animationSpec = tween(durationMillis = 150),
        label = "tvFocusBorder"
    )

    return this
        .scale(scale)
        .border(
            border = BorderStroke(if (isFocused) borderWidth else 0.dp, borderColor),
            shape = shape
        )
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
            onFocusChange?.invoke(focusState.isFocused)
        }
        .focusable()
}

/**
 * Helper to intercept D-pad Center or Enter key events for explicit click actions.
 */
fun Modifier.onDpadCenterOrEnter(onAction: () -> Unit): Modifier {
    return this.onKeyEvent { keyEvent ->
        if (keyEvent.type == KeyEventType.KeyUp &&
            (keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter)
        ) {
            onAction()
            true
        } else {
            false
        }
    }
}

/**
 * TV Navigation Rail component for landscape 10-foot TV experience.
 * Displays vertically on the left edge with clear focus borders and D-pad support.
 */
@Composable
fun TvNavigationRail(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(110.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 24.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .tvFocusHighlight(
                        shape = RoundedCornerShape(12.dp),
                        focusedScale = 1.05f,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onItemSelected(index) }
                    .onDpadCenterOrEnter { onItemSelected(index) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(28.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
