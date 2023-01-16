package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.databinding.FragmentTimerSettingsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TimerSettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerSettingsFragment : Fragment() {

    private var _binding : FragmentTimerSettingsBinding? = null
    private val binding get() = _binding!!

    private var beepTone = -1

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