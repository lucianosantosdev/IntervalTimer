package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.ui.NumberPickerHelper
import dev.lucianosantos.intervaltimer.core.utils.getMinutesFromSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromMinutesAndSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromSeconds
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.databinding.FragmentSettingsBinding

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

        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.editTextNumber.text = it.timerSettings.sections.toString()

            binding.trainTimeMinutesNumberPicker.value = getMinutesFromSeconds(it.timerSettings.trainTimeSeconds)
            binding.trainTimeSecondsNumberPicker.value = getSecondsFromSeconds(it.timerSettings.trainTimeSeconds)
            binding.restTimeMinutesNumberPicker.value = getMinutesFromSeconds(it.timerSettings.restTimeSeconds)
            binding.restTimeSecondsNumberPicker.value = getSecondsFromSeconds(it.timerSettings.restTimeSeconds)
        }

        setupSectionPicker()
        setupTimePickers()

        binding.startButton.setOnClickListener {
            val sets = viewModel.uiState.value?.timerSettings?.sections ?: 1
            val trainTime = viewModel.uiState.value?.timerSettings?.trainTimeSeconds ?: 60
            val restTime = viewModel.uiState.value?.timerSettings?.restTimeSeconds ?: 60
            val action = SettingsFragmentDirections.actionSettingsFragmentToTimerRunningFragment(sets = sets, trainTime = trainTime, restTime = restTime)

            findNavController().navigate(action)
        }
    }

    private fun setupSectionPicker() {
        binding.sectionMinusButton.setOnClickListener {
            viewModel.decrementSections()
        }

        binding.sectionPlusButton.setOnClickListener {
            viewModel.incrementSections()
        }

        binding.trainMinusButton.setOnClickListener {
            viewModel.decrementTrainTime()
        }

        binding.trainPlusButton.setOnClickListener {
            viewModel.incrementTrainTime()
        }

        binding.restMinusButton.setOnClickListener {
            viewModel.decrementRestTime()
        }

        binding.restPlusButton.setOnClickListener {
            viewModel.incrementRestTime()
        }
    }

    private fun setupTimePickers() {
        NumberPickerHelper().setupNumberPickersFocus(listOf(
            binding.restTimeMinutesNumberPicker,
            binding.restTimeSecondsNumberPicker,
            binding.trainTimeMinutesNumberPicker,
            binding.trainTimeSecondsNumberPicker,
        ))

        binding.trainTimeSecondsNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            viewModel.setTrainTime(getTrainSeconds())
        }
        binding.trainTimeMinutesNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            viewModel.setTrainTime(getTrainSeconds())
        }

        binding.restTimeSecondsNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            viewModel.setRestTime(getRestSeconds())
        }
        binding.restTimeMinutesNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            viewModel.setRestTime(getRestSeconds())
        }
    }

    private fun getTrainSeconds() : Int {
        val seconds = binding.trainTimeSecondsNumberPicker.value
        val minutes = binding.trainTimeMinutesNumberPicker.value
        return getSecondsFromMinutesAndSeconds(minutes, seconds)
    }

    private fun getRestSeconds() : Int {
        val seconds = binding.restTimeSecondsNumberPicker.value
        val minutes = binding.restTimeMinutesNumberPicker.value
        return getSecondsFromMinutesAndSeconds(minutes, seconds)
    }

}