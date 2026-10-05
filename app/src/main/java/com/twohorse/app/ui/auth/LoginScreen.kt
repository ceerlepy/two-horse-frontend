package com.twohorse.app.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.twohorse.app.Config
import com.twohorse.app.R
import com.twohorse.app.data.api.ApiException as TwoHorseApiException
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.MembershipUser
import com.twohorse.app.i18n.Language
import com.twohorse.app.i18n.currentLanguage
import com.twohorse.app.i18n.setLanguage
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.i18n.Strings
import com.twohorse.app.ui.theme.*
import kotlinx.coroutines.launch

private sealed interface LoginError {
    data object InvalidCredentials : LoginError
    data object EmailPasswordRequired : LoginError
    data object EmailNotVerified : LoginError
    data object NotConfigured : LoginError
    data object Generic : LoginError
    data object GoogleIncomplete : LoginError
    data class GoogleFailed(val code: String) : LoginError
    data object EmailTaken : LoginError
    data object InvalidEmail : LoginError
    data object WeakPassword : LoginError
}

private const val PASSWORD_MIN_LENGTH = 8

@Composable
private fun loginErrorText(
    error: LoginError,
    strings: Strings
): String =
    when (error) {
        LoginError.InvalidCredentials ->
            strings.loginErrorInvalidCredentials

        LoginError.EmailPasswordRequired ->
            strings.loginErrorEmailPasswordRequired

        LoginError.EmailNotVerified ->
            strings.loginErrorEmailNotVerified

        LoginError.NotConfigured ->
            strings.loginErrorNotConfigured

        LoginError.Generic ->
            strings.loginErrorGeneric

        LoginError.GoogleIncomplete ->
            strings.loginErrorGoogleIncomplete

        is LoginError.GoogleFailed ->
            strings.loginErrorGoogleFailed(error.code)

        LoginError.EmailTaken ->
            strings.loginErrorEmailTaken

        LoginError.InvalidEmail ->
            strings.loginErrorInvalidEmail

        LoginError.WeakPassword ->
            strings.loginErrorWeakPassword
    }

private fun loginErrorFromThrowable(
    throwable: Throwable
): LoginError {
    val api = throwable as? TwoHorseApiException

    return when (api?.apiCode) {
        "INVALID_CREDENTIALS" ->
            LoginError.InvalidCredentials

        "EMAIL_AND_PASSWORD_REQUIRED" ->
            LoginError.EmailPasswordRequired

        "GOOGLE_EMAIL_NOT_VERIFIED" ->
            LoginError.EmailNotVerified

        "EMAIL_ALREADY_REGISTERED" ->
            LoginError.EmailTaken

        "INVALID_EMAIL" ->
            LoginError.InvalidEmail

        "WEAK_PASSWORD" ->
            LoginError.WeakPassword

        "GOOGLE_CLIENT_ID_NOT_CONFIGURED",
        "SESSION_JWT_SECRET_NOT_CONFIGURED" ->
            LoginError.NotConfigured

        else ->
            LoginError.Generic
    }
}

