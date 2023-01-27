package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.utils.getMinutesFromSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromMinutesAndSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromSeconds
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.databinding.FragmentSetupTimeRestBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SetupRestTimeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupTimeRestFragment : Fragment() {

    private var _binding : FragmentSetupTimeRestBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels {
        SettingsViewModel.Factory(TimerSettingsRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetupTimeRestBinding.inflate(layoutInflater, container, false)
        NumberPickerHelper().setupNumberPickersFocus(listOf(
            binding.restTimeMinutesNumberPicker,
            binding.restTimeSecondsNumberPicker,
        ))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.restTimeMinutesNumberPicker.value = getMinutesFromSeconds(it.timerSettings.restTimeSeconds)
            binding.restTimeSecondsNumberPicker.value = getSecondsFromSeconds(it.timerSettings.restTimeSeconds)
        }

        binding.startButton.setOnClickListener {
            setupRestTime()

            val sets = viewModel.uiState.value?.timerSettings?.sections ?: 1
            val trainTime = viewModel.uiState.value?.timerSettings?.trainTimeSeconds ?: 60
            val restTime = viewModel.uiState.value?.timerSettings?.restTimeSeconds ?: 60
            val action = SetupTimeRestFragmentDirections.actionSetupTimeRestFragmentToTimerRunningFragment(sets, trainTime, restTime)

            findNavController().navigate(action)
        }
    }

    private fun setupRestTime() {
            val seconds = getSecondsFromMinutesAndSeconds(binding.restTimeMinutesNumberPicker.value, binding.restTimeSecondsNumberPicker.value)
        viewModel.setRestTime(seconds)
    }
}