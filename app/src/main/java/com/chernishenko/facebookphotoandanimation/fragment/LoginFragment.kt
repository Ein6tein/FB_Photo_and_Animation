package com.chernishenko.facebookphotoandanimation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.chernishenko.facebookphotoandanimation.R
import com.chernishenko.facebookphotoandanimation.databinding.FragmentLoginBinding
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult

class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    private val viewModel by activityViewModels<MainViewModel>()
    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setPermissions("public_profile", "user_photos")
        binding.loginButton.fragment = this
        binding.loginButton.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        viewModel.retrieveImageUrl(it.accessToken) {
                            parentFragmentManager.commit {
                                replace(R.id.fl_fragment_container, MainFragment(), MainFragment.TAG)
                            }
                        }
                    }
                }

                override fun onCancel() {
                    Log.d("Something", "went wrong")
                }

                override fun onError(error: FacebookException?) {
                    Log.e("Something", "went wrong", error)
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}