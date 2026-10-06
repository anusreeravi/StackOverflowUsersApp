package com.candyspace.stackoverflowusers.feature.usersearch.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.candyspace.stackoverflowusers.designsystem.StackAppTheme
import com.candyspace.stackoverflowusers.domain.model.BadgeCounts
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.feature.usersearch.presentation.components.UserCard
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(
    uiState: UserSearchUiState,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    usersPagingItems: LazyPagingItems<User>? = null,
    onEvent: (UserSearchEvent) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState) {
        if (uiState !is UserSearchUiState.Loading) {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "StackOverflow Users",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(UserSearchEvent.ExitClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Exit app",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Search Bar Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { onEvent(UserSearchEvent.SearchQueryChanged(it)) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search by name...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    onEvent(UserSearchEvent.SearchQueryChanged(""))
                                    onEvent(UserSearchEvent.SearchClicked)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Search",
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onEvent(UserSearchEvent.SearchClicked)
                        },
                    ),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onEvent(UserSearchEvent.SearchClicked)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    modifier = Modifier
                        .height(56.dp)
                        .semantics { contentDescription = "Perform search" },
                ) {
                    Text("Search")
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                if (usersPagingItems != null) {
                    // Paging 3 Rendering Logic
                    when (val refreshState = usersPagingItems.loadState.refresh) {
                        is LoadState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.semantics { contentDescription = "Loading users" },
                            )
                        }

                        is LoadState.Error -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "Failed to load users",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = refreshState.error.localizedMessage ?: "An unexpected error occurred",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        is LoadState.NotLoading -> {
                            if (usersPagingItems.itemCount == 0) {
                                Text(
                                    text = "No users found.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                ) {
                                    items(
                                        count = usersPagingItems.itemCount,
                                        key = usersPagingItems.itemKey { user -> user.id },
                                    ) { index ->
                                        val user = usersPagingItems[index]
                                        if (user != null) {
                                            UserCard(
                                                user = user,
                                                onClick = {
                                                    focusManager.clearFocus()
                                                    keyboardController?.hide()
                                                    onEvent(UserSearchEvent.UserClicked(user))
                                                },
                                            )
                                        }
                                    }

                                    // Append LoadState (Footer Pagination Loader & Error)
                                    when (val appendState = usersPagingItems.loadState.append) {
                                        is LoadState.Loading -> {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .semantics { contentDescription = "Loading more users" },
                                                    )
                                                }
                                            }
                                        }

                                        is LoadState.Error -> {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = appendState.error.localizedMessage ?: "Failed to load more users",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.error,
                                                        textAlign = TextAlign.Center,
                                                    )
                                                }
                                            }
                                        }

                                        is LoadState.NotLoading -> {}
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // UI State Rendering Logic
                    when (uiState) {
                        is UserSearchUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.semantics { contentDescription = "Loading users" },
                            )
                        }

                        is UserSearchUiState.Error -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "Failed to load users",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        is UserSearchUiState.Success -> {
                            if (uiState.users.isEmpty()) {
                                Text(
                                    text = "No users found.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                ) {
                                    items(
                                        items = uiState.users,
                                        key = { user -> user.id },
                                    ) { user ->
                                        UserCard(
                                            user = user,
                                            onClick = {
                                                focusManager.clearFocus()
                                                keyboardController?.hide()
                                                onEvent(UserSearchEvent.UserClicked(user))
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchUserScreen(
    uiState: UserSearchUiState,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    usersPagingItems: LazyPagingItems<User>? = null,
    onEvent: (UserSearchEvent) -> Unit = {},
) {
    UserSearchScreen(
        uiState = uiState,
        modifier = modifier,
        searchQuery = searchQuery,
        usersPagingItems = usersPagingItems,
        onEvent = onEvent,
    )
}

@Preview(showBackground = true)
@Composable
private fun UserSearchScreenPreview() {
    val previewUsers = listOf(
        User(
            id = 1L,
            displayName = "Test Developer",
            reputation = 1234,
            profileImage = null,
            location = "London, UK",
            creationDate = 0L,
            badgeCounts = BadgeCounts(gold = 1, silver = 2, bronze = 3),
        ),
    )
    val pagingItems = flowOf(PagingData.from(previewUsers)).collectAsLazyPagingItems()

    StackAppTheme {
        UserSearchScreen(
            uiState = UserSearchUiState.Success(users = previewUsers),
            usersPagingItems = pagingItems,
        )
    }
}
