package com.twohorse.app.ui.auth

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.twohorse.app.Config
import com.twohorse.app.data.api.ApiException as TwoHorseApiException
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.MembershipUser
import com.twohorse.app.ui.theme.*
import kotlinx.coroutines.launch

private fun loginErrorMessage(
    throwable: Throwable
): String {
    val api = throwable as? TwoHorseApiException

    return when (api?.apiCode) {
        "INVALID_CREDENTIALS" ->
            "E-posta veya şifre hatalı."

        "EMAIL_AND_PASSWORD_REQUIRED" ->
            "E-posta ve şifre gerekli."

        "GOOGLE_EMAIL_NOT_VERIFIED" ->
            "Google hesabının e-postası doğrulanmamış."

        "GOOGLE_CLIENT_ID_NOT_CONFIGURED",
        "SESSION_JWT_SECRET_NOT_CONFIGURED" ->
            "Giriş sistemi backend'de henüz tam ayarlanmadı. Birazdan tekrar dene."

        else ->
            "Giriş yapılamadı. Bağlantını kontrol edip tekrar dene."
    }
}

@Composable
fun LoginScreen(
    repository: TwoHorseRepository,
    onLoginSuccess: (MembershipUser) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val googleSignInClient =
        remember {
            GoogleSignIn.getClient(
                context,
                GoogleSignInOptions
                    .Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                    )
                    .requestIdToken(
                        Config.GOOGLE_WEB_CLIENT_ID
                    )
                    .requestEmail()
                    .build()
            )
        }

    val googleLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode != RESULT_OK) {
                return@rememberLauncherForActivityResult
            }

            val task =
                GoogleSignIn.getSignedInAccountFromIntent(
                    result.data
                )

            try {
                val account =
                    task.getResult(
                        ApiException::class.java
                    )

                val idToken =
                    account?.idToken

                if (idToken == null) {
                    error =
                        "Google girişi tamamlanamadı."

                    return@rememberLauncherForActivityResult
                }

                loading = true
                error = null

                scope.launch {
                    repository
                        .loginWithGoogle(
                            idToken
                        )
                        .onSuccess { user ->
                            loading = false
                            onLoginSuccess(user)
                        }
                        .onFailure { throwable ->
                            loading = false
                            error =
                                loginErrorMessage(
                                    throwable
                                )
                        }
                }
            } catch (e: ApiException) {
                error =
                    "Google girişi başarısız (${e.statusCode})."
            }
        }

    fun submitPasswordLogin() {
        if (loading) return

        if (
            email.isBlank() ||
            password.isBlank()
        ) {
            error =
                "E-posta ve şifre gerekli."

            return
        }

        loading = true
        error = null

        scope.launch {
            repository
                .loginWithPassword(
                    email.trim(),
                    password
                )
                .onSuccess { user ->
                    loading = false
                    onLoginSuccess(user)
                }
                .onFailure { throwable ->
                    loading = false
                    error =
                        loginErrorMessage(
                            throwable
                        )
                }
        }
    }

    Scaffold(
        containerColor = Bg
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
            verticalArrangement =
                Arrangement.Center
        ) {
            Surface(
                color = Green,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Two Horse",
                color = Ink,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Devam etmek için giriş yap",
                color = Muted,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    error = null
                    googleLauncher.launch(
                        googleSignInClient.signInIntent
                    )
                },
                enabled = !loading,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Ink
                    )
            ) {
                Text(
                    text = "Google ile Giriş Yap",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Border
                )

                Text(
                    text = "  veya  ",
                    color = Muted,
                    fontSize = 11.sp
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Border
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-posta") },
                singleLine = true,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Şifre") },
                singleLine = true,
                visualTransformation =
                    PasswordVisualTransformation(),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            error?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = PaleRed
                        ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp),
                        color = Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            Button(
                onClick = { submitPasswordLogin() },
                enabled = !loading,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Green
                    )
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Giriş Yap",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
