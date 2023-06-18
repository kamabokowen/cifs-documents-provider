package com.wa2c.android.cifsdocumentsprovider.presentation.ui.folder

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wa2c.android.cifsdocumentsprovider.domain.model.CifsFile
import com.wa2c.android.cifsdocumentsprovider.presentation.R
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.AppSnackbar
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.AppSnackbarHost
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.AppTopAppBarColors
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.DividerNormal
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.DividerThin
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.LoadingIconButton
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.MessageSnackbarVisual
import com.wa2c.android.cifsdocumentsprovider.presentation.ui.common.Theme

/**
 * Folder Screen
 */
@Composable
fun FolderScreen(
    viewModel: FolderViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onNavigateBack: () -> Unit,
    onNavigateSet: (CifsFile) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val fileList = viewModel.fileList.collectAsStateWithLifecycle()
    val currentFile = viewModel.currentFile.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()

    FolderScreenContainer(
        snackbarHostState = snackbarHostState,
        fileList = fileList.value,
        currentFile = currentFile.value,
        isLoading = isLoading.value,
        onClickBack = onNavigateBack,
        onClickReload = { viewModel.onClickReload() },
        onClickItem = { viewModel.onSelectFolder(it) },
        onClickSet = { viewModel.currentFile.value?.let { onNavigateSet(it) } },
    )
}

/**
 * Folder Screen container
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreenContainer(
    snackbarHostState: SnackbarHostState,
    fileList: List<CifsFile>,
    currentFile: CifsFile?,
    isLoading: Boolean,
    onClickBack: () -> Unit,
    onClickReload: () -> Unit,
    onClickItem: (CifsFile) -> Unit,
    onClickSet: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.host_title)) },
                colors = AppTopAppBarColors(),
                actions = {
                    LoadingIconButton(
                        contentDescription = stringResource(id = R.string.folder_reload_button),
                        isLoading = isLoading,
                        onClick = onClickReload,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "")
                    }
                },
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.weight(weight = 1f, fill = true)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    LazyColumn {
                        items(items = fileList) { file ->
                            FolderItem(
                                cifsFile = file,
                                onClick = { onClickItem(file) },
                            )
                            DividerThin()
                        }
                    }
                }
            }

            DividerNormal()

            Column(
                modifier = Modifier
                    .padding(Theme.SizeS),
            ) {
                Text(
                    text = currentFile?.uri?.toString() ?: "",
                    maxLines = 1,
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                )
                Button(
                    onClick = onClickSet,
                    shape = RoundedCornerShape(Theme.SizeSS),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Theme.SizeS)
                ) {
                    Text(text = stringResource(id = R.string.folder_set))
                }
            }
        }
    }
}

@Composable
private fun FolderItem(
    cifsFile: CifsFile,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.SizeS, vertical = Theme.SizeSS)
            .clickable(enabled = true, onClick = onClick)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_folder),
            "Folder",
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = cifsFile.name,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = Theme.SizeS)
        )

    }
}

/**
 * Preview
 */
@Preview(
    name = "Preview",
    group = "Group",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun FolderScreenContainerPreview() {
    Theme.AppTheme {
        FolderScreenContainer(
            snackbarHostState = SnackbarHostState(),
            fileList = listOf(
                CifsFile(
                    name = "example1.txt",
                    uri = Uri.parse("smb://example/"),
                    size = 128,
                    lastModified = 0,
                    isDirectory = true,
                ),
                CifsFile(
                    name = "example2example2example2example2example2example2.txt",
                    uri = Uri.parse("smb://example/"),
                    size = 128,
                    lastModified = 0,
                    isDirectory = true,
                ),
                CifsFile(
                    name = "example3example3example3example3example3example3example3example3example3example3.txt",
                    uri = Uri.parse("smb://example/"),
                    size = 128,
                    lastModified = 0,
                    isDirectory = true,
                )
            ),
            currentFile = CifsFile(
                name = "",
                uri = Uri.parse("smb://example/test"),
                size = 0,
                lastModified = 0,
                isDirectory = true,
            ),
            isLoading = false,
            onClickBack = {},
            onClickReload = {},
            onClickItem = {},
            onClickSet = {},
        )
    }
}
