package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.lucianosantos.intervaltimer.R
import dev.lucianosantos.intervaltimer.databinding.FragmentTimerFinishedBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TimerFinishedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerFinishedFragment : Fragment() {
    private var _binding: FragmentTimerFinishedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToBeginButton.setOnClickListener {
            findNavController().navigate(R.id.action_timerFinishedFragment_to_setupSectionsFragment)
        }

        binding.restartButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}