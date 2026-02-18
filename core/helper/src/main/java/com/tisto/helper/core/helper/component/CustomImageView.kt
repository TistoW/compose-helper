package com.tisto.helper.core.helper.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
    size: Dp = 50.dp,
    placeholder: Painter? = null,
    error: Painter? = null,
    shape: AvatarShape = AvatarShape.SQUARE,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 4.dp // default kalau SQUARE
) {
    val tempName = name.def("")
    var isError by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
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

    Box(
        modifier = modifier
            .size(size)
            .clip(avatarShape),
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
                    modifier = modifier
                        .fillMaxSize()
                        .background(if (initials.isNotEmpty()) colorFromText(tempName) else Colors.Gray5),
                    contentAlignment = Alignment.Center
                ) {
                    if (initials.isNotEmpty()) {
                        Text(
                            text = initials,
                            fontSize = size.value.sp * 0.4f,
                            fontWeight = FontWeight.Bold,
                            color = Colors.Gray3
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(size.value.dp * 0.5f),
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
                        modifier = modifier
                            .fillMaxSize()
                            .background(Colors.Gray5),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MyIcon.IcLoading,
                            contentDescription = null,
                            modifier = Modifier.size(size.value.dp * 0.5f),
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