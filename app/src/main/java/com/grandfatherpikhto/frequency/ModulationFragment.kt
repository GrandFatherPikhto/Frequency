package com.grandfatherpikhto.frequency

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.grandfatherpikhto.frequency.databinding.FragmentModulationBinding
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ModulationFragment : Fragment() {
    companion object {
        const val TAG:String = "ModulationFragment"
        const val NAME:String = "ModulationFragment"
        const val FREQUENCY:String = "Frequency"
        const val ENABLE:String = "Enable"
    }

    private var _binding: FragmentModulationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val modulationModel by activityViewModels<ModulationModel>()

    private val preferences by lazy {
        requireContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
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
        Log.e(TAG, "model: $modulationModel")
        binding.apply {
            ibPlay.setOnClickListener { button ->
                Log.e(TAG, "onClick($button)")
                modulationModel.resetPlay()
            }

            sliderPulseFrequency.addOnChangeListener { _, value, fromUser ->
                if(fromUser) {
                    modulationModel.changeFrequency(value.roundToInt())
                }
            }
        }

        lifecycleScope.launch {
            modulationModel.enable.collect { enable ->
                Log.e(TAG, "Enable: $enable")
                setPlayIcon(enable)
            }
        }

        lifecycleScope.launch {
            modulationModel.frequency.collect { frequency ->
                binding.tvPulseFrequency.text = getString(R.string.tvPulseFrequency, frequency)
                if(binding.sliderPulseFrequency.value.roundToInt() != frequency) {
                    binding.sliderPulseFrequency.value = frequency.toFloat()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // play(modulationModel.enable.value)
        preferences.let {
            val frequency = it.getInt(FREQUENCY, ModulationModel.DEFAULT_FREQUENCY)
            modulationModel.changeFrequency(frequency)
            val enable = it.getBoolean(ENABLE, ModulationModel.DEFAULT_ENABLE)
            modulationModel.changeEnable(enable)
        }
    }

    override fun onPause() {
        preferences.edit {
            putInt(FREQUENCY, modulationModel.frequency.value)
            putBoolean(ENABLE, modulationModel.enable.value)
            commit()
        }
        super.onPause()
    }

    private fun setPlayIcon(enable: Boolean) {
        if (enable) {
            binding.ibPlay.setImageResource(R.drawable.ic_baseline_pause_48)
        } else {
            binding.ibPlay.setImageResource(R.drawable.ic_baseline_play_arrow_48)
        }
    }
}