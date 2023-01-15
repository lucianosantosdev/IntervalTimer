package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.core.data.TimerViewModel
import dev.lucianosantos.intervaltimer.databinding.FragmentTimerSettingsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TimerSettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerSettingsFragment : Fragment() {

    private var _binding : FragmentTimerSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimerViewModel by activityViewModels {
        TimerViewModel.Factory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTimerSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_timerSettingsFragment_to_timerRunningFragment)
        }
    }
}