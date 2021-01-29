package com.chernishenko.facebookphotoandanimation.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.chernishenko.facebookphotoandanimation.R
import com.chernishenko.facebookphotoandanimation.databinding.FragmentMainBinding
import com.chernishenko.facebookphotoandanimation.module.GlideApp
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModel
import com.facebook.AccessToken

class MainFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"

        private val CIRCLES = listOf(
            R.drawable.circle_red,
            R.drawable.circle_yellow,
            R.drawable.circle_green,
            R.drawable.circle_blue
        )
    }

    private val viewModel by activityViewModels<MainViewModel>()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setupViewModel()
        binding.btnChangePic.setOnClickListener {
            viewModel.retrieveUserAlbums(AccessToken.getCurrentAccessToken()) {
                parentFragmentManager.commit {
                    addToBackStack(AlbumSelectionFragment.TAG)
                    replace(R.id.fl_fragment_container, AlbumSelectionFragment(), AlbumSelectionFragment.TAG)
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel.url.observe(viewLifecycleOwner) {
            loadPhoto(it)
        }
    }

    private fun loadPhoto(photo: String) =
        GlideApp
            .with(this)
            .load(photo)
            .circleCrop()
            .addListener(object : RequestListener<Drawable> {

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    viewModel.loading.invoke(false)
                    animatePulse(0)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Log.e("Something", "wrong", e)
                    return false
                }
            })
            .into(binding.ivProfilePic)

    private fun animatePulse(position: Int) {
        when (position % 2) {
            0 -> {
                animate(binding.ivCircle, position)
            }
            1 -> {
                animate(binding.ivCircle2, position)
            }
        }
    }

    private fun animate(view: ImageView, position: Int) {
        view.setBackgroundResource(CIRCLES[position])
        view
            .animate()
            .setDuration(1500)
            .scaleX(8f)
            .scaleY(8f)
            .alpha(0f)
            .setInterpolator(AccelerateInterpolator(.25f))
            .withEndAction {
                view.scaleX = 1f
                view.scaleY = 1f
                view.alpha = 1f
            }

        if (isVisible) {
            handler.postDelayed({
                animatePulse(if ((position + 1) == 4) 0 else position + 1)
            }, 1000)
        }
    }
}