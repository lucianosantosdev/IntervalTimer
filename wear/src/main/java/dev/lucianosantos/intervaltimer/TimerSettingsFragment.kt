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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startButton.setOnClickListener {
            val sets = binding.setsNumberEditText.text.toString().toInt()
            val trainTime = binding.trainTimeEditText.text.toString().toLong()
            val restTime = binding.restTimeEditText.text.toString().toLong()
            val action =
                TimerSettingsFragmentDirections.
                actionTimerSettingsFragmentToTimerRunningFragment(sets, trainTime, restTime)
            findNavController().navigate(action)
        }
    }
}