package com.chernishenko.facebookphotoandanimation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.chernishenko.facebookphotoandanimation.databinding.ActivityMainBinding
import com.chernishenko.facebookphotoandanimation.fragment.LoginFragment
import com.chernishenko.facebookphotoandanimation.fragment.MainFragment
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModel
import com.facebook.AccessToken

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        viewModel.loading = { isVisible ->
            binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
        val currentAccessToken = AccessToken.getCurrentAccessToken()
        if (currentAccessToken != null) {
            viewModel.retrieveImageUrl(currentAccessToken) {
                supportFragmentManager.commit {
                    add(R.id.fl_fragment_container, MainFragment(), MainFragment.TAG)
                }
            }
        } else {
            supportFragmentManager.commit {
                add(R.id.fl_fragment_container, LoginFragment(), LoginFragment.TAG)
            }
        }
    }
}