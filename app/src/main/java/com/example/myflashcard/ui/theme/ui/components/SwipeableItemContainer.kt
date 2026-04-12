package com.example.flashcardapp.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.flashcardapp.ui.theme.PastelRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableItemContainer(
    onAction: () -> Unit,
    actionColor: Color = PastelRed, // Mặc định là đỏ (Xóa), có thể truyền LiquidBlue (Sửa)
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                // Kích hoạt hành động khi vuốt quá 1 nửa
                onAction()
                // Thẻ tự trượt về vị trí cũ
                false 
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = enabled,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> actionColor
                    else -> Color.Transparent
                }, label = "bg"
            )
            
            val isRevealed = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart || 
                             dismissState.currentValue == SwipeToDismissBoxValue.EndToStart
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color),
                contentAlignment = Alignment.CenterEnd
            ) {
                // Không hiển thị Icon để tối giản giao diện
            }
        },
        content = { content() }
    )
}
