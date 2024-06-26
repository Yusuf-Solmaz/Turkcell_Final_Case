package com.yusuf.yusuf_mucahit_solmaz_final.presentation.drawer.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.yusuf.yusuf_mucahit_solmaz_final.R
import com.yusuf.yusuf_mucahit_solmaz_final.data.remoteconfig.RemoteConfigManager
import com.yusuf.yusuf_mucahit_solmaz_final.data.remoteconfig.RemoteConfigManager.loadBackgroundColor
import com.yusuf.yusuf_mucahit_solmaz_final.data.remoteconfig.RemoteConfigManager.saveBackgroundColor
import com.yusuf.yusuf_mucahit_solmaz_final.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnColorRed.setOnClickListener { updateBackgroundColor("#FF5733", view) }
        binding.btnColorGreen.setOnClickListener { updateBackgroundColor("#4CAF50", view) }
        binding.btnColorBlue.setOnClickListener { updateBackgroundColor("#2196F3", view) }
        binding.btnColorYellow.setOnClickListener { updateBackgroundColor("#FFEB3B", view) }
        binding.btnColorWhite.setOnClickListener { updateBackgroundColor("#FFFFFF", view) }

        binding.btnLanguageTurkish.setOnClickListener { updateLocale("tr") }
        binding.btnLanguageEnglish.setOnClickListener { updateLocale("en") }
    }

    private fun updateBackgroundColor(color: String, view: View) {
        RemoteConfigManager.setBackgroundColor(color) { success ->
            if (success) {
                saveBackgroundColor(color, requireContext())
                loadBackgroundColor(requireContext()) { color ->
                    view.setBackgroundColor(Color.parseColor(color))
                }
                Toast.makeText(context, requireContext().getString(R.string.background_color_updated), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, requireContext().getString(R.string.background_color_update_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadBackgroundColor(requireContext()) { color ->
            view?.setBackgroundColor(Color.parseColor(color))
        }
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)


        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.create(Locale.forLanguageTag(languageCode))
        )

        val sharedPref = requireActivity().getSharedPreferences("com.yusuf.yusuf_mucahit_solmaz_final", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("SELECTED_LANGUAGE", languageCode)
            apply()
        }

        requireActivity().recreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
