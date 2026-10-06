package com.candyspace.stackoverflowusers.feature.usersearch.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.candyspace.stackoverflowusers.domain.model.BadgeCounts
import com.candyspace.stackoverflowusers.domain.model.User

@Composable
fun UserCard(
    modifier: Modifier = Modifier,
    user: User,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = "User ${user.displayName}, reputation ${user.reputation}, " +
                    "${user.badgeCounts.gold} gold, ${user.badgeCounts.silver} silver, " +
                    "${user.badgeCounts.bronze} bronze badges."
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.profileImage)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "${user.displayName}'s profile picture",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                val userLocation = user.location
                if (!userLocation.isNullOrBlank()) {
                    Text(
                        text = userLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = "Reputation: ${user.reputation}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                BadgeCountRow(badgeCounts = user.badgeCounts)
            }
        }
    }
}

@Composable
private fun BadgeCountRow(badgeCounts: BadgeCounts) {
    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BadgeTag(count = badgeCounts.gold, label = "Gold", color = Color(0xFFFFD700))
        Spacer(modifier = Modifier.width(8.dp))
        BadgeTag(count = badgeCounts.silver, label = "Silver", color = Color(0xFFC0C0C0))
        Spacer(modifier = Modifier.width(8.dp))
        BadgeTag(count = badgeCounts.bronze, label = "Bronze", color = Color(0xFFCD7F32))
    }
}

@Composable
private fun BadgeTag(count: Int, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BoxBadge(color = color)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$count $label",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BoxBadge(color: Color) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color),
    )
}
