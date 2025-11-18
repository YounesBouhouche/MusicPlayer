package younesbouhouche.musicplayer.core.presentation.util

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import younesbouhouche.musicplayer.ui.theme.topAppBarTitleFont

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {
        val backDispatcher = LocalOnBackPressedDispatcherOwner.current
        ExpressiveIconButton(
            Icons.AutoMirrored.Default.ArrowBack,
            size = IconButtonDefaults.mediumIconSize,
            widthOption = IconButtonDefaults.IconButtonWidthOption.Wide,
            colors = IconButtonDefaults.filledTonalIconButtonColors()
        ) {
            backDispatcher?.onBackPressedDispatcher?.onBackPressed()
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        scrolledContainerColor = MaterialTheme.colorScheme.background
    ),
    spacing: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            ProvideTextStyle(topAppBarTitleFont) {
                Row(Modifier.padding(spacing)) {
                    title()
                }
            }
        },
        actions = actions,
        modifier = modifier,
        navigationIcon = navigationIcon,
        contentPadding = contentPadding,
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TopBarPreview() {
    TopBar(
        title = {
            Text("Settings")
        }
    )
}
