package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.data.TimerViewModel
import dev.lucianosantos.intervaltimer.databinding.FragmentTimerRunningBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TimerRunningFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerRunningFragment : Fragment() {
    private val TAG = javaClass.name

    private var _binding: FragmentTimerRunningBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimerViewModel by activityViewModels {
        TimerViewModel.Factory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerRunningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) {
            Log.d(TAG, "uiState changed!")
            binding.timerTextView.text = it.currentTime
            setBackgroundColor(it.timerState)
            setTextView(it.timerState)
        }
        viewModel.startTimer()
    }

    private fun setBackgroundColor(state: TimerState) {
        val color = when(state) {
            TimerState.PREPARE -> R.color.yellow
            TimerState.TRAIN -> R.color.green
            TimerState.REST -> R.color.red
            TimerState.FINISHED -> R.color.blue
        }
        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun setTextView(state: TimerState) {
        val stringId = when(state) {
            TimerState.PREPARE -> R.string.state_prepare_text
            TimerState.TRAIN -> R.string.state_train_text
            TimerState.REST -> R.string.state_rest_text
            TimerState.FINISHED -> R.string.state_finished_text
        }
        binding.stateTextView.text = requireContext().getString(stringId)
    }
}