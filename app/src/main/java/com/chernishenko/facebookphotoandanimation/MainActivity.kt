package com.chernishenko.facebookphotoandanimation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.chernishenko.facebookphotoandanimation.databinding.ActivityMainBinding
import com.chernishenko.facebookphotoandanimation.fragment.LoginFragment
import com.chernishenko.facebookphotoandanimation.fragment.MainFragment
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModel
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModelFactory
import com.facebook.AccessToken

class MainActivity : AppCompatActivity() {

    private val factory by lazy { MainViewModelFactory() }
    private val viewModel by lazy { ViewModelProvider(this, factory).get(MainViewModel::class.java) }

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
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_fragment_container, MainFragment(), MainFragment.TAG)
                    .commit()
            }
        } else {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fl_fragment_container, LoginFragment(), LoginFragment.TAG)
                .commit()
        }
    }
}