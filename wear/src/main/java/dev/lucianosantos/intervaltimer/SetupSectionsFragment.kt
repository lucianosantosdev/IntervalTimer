package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.core.data.SettingsViewModel
import dev.lucianosantos.intervaltimer.databinding.FragmentSetupSectionsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SetupSectionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupSectionsFragment : Fragment() {
    private var _binding : FragmentSetupSectionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels {
        SettingsViewModel.Factory()
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
            binding.editTextNumber.setText(it.timerSettings.sets.toString())
        }

        binding.sectionMinusButton.setOnClickListener {
            viewModel.decrementSections()
        }

        binding.sectionPlusButton.setOnClickListener {
            viewModel.incrementSections()
        }

        binding.editTextNumber.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val number = v?.text.toString().toInt()
                    viewModel.setSections(number)
                }
                return false
            }
        })

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_setupSectionsFragment_to_setupTimeTrainFragment)
        }
    }
}