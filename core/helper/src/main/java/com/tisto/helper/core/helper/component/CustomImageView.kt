package com.tisto.helper.core.helper.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.tisto.helper.core.helper.ui.icon.MyIcon
import com.tisto.helper.core.helper.ui.icon.myicon.IcLoading
import com.tisto.helper.core.helper.ui.theme.Colors
import com.tisto.helper.core.helper.utils.ext.MobilePreview
import com.tisto.helper.core.helper.utils.ext.colorFromText
import com.tisto.helper.core.helper.utils.ext.def
import com.tisto.helper.core.helper.utils.toPainter

enum class AvatarShape {
    CIRCLE,
    SQUARE
}

@Composable
fun CustomImageView(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    image: Painter? = null,
    name: String? = null,
    size: Dp? = null,
    width: Dp? = null,
    height: Dp? = null,
    placeholder: Painter? = null,
    error: Painter? = null,
    shape: AvatarShape = AvatarShape.SQUARE,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 4.dp
) {
    val tempName = name.def("")
    var isError by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    // Track actual size
    var actualHeight by remember { mutableStateOf(50.dp) }

    val initials = remember(tempName) {
        tempName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
    }
    val avatarShape = when (shape) {
        AvatarShape.CIRCLE -> CircleShape
        AvatarShape.SQUARE -> RoundedCornerShape(cornerRadius)
    }

    val sizeModifier = when {
        size != null -> Modifier.size(size)
        width != null && height != null -> Modifier.width(width).height(height)
        width != null -> Modifier.width(width)
        height != null -> Modifier.height(height)
        else -> Modifier
    }

    // Use actual measured height, or fallback to provided size/height
    val referenceSize = size ?: height ?: actualHeight

    Box(
        modifier = modifier
            .then(sizeModifier)
            .clip(avatarShape)
            .onSizeChanged { intSize ->
                // Update actual height when measured
                with(density) {
                    actualHeight = intSize.height.toDp()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (image != null && imageUrl == null) {
            Image(
                painter = image,
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            if (imageUrl.isNullOrBlank() || isError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (initials.isNotEmpty()) colorFromText(tempName) else Colors.Gray5),
                    contentAlignment = Alignment.Center
                ) {
                    if (initials.isNotEmpty()) {
                        Text(
                            text = initials,
                            fontSize = referenceSize.value.sp * 0.4f,
                            fontWeight = FontWeight.Bold,
                            color = Colors.Gray3
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(referenceSize * 0.5f),
                            tint = Colors.Gray4
                        )
                    }
                }
            } else {
                AsyncImage(
                    model = imageUrl,
                    placeholder = placeholder ?: MyIcon.IcLoading.toPainter(),
                    error = error,
                    contentDescription = null,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize(),
                    onError = {
                        isError = true
                    },
                    onSuccess = {
                        isSuccess = true
                    }
                )
                if (!isSuccess) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Colors.Gray5),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MyIcon.IcLoading,
                            contentDescription = null,
                            modifier = Modifier.size(referenceSize * 0.5f),
                            tint = Colors.Gray4
                        )
                    }
                }
            }
        }
    }
}

@MobilePreview
@Composable
fun CustomImageViewPreview() {
    CustomImageView(
        imageUrl = null,
    )
}