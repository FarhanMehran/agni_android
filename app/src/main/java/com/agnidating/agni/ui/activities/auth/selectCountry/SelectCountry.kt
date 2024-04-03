package com.agnidating.agni.ui.activities.auth.selectCountry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.agnidating.agni.R
import com.agnidating.agni.databinding.ActivitySelectCountryBinding
import com.agnidating.agni.model.countries.Country
import com.agnidating.agni.model.countries.CountryResponse
import com.agnidating.agni.utils.loadJSONFromAsset
import com.google.gson.Gson

class SelectCountry(var selectedCountry:Country?) : DialogFragment() {
    private lateinit var adapter: CountryAdapter
    var callBack: ((Country) -> Unit)? = null

    private lateinit var binding: ActivitySelectCountryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.activity_select_country, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val countriesJson=requireActivity().loadJSONFromAsset("countries.json")
        val countryList=Gson().fromJson(countriesJson,CountryResponse::class.java).country
        selectedCountry=selectedCountry?:countryList[224]
        adapter=CountryAdapter(requireContext(),countryList,
            ArrayList(countryList),
            selectedCountry!!
        ){
            binding.btClose.performClick()
        }
        binding.rvCountries.adapter=adapter
        listeners()
    }

    private fun listeners() {
        binding.btClose.setOnClickListener {
            callBack?.invoke(adapter.lastItem)
        }
        binding.etSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                adapter.filterList(binding.etSearch.text.toString())
            }

        })
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }
}