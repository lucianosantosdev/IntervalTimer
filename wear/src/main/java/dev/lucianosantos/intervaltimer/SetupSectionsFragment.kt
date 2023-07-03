package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.wear.R
import dev.lucianosantos.intervaltimer.wear.databinding.FragmentSetupSectionsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SetupSectionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupSectionsFragment : Fragment() {
    private var _binding : FragmentSetupSectionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels {
        SettingsViewModel.Factory(TimerSettingsRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSetupSectionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.editTextNumber.text = it.timerSettings.sections.toString()
        }

        binding.sectionMinusButton.setOnClickListener {
            viewModel.decrementSections()
        }

        binding.sectionPlusButton.setOnClickListener {
            viewModel.incrementSections()
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_setupSectionsFragment_to_setupTimeTrainFragment)
        }
    }
}