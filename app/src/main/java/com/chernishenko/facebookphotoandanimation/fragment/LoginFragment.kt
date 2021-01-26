package com.chernishenko.facebookphotoandanimation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chernishenko.facebookphotoandanimation.R
import com.chernishenko.facebookphotoandanimation.databinding.FragmentLoginBinding
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModel
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModelFactory
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult

class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    private val factory by lazy { MainViewModelFactory() }
    private val viewModel by lazy { ViewModelProvider(requireActivity(), factory).get(MainViewModel::class.java) }
    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setPermissions("public_profile")
        binding.loginButton.fragment = this
        binding.loginButton.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        viewModel.retrieveImageUrl(it.accessToken) {
                            activity
                                ?.supportFragmentManager
                                ?.beginTransaction()
                                ?.replace(R.id.fl_fragment_container, MainFragment(), MainFragment.TAG)
                                ?.commit()
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