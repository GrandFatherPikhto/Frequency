package com.grandfatherpikhto.frequency

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.grandfatherpikhto.frequency.databinding.FragmentModulationBinding
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ModulationFragment : Fragment() {

    private var _binding: FragmentModulationBinding? = null

    companion object {
        const val TAG:String = "ModulationFragment"
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val modulationModel by viewModels<ModulationModel>()

    private val modulationPlayer by lazy {
        ToneModulationPlayer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentModulationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            sliderPulseFrequency.addOnChangeListener { _, value, fromUser ->
                if(fromUser) {
                    modulationModel.changeFrequency(value.roundToInt())
                }
            }
            switchModulation.setOnCheckedChangeListener { _, isChecked ->
                modulationModel.changeEnable(isChecked)
            }
        }

        modulationModel.enable.observe(viewLifecycleOwner, { enable ->
            if(enable) {
                modulationPlayer.play()
            } else {
                modulationPlayer.stop()
            }
        })

        modulationModel.frequency.observe(viewLifecycleOwner, { frequency ->
            binding.tvPulseFrequency.text = getString(R.string.tvPulseFrequency, frequency)
            modulationPlayer.frequency = frequency
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        modulationModel.frequency.value?.let { frequency ->
            binding.tvPulseFrequency.text = getString(R.string.tvPulseFrequency, frequency)
        }
        modulationModel.enable.value?.let { enable ->
            binding.switchModulation.isChecked = enable
        }
    }

    override fun onPause() {
        super.onPause()
        modulationPlayer.stop()
    }
}