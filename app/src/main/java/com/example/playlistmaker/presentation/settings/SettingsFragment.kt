package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switch_dark_theme)
        val itemShareApp = view.findViewById<LinearLayout>(R.id.item_share_app)
        val itemWriteToSupport = view.findViewById<LinearLayout>(R.id.item_write_to_support)
        val itemUserAgreement = view.findViewById<LinearLayout>(R.id.item_user_agreement)

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            switchTheme.isChecked = state.isDarkTheme
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitched(isChecked)
        val nightMode = if (isChecked) {
            AppCompatDelegate.MODE_NIGHT_YES
            } else {
            AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }

        itemShareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=${requireContext().packageName}")
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }

        itemWriteToSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf("support@playlistmaker.com"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            }
            startActivity(Intent.createChooser(emailIntent, getString(R.string.write_to_support)))
        }

        itemUserAgreement.setOnClickListener {
            val termUrl = getString(R.string.terms_url)
            startActivity(Intent(Intent.ACTION_VIEW, termUrl.toUri()))
        }
    }
}