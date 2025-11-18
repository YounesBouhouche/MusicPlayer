package younesbouhouche.musicplayer.features.main.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage

@Composable
fun MyImagePicker(
    picture: Uri?,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.Default.AddAPhoto,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    shape: Shape = MaterialTheme.shapes.medium,
    background: Color = MaterialTheme.colorScheme.surfaceContainer,
    fraction: Float = 0.5f,
    onPick: (Uri?) -> Unit,
) {
    val photoPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = onPick,
        )
    MyImage(
        picture,
        icon,
        modifier,
        iconTint,
        shape,
        background,
        fraction,
        {
            photoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    )
}