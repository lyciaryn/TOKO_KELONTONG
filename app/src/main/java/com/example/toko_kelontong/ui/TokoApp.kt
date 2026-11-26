package com.example.toko_kelontong.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.toko_kelontong.AppContainer
import com.example.toko_kelontong.data.local.Session
import com.example.toko_kelontong.ui.auth.AuthViewModel
import com.example.toko_kelontong.ui.auth.LoginScreen
import com.example.toko_kelontong.ui.auth.RegisterScreen
import com.example.toko_kelontong.ui.common.LoadingView
import com.example.toko_kelontong.ui.home.HomeScreen
import com.example.toko_kelontong.ui.product.ProductListScreen
import com.example.toko_kelontong.ui.product.ProductViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PRODUCTS = "products"
    // Tambahkan: PRODUCT_FORM, CASHIER, TRANSACTIONS, CATEGORIES, CASHIER_MANAGEMENT, PROFILE
}

@Composable
fun TokoApp(container: AppContainer) {
    // Tunggu sesi terbaca dari DataStore agar start destination tepat (tanpa kedipan layar login).
    val initialSession by produceState<Session?>(initialValue = null) {
        value = container.sessionManager.session.first()
    }

    val start = initialSession ?: run {
        LoadingView()
        return
    }

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val session by container.sessionManager.session
        .collectAsStateWithLifecycle(initialValue = start)

    // Token hilang (logout atau 401 dari server) -> paksa kembali ke Login.
    LaunchedEffect(navController) {
        container.sessionManager.session
            .map { it.isLoggedIn }
            .distinctUntilChanged()
            .collect { loggedIn ->
                val route = navController.currentDestination?.route
                if (!loggedIn && route != null && route != Routes.LOGIN && route != Routes.REGISTER) {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) }
                }
            }
    }

    NavHost(
        navController = navController,
        startDestination = if (start.isLoggedIn) Routes.HOME else Routes.LOGIN,
    ) {
        composable(Routes.LOGIN) {
            val vm: AuthViewModel = viewModel(factory = viewModelFactory {
                initializer { AuthViewModel(container.authRepository) }
            })
            LoginScreen(
                viewModel = vm,
                onSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(0) }
                },
                onGoToRegister = { navController.navigate(Routes.REGISTER) },
            )
        }
        composable(Routes.REGISTER) {
            val vm: AuthViewModel = viewModel(factory = viewModelFactory {
                initializer { AuthViewModel(container.authRepository) }
            })
            RegisterScreen(
                viewModel = vm,
                onSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(0) }
                },
                onBackToLogin = { navController.popBackStack() },
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                session = session,
                onOpenProducts = { navController.navigate(Routes.PRODUCTS) },
                onLogout = {
                    scope.launch {
                        container.authRepository.logout()
                        // Navigasi ke Login ditangani LaunchedEffect di atas.
                    }
                },
            )
        }
        composable(Routes.PRODUCTS) {
            val vm: ProductViewModel = viewModel(factory = viewModelFactory {
                initializer { ProductViewModel(container.productRepository) }
            })
            ProductListScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}
