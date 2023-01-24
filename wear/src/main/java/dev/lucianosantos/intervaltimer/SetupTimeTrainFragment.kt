package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.databinding.FragmentSetupTimeTrainBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SetupTrainTimeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupTimeTrainFragment : Fragment() {
    private var _binding : FragmentSetupTimeTrainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels {
        SettingsViewModel.Factory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetupTimeTrainBinding.inflate(layoutInflater, container, false)
        NumberPickerHelper().setupNumberPickersFocus(listOf(
            binding.trainTimeMinutesNumberPicker,
            binding.trainTimeSecondsNumberPicker,
        ))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            setupTrainTime()
            findNavController().navigate(R.id.action_setupTimeTrainFragment_to_setupTimeRestFragment)
        }
    }

    private fun setupTrainTime() {
        val seconds = NumberPickerHelper().getTimeSeconds(binding.trainTimeMinutesNumberPicker, binding.trainTimeSecondsNumberPicker)
        viewModel.setTrainTime(seconds)
    }
}