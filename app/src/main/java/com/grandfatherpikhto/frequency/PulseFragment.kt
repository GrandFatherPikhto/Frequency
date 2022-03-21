package com.grandfatherpikhto.frequency

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.grandfatherpikhto.frequency.databinding.FragmentPulseBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PulseFragment : Fragment() {
    companion object {
        const val TAG:String = "PulseFramgent"
    }

    private var _binding: FragmentPulseBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val pulseModel by viewModels<PulseModel>()

    private val pulsePlayer:PulsePlayer by lazy {
        PulsePlayer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPulseBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            sliderFrequency.addOnChangeListener { _, value, fromUser ->
                tvFrequency.text = getString(R.string.tvFrequency, value)
                if(fromUser) {
                    pulseModel.changeFrequency(value.toDouble())
                }
            }
            sliderEnvelope.addOnChangeListener { _, value, fromUser ->
                tvEnvelope.text = getString(R.string.tvEnvelope, value)
                if(fromUser) {
                    pulseModel.changeEnvelope(value.toDouble())
                }
            }
            switchPulse.setOnCheckedChangeListener { _, isChecked ->
                pulseModel.changeEnable(isChecked)
            }
        }

        pulseModel.frequency.observe(viewLifecycleOwner) { frequency ->
            pulsePlayer.frequency = frequency
        }

        pulseModel.envelope.observe(viewLifecycleOwner) { envelope ->
            pulsePlayer.envelope = envelope
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            pulseModel.frequency.value?.let { frequency ->
                tvFrequency.text = getString(R.string.tvFrequency, frequency)
                sliderFrequency.value = frequency.toFloat()
                pulsePlayer.frequency = frequency
            }

            pulseModel.envelope.value?.let { envelope ->
                tvEnvelope.text = getString(R.string.tvEnvelope, envelope)
                sliderEnvelope.value = envelope.toFloat()
                pulsePlayer.envelope = envelope
            }

            pulseModel.enable.value?.let { enable ->
                switchPulse.isChecked = enable
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause()")
    }
}