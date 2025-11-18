package younesbouhouche.musicplayer.features.permissions.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.presentation.util.Event
import younesbouhouche.musicplayer.features.main.presentation.util.EventBus
import younesbouhouche.musicplayer.features.main.presentation.util.expressiveRectShape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PermissionsNavGraph(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        Column(
            Modifier.weight(1f).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.permission_required),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(R.string.permission_text),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(Modifier
            .clip(expressiveRectShape(
                0,
                2,
                RoundedCornerShape(0),
                MaterialTheme.shapes.extraLarge
            ))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .navigationBarsPadding()
            .padding(16.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.let_s_get_started),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            ExpressiveIconButton(
                Icons.AutoMirrored.Filled.ArrowForward,
                size = IconButtonDefaults.largeIconSize,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            ) {
                scope.launch(Dispatchers.Main) {
                    EventBus.sendEvent(Event.Initiate)
                }
            }
        }
    }
}