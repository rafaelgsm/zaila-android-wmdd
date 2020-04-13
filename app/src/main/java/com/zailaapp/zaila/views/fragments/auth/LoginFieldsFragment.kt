package com.zailaapp.zaila.views.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zailaapp.zaila.R
import com.zailaapp.zaila.data.api.zaila.getApiMessage
import com.zailaapp.zaila.extensions.*
import com.zailaapp.zaila.model.requests.UserLoginRequest
import com.zailaapp.zaila.model.responses.LoginResponse
import com.zailaapp.zaila.repositories.AuthRepository
import com.zailaapp.zaila.utils.PreferencesManager
import com.zailaapp.zaila.views.activities.LoginActivity
import com.zailaapp.zaila.views.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_login_fields.*
import kotlinx.android.synthetic.main.layout_progress.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LoginFieldsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login_fields, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_confirm_login.setOnClickListener {
            loginAction()
        }

        btn_back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //region focus change config
        edt_password.onDone { loginAction() }
        //endregion focus change config
    }

    //region loginAction
    //todo - refactor

    private val authRepository: AuthRepository by inject()

    private fun getLoginActivity(): LoginActivity = (requireActivity() as LoginActivity)

    private fun loginAction() {

        if (!edt_email.isValidEmail() || !edt_password.isValidPassword()) {
            requireActivity().toast("Please fill in the fields to login.")
            return
        }

        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {

            requireActivity().runOnUiThread {
                getLoginActivity().progress.vis()
            }

            var result: LoginResponse? = null
            var apiMessage: String? = null

            try {

                result = authRepository.loginUser(
                    UserLoginRequest(
                        edt_email.text.toString(),
                        edt_password.text.toString().toSha256HexDigest()
                    )
                )

            } catch (throwable: Throwable) {

                apiMessage =
                    throwable.getApiMessage() //Extension function to read the api response error

            }

            requireActivity().runOnUiThread {

                getLoginActivity().progress.gone()

                if (apiMessage != null) {

                    //error
                    requireActivity().toast("" + apiMessage)

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
}