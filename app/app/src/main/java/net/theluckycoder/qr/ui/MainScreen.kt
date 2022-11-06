package net.theluckycoder.qr.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.runBlocking
import net.theluckycoder.qr.ui.tabs.BottomTab
import net.theluckycoder.qr.ui.tabs.CreateReservationTab
import net.theluckycoder.qr.ui.tabs.ReservationsTab
import net.theluckycoder.qr.viewmodel.MainViewModel

object MainScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        val viewModel = viewModel<MainViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val isFabVisible by viewModel.showFab.collectAsState()
        val tokens by viewModel.userDataStore.tokens.collectAsState(initial = null)
        val refreshing by viewModel.refreshing.collectAsState()

        LaunchedEffect(tokens) {
            if (tokens == null)
                navigator.push(LoginScreen())
        }

        TabNavigator(ReservationsTab) {
            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = refreshing)

            SwipeRefresh(
                modifier = Modifier.fillMaxSize(),
                state = swipeRefreshState,
                onRefresh = { viewModel.refresh() }) {
                androidx.compose.material.Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom)),
                    floatingActionButtonPosition = androidx.compose.material.FabPosition.Center,
                    isFloatingActionButtonDocked = true,
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = isFabVisible,
                            enter = scaleIn(),
                            exit = scaleOut(),
                        ) {
                            FloatingActionButton(onClick = {
                                navigator.push(ScannerScreen())
                            }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            }
                        }
                    },
                    topBar = {
                        AnimatedVisibility(
                            isFabVisible,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            SmallTopAppBar(
                                title = { Text("Rezervările tale") },
                                actions = {
                                    IconButton(onClick = {
                                        runBlocking {
                                            viewModel.userDataStore.setTokens(null)
                                        }
                                        navigator.push(LoginScreen())
                                    }) {
                                        Icon(Icons.Default.Logout, contentDescription = null)
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            TabNavigationItem(ReservationsTab)
                            TabNavigationItem(CreateReservationTab)
                        }
                    },
                ) { paddingValues ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
//                BottomSheetNavigator {
                        CurrentTab()
//                }

//                DefaultSnackbar(
//                    modifier = Modifier.align(Alignment.BottomCenter),
//                    snackbarHostState = snackbarHostState,
//                    onDismiss = {
//                        snackbarHostState.currentSnackbarData?.dismiss()
//                    }
//                )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: BottomTab) {
    val tabNavigator = LocalTabNavigator.current
    val selected = tabNavigator.current == tab

    NavigationBarItem(
        selected = selected,
        onClick = { tabNavigator.current = tab },
        label = { Text(tab.options.title) },
        icon = {
            Icon(
                painter = if (selected) tab.selectedIcon else tab.options.icon!!,
                contentDescription = tab.options.title
            )
        }
    )
}
