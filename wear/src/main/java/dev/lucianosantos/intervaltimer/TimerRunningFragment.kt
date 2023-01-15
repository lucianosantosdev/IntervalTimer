package dev.lucianosantos.intervaltimer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.lucianosantos.intervaltimer.databinding.FragmentTimerRunningBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TimerRunningFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerRunningFragment : Fragment() {
    private var _binding: FragmentTimerRunningBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTimerRunningBinding.inflate(inflater, container, false)
        return binding.root
    }
}