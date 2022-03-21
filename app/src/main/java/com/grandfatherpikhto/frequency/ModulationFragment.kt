package com.grandfatherpikhto.frequency

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.grandfatherpikhto.frequency.databinding.FragmentModulationBinding
import kotlinx.coroutines.launch
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

    private val modulationModel by activityViewModels<ModulationModel>()

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
                if (enable) {
                    binding.ibPlay.setImageResource(R.drawable.ic_baseline_pause_48)
                } else {
                    binding.ibPlay.setImageResource(R.drawable.ic_baseline_play_arrow_48)
                }
            }
        }

        lifecycleScope.launch {
            modulationModel.frequency.collect { frequency ->
                binding.tvPulseFrequency.text = getString(R.string.tvPulseFrequency, frequency)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}