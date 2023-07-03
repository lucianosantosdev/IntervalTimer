package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.viewmodels.TimerViewModel
import dev.lucianosantos.intervaltimer.mobile.R
import dev.lucianosantos.intervaltimer.mobile.databinding.FragmentTimerRunningBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TimerRunningFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerRunningFragment : Fragment() {
    private var _binding: FragmentTimerRunningBinding? = null
    private val binding get() = _binding!!

    private val arguments by navArgs<TimerRunningFragmentArgs>()

    private val viewModel: TimerViewModel by viewModels {
        TimerViewModel.Factory(
            countDownTimerHelper = CountDownTimerHelper(),
            beepHelper = AlertUserHelper(requireContext()),
            timerSettings = TimerSettings(
                sections = arguments.sets,
                prepareTimeSeconds = DefaultTimerSettings.settings.prepareTimeSeconds,
                trainTimeSeconds = arguments.trainTime,
                restTimeSeconds = arguments.restTime
            )
        )
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
            if (it.timerState == TimerState.FINISHED) {
                findNavController().navigate(R.id.action_timerRunningFragment_to_timerFinishedFragment)
            } else {
                binding.timerTextView.text = it.currentTime
                setBackgroundColor(it.timerState)
                setStateTextView(it.timerState)
                binding.remainingSectionsTextView.text = it.remainingSections.toString()
            }
        }
        viewModel.startTimer()

        binding.pauseButton.setOnClickListener {
            pause()
        }

        binding.stopButton.setOnClickListener {
            stop()
        }

        binding.resumeButton.setOnClickListener {
            resume()
        }
    }

    private fun pause() {
        binding.pauseButton.visibility = View.INVISIBLE
        binding.stopButton.visibility = View.VISIBLE
        binding.resumeButton.visibility = View.VISIBLE
        viewModel.pauseTimer()
    }

    private fun resume() {
        binding.stopButton.visibility = View.INVISIBLE
        binding.resumeButton.visibility = View.INVISIBLE
        binding.pauseButton.visibility = View.VISIBLE
        viewModel.resumeTimer()
    }

    private fun stop() {
        findNavController().navigateUp()
    }

    private fun setBackgroundColor(state: TimerState) {
        val color = when(state) {
            TimerState.PREPARE -> R.color.orange
            TimerState.TRAIN -> R.color.green
            TimerState.REST -> R.color.blue
            TimerState.FINISHED -> R.color.blue
        }
        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun setStateTextView(state: TimerState) {
        val stringId = when(state) {
            TimerState.PREPARE -> R.string.state_prepare_text
            TimerState.TRAIN -> R.string.state_train_text
            TimerState.REST -> R.string.state_rest_text
            TimerState.FINISHED -> R.string.state_finished_text
        }
        binding.stateTextView.text = requireContext().getString(stringId)
    }
}