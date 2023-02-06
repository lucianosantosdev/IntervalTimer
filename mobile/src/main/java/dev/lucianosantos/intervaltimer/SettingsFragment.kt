package dev.lucianosantos.intervaltimer

import android.media.audiofx.Equalizer.Settings
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.databinding.FragmentSettingsBinding
import dev.lucianosantos.intervaltimer.databinding.FragmentTimerRunningBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels {
        SettingsViewModel.Factory(TimerSettingsRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startButton.setOnClickListener {
            val sets = viewModel.uiState.value?.timerSettings?.sections ?: 1
//            val trainTime = viewModel.uiState.value?.timerSettings?.trainTimeSeconds ?: 60
            val trainTime = 5
            val restTime = viewModel.uiState.value?.timerSettings?.restTimeSeconds ?: 60
            val action = SettingsFragmentDirections.actionSettingsFragmentToTimerRunningFragment(sets, trainTime, restTime)

            findNavController().navigate(action)
        }
    }
}