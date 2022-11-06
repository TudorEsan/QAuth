package net.theluckycoder.qr.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.theluckycoder.qr.datastore.UserDataStore
import net.theluckycoder.qr.model.UserLoginForm
import net.theluckycoder.qr.network.service.UserService
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userService: UserService,
    val userDataStore: UserDataStore,
) : ViewModel() {

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tokensResponse = userService.login(UserLoginForm(email, password))
            if (tokensResponse.isSuccessful) {
                userDataStore.setTokens(tokensResponse.body()!!)
            } else {
                Log.e("Login", "Failed to log in" + tokensResponse.message())
            }
        }
    }
}
