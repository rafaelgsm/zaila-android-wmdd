package com.zailaapp.zaila.views.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.zailaapp.zaila.R
import com.zailaapp.zaila.data.api.zaila.getApiCode
import com.zailaapp.zaila.data.api.zaila.getApiMessage
import com.zailaapp.zaila.extensions.gone
import com.zailaapp.zaila.extensions.toast
import com.zailaapp.zaila.extensions.vis
import com.zailaapp.zaila.model.UserZaila
import com.zailaapp.zaila.model.responses.LoginResponse
import com.zailaapp.zaila.repositories.AuthRepository
import com.zailaapp.zaila.utils.PreferencesManager
import com.zailaapp.zaila.views.activities.LoginActivity
import com.zailaapp.zaila.views.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_login_options.*
import kotlinx.android.synthetic.main.layout_progress.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LoginOptionsFragment : Fragment() {

    private val navController by lazy { findNavController() }

    companion object {
        private const val RC_SIGN_IN = 321
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login_options, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGoogleSignIn()

        btn_email.setOnClickListener {
            navController.navigate(R.id.actionGoLoginFields)
        }
    }

    //region Google Sign in
    private fun setupGoogleSignIn() {

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))

            .requestProfile()
            .requestEmail()

            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        btn_google.setOnClickListener {

            (requireActivity() as LoginActivity).progress.vis()
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    //endregion Google Sign in

    //region Google Sign in result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        (requireActivity() as LoginActivity).progress.gone()

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                if (account != null && account.idToken != null) {
                    loginAction(account.idToken!!)
                } else {
                    requireActivity().toast(getString(R.string.error_google_token))
                }

            } catch (e: ApiException) {

                requireActivity().toast(getString(R.string.error_google_token))
//                Log.d(TAG", "Google sign in failed", e)
            }
        }
    }
    //endregion Google Sign in result

    //region loginAction Google
    //todo - refactor

    private val authRepository: AuthRepository by inject()

    private fun getLoginActivity(): LoginActivity = (requireActivity() as LoginActivity)

    //region loginAction
    /**
     * If we get a forbidden message from the api, then it means that the user is not registered.
     * We will try then to register it.
     */
    private fun loginAction(idToken: String, secondAttempt: Boolean = false) {

        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {

            requireActivity().runOnUiThread {
                getLoginActivity().progress.vis()
            }

            var result: LoginResponse? = null
            var apiMessage: String? = null
            var shouldRegisterUser = false

            try {

                result = authRepository.loginUserGoogle(idToken)

            } catch (throwable: Throwable) {

                apiMessage =
                    throwable.getApiMessage() //Extension function to read the api response error


                //{"errorCode":403,"errorMessage":"Sign In not allowed. Error: Invalid User"}
                shouldRegisterUser = throwable.getApiCode() == 403
            }

            requireActivity().runOnUiThread {

                getLoginActivity().progress.gone()

                if (apiMessage != null) {

                    //todo - validate error and do registration flow

                    if (shouldRegisterUser) {

                        if (secondAttempt) {
                            Log.d("LoginOptions", "Second Attempt fail!")
                            requireActivity().toast("$apiMessage Try again later.")
                        } else {
                            registerUser(idToken)
                        }

                    } else {
                        //error
                        requireActivity().toast("" + apiMessage)
                    }

                } else {

                    //success
                    if (result?.token != null) {

                        if (result.user != null) {
                            PreferencesManager.saveUser(result.user!!)
                        }

                        PreferencesManager.saveToken(result.token!!)
                        startActivity(MainActivity.newIntent())
                        requireActivity().finish()
                    }

                }

            }

        }

    }
    //endregion loginAction

    //region registerUser
    private fun registerUser(idToken: String) {
        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {

            requireActivity().runOnUiThread {
                getLoginActivity().progress.vis()
            }

            var result: UserZaila? = null
            var apiMessage: String? = null

            try {

                result = authRepository.registerUserGoogle(idToken)

            } catch (throwable: Throwable) {

                apiMessage =
                    throwable.getApiMessage() //Extension function to read the api response error
            }

            requireActivity().runOnUiThread {

                getLoginActivity().progress.gone()

                if (apiMessage != null) {

                    requireActivity().toast("" + apiMessage)

                } else {

                    //success - then try to login with Google again:
                    if (result?.email != null) {
                        loginAction(idToken)
                    } else {
                        requireActivity().toast("An error has occurred.")
                    }

                }

            }

        }
    }
    //endregion registerUser
    //endregion loginAction Google
}