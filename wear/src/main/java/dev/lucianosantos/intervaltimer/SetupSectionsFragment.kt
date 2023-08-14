package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.utils.getMinutesFromSeconds
import dev.lucianosantos.intervaltimer.core.utils.getSecondsFromSeconds
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.databinding.FragmentSetupSectionsBinding
import kotlinx.coroutines.launch

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect{
                    binding.editTextNumber.text = it.timerSettings.sections.toString()
                }
            }
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