@Composable
fun LoginScreen(
    repository: TwoHorseRepository,
    onLoginSuccess: (MembershipUser) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val strings = LocalStrings.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<LoginError?>(null) }

    var registerMode by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf("") }

    val credentialManager =
        remember {
            CredentialManager.create(context)
        }

    fun handleAuthResult(
        result: Result<MembershipUser>
    ) {
        loading = false

        result
            .onSuccess { user ->
                onLoginSuccess(user)
            }
            .onFailure { throwable ->
                error =
                    loginErrorFromThrowable(
                        throwable
                    )
            }
    }

    /*
     * Sign in with Google through Credential Manager (the current
     * Android standard; the old GoogleSignIn API is deprecated). The
     * ID token is audienced to the Web client ID, which the backend
     * verifies against its GOOGLE_CLIENT_ID.
     */
    fun startGoogleSignIn() {
        if (loading) return

        // No Web client ID baked into this build (GitHub variable unset).
        if (Config.GOOGLE_WEB_CLIENT_ID.isBlank()) {
            error = LoginError.NotConfigured
            return
        }

        error = null
        loading = true

        scope.launch {
            val idToken =
                try {
                    val request =
                        GetCredentialRequest.Builder()
                            .addCredentialOption(
                                GetSignInWithGoogleOption
                                    .Builder(
                                        Config.GOOGLE_WEB_CLIENT_ID
                                    )
                                    .build()
                            )
                            .build()

                    val credential =
                        credentialManager
                            .getCredential(
                                context,
                                request
                            )
                            .credential

                    if (
                        credential is CustomCredential &&
                        credential.type ==
                        GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        GoogleIdTokenCredential
                            .createFrom(
                                credential.data
                            )
                            .idToken
                    } else {
                        error = LoginError.GoogleIncomplete
                        null
                    }
                } catch (e: GetCredentialCancellationException) {
                    null
                } catch (e: GetCredentialException) {
                    error =
                        LoginError.GoogleFailed(
                            e.type.substringAfterLast('.')
                        )
                    null
                } catch (e: GoogleIdTokenParsingException) {
                    error = LoginError.GoogleIncomplete
                    null
                }

            if (idToken == null) {
                loading = false
                return@launch
            }

            handleAuthResult(
                repository.loginWithGoogle(
                    idToken
                )
            )
        }
    }

    fun submitEmailForm() {
        if (loading) return

        if (
            email.isBlank() ||
            password.isBlank()
        ) {
            error =
                LoginError.EmailPasswordRequired

            return
        }

        if (
            registerMode &&
            password.length < PASSWORD_MIN_LENGTH
        ) {
            error =
                LoginError.WeakPassword

            return
        }

        loading = true
        error = null

        scope.launch {
            handleAuthResult(
                if (registerMode)
                    repository.register(
                        email.trim(),
                        password,
                        displayName.trim().ifBlank { null }
                    )
                else
                    repository.loginWithPassword(
                        email.trim(),
                        password
                    )
            )
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Bg
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
        ) {
            // Brand header behind the top of the form card.
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Ink, Green)
                            )
                        )
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    LanguageToggle()
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 6.dp
                ) {
                    Image(
                        painter = painterResource(R.drawable.two_horse_logo),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .padding(8.dp)
                                .size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Two Horse",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = strings.loginSubtitle,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = Surface
                        ),
                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        ModeTabs(
                            registerMode = registerMode,
                            enabled = !loading,
                            onSelect = { register ->
                                registerMode = register
                                error = null
                            }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedButton(
                            onClick = { startGoogleSignIn() },
                            enabled = !loading,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Border),
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = Ink
                                )
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_google_g),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = strings.loginGoogleButton,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Border
                            )

                            Text(
                                text = "  ${strings.loginOr}  ",
                                color = Muted,
                                fontSize = 12.sp
                            )

                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Border
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val fieldShape = RoundedCornerShape(14.dp)

                        val fieldColors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Green,
                                unfocusedBorderColor = Border,
                                focusedLeadingIconColor = Green,
                                focusedLabelColor = Green,
                                cursorColor = Green
                            )

                        AnimatedVisibility(visible = registerMode) {
                            Column {
                                OutlinedTextField(
                                    value = displayName,
                                    onValueChange = { displayName = it },
                                    label = { Text(strings.loginNameLabel) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null)
                                    },
                                    singleLine = true,
                                    shape = fieldShape,
                                    colors = fieldColors,
                                    keyboardOptions =
                                        KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(strings.loginEmailLabel) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null)
                            },
                            singleLine = true,
                            shape = fieldShape,
                            colors = fieldColors,
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(strings.loginPasswordLabel) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible }
                                ) {
                                    Icon(
                                        imageVector =
                                            if (passwordVisible) Icons.Default.VisibilityOff
                                            else Icons.Default.Visibility,
                                        contentDescription =
                                            if (passwordVisible) strings.loginHidePassword
                                            else strings.loginShowPassword
                                    )
                                }
                            },
                            supportingText =
                                if (registerMode) {
                                    { Text(strings.loginPasswordHint) }
                                } else null,
                            singleLine = true,
                            shape = fieldShape,
                            colors = fieldColors,
                            visualTransformation =
                                if (passwordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                            keyboardActions =
                                KeyboardActions(
                                    onDone = { submitEmailForm() }
                                ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        error?.let { loginError ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = PaleRed
                                    ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = loginErrorText(loginError, strings),
                                    modifier = Modifier.padding(12.dp),
                                    color = Red,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Button(
                            onClick = { submitEmailForm() },
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
                                    text =
                                        if (registerMode)
                                            strings.loginRegisterButton
                                        else
                                            strings.loginSubmitButton,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                BenefitRow(strings.loginBenefitTrial)
                BenefitRow(strings.loginBenefitSignals)
                BenefitRow(strings.loginBenefitCancel)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = strings.loginLegalNotice,
                    modifier = Modifier.fillMaxWidth(),
                    color = Muted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ModeTabs(
    registerMode: Boolean,
    enabled: Boolean,
    onSelect: (Boolean) -> Unit
) {
    val strings = LocalStrings.current

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Bg)
                .padding(4.dp)
    ) {
        listOf(
            false to strings.loginSubmitButton,
            true to strings.loginRegisterButton
        ).forEach { (isRegister, label) ->
            val selected = registerMode == isRegister

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .background(if (selected) Surface else Color.Transparent)
                        .clickable(enabled = enabled) { onSelect(isRegister) }
                        .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selected) Ink else Muted,
                    fontSize = 14.sp,
                    fontWeight =
                        if (selected) FontWeight.Bold
                        else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BenefitRow(
    text: String
) {
    Row(
        modifier = Modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Green,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            color = Ink,
            fontSize = 13.sp
        )
    }
}

@Composable
fun LanguageToggle(
    modifier: Modifier = Modifier
) {
    var language by remember { mutableStateOf(currentLanguage()) }

    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(50))
                .background(Surface)
                .border(
                    BorderStroke(1.dp, Border),
                    RoundedCornerShape(50)
                )
    ) {
        LanguageToggleOption(
            label = "TR",
            selected = language == Language.TR,
            onClick = {
                language = Language.TR
                setLanguage(Language.TR)
            }
        )

        LanguageToggleOption(
            label = "EN",
            selected = language == Language.EN,
            onClick = {
                language = Language.EN
                setLanguage(Language.EN)
            }
        )
    }
}

@Composable
private fun LanguageToggleOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onClick),
        color = if (selected) Green else Color.Transparent
    ) {
        Text(
            text = label,
            modifier =
                Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                ),
            color = if (selected) Color.White else Muted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
