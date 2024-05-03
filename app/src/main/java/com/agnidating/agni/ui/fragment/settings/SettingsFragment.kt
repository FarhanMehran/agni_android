package com.agnidating.agni.ui.fragment.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentSettingsBinding
import com.agnidating.agni.model.profile.Data
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.auth.phone.YourPhoneActivity
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import com.agnidating.agni.ui.activities.getStarted.GetStarted
import com.agnidating.agni.ui.activities.plans.PlanSelectionActivity
import com.agnidating.agni.ui.activities.writeBio.WriteBio
import com.agnidating.agni.utils.*
import com.agnidating.agni.utils.custom_view.ConfirmDialog
import com.agnidating.agni.utils.custom_view.DeleteDialog
import com.agnidating.agni.utils.custom_view.ErrorDialog
import com.agnidating.agni.utils.custom_view.FlowerDialog
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 *
 * Created by AJAY ASIJA
 *
 */
@AndroidEntryPoint
class SettingsFragment : ScopedFragment() {
    private var profileData: Data?=null
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel:SettingsFragmentViewModel by viewModels()
    private var showLoader=true
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSettingsBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rlPremium.isVisible=sharedPrefs.isSubscribed().not()
        listeners()
        bindObserver()
    }

    override fun onResume() {
        super.onResume()
        binding.rlPremium.isVisible=sharedPrefs.isSubscribed().not()
        hitApi()
    }


    /**
     * Hit all api here [hitApi]
     */
    private fun hitApi() {
        if (checkNetwork()){
            viewModel.getProfile()
        }
    }

    /**
     * Bind live data observers here [bindObserver]
     */
    private fun bindObserver() {
        viewModel.baseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    sharedPrefs.clearPreference()
                    startActivity(Intent(requireContext(),GetStarted::class.java))
                    requireActivity().finishAffinity()
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
        viewModel.hideProfileLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    it.data.message?.toast(requireContext())
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }

        viewModel.profileLiveData.observe(viewLifecycleOwner){
            when(it){
                is ResultWrapper.Loading->{
                    if(showLoader){
                        (requireActivity() as ScopedActivity).showProgress()
                        showLoader=false
                    }
                }
                is ResultWrapper.Success->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    handleResponse(it.data.data)
                }
                is ResultWrapper.Error->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }

    }


    /**
     * Handle response [handleResponse]
     */
    @SuppressLint("SetTextI18n")
    private fun handleResponse(data: Data) {
        profileData=data
        binding.tvName.text=data.name+", ${data.birthDate.toAge()}"
        binding.ivProfile.load(data.profileImg[0].thumbnail,CommonKeys.IMAGE_ThUMBNAIL_BASE_URL)
        binding.switchHideProfile.isChecked= data.isHide=="1"
    }
    /**
     * handle all click listeners
     */
    private fun listeners() {
        binding.tvPhoneNumber.setOnClickListener {
            startActivity(Intent(requireContext(),YourPhoneActivity::class.java).apply {
                putExtra(CommonKeys.FOR_UPDATE,true)
                putExtra(CommonKeys.COUNTRY_CODE,profileData?.countryCode)
                putExtra(CommonKeys.PHONE,profileData?.phone)
            })
        }
        binding.btLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.rlPremium.setOnClickListener {
            val intent = Intent(requireActivity(), PlanSelectionActivity::class.java)
            startActivity(intent)
        }

        binding.ivEdit.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingFragmentToEditProfileFragment(
                profileData?.profileImg?.toTypedArray(),
                profileData?.bio.toString()
            )
            findNavController().navigate(action)
        }
        binding.switchHideProfile.setOnClickListener {
            val status= if (binding.switchHideProfile.isChecked) "1" else "0"
            viewModel.hideProfile(status)
        }

        binding.tvBio.setOnClickListener {
            val intent=Intent(requireContext(),WriteBio::class.java)
            intent.putExtra(CommonKeys.FOR_UPDATE,true)
            intent.putExtra(CommonKeys.BIO,profileData?.bio)
            startActivity(intent)
        }
        binding.tvPreferredAgeRange.setOnClickListener {
            val action=SettingsFragmentDirections.actionSettingFragmentToAgeFragment(
                profileData?.minAge.toString(),
                profileData?.maxAge.toString(),
            )
            findNavController().navigate(action)
        }
        binding.tvEducation.setOnClickListener {
            val bundle=Bundle()
            bundle.putBoolean(CommonKeys.FOR_UPDATE,true)
            bundle.putString(CommonKeys.OCCUPATION,profileData?.occupation)
            bundle.putString(CommonKeys.EDUCATION,profileData?.education)
            findNavController().navigate(R.id.action_settingFragment_to_nav_education,bundle)
        }
        binding.tvCommunity.setOnClickListener {
            val bundle=Bundle()
            bundle.putBoolean(CommonKeys.FOR_UPDATE,true)
            bundle.putString(CommonKeys.COMMUNITY,profileData?.community)
            findNavController().navigate(R.id.action_settingFragment_to_nav_community,bundle)
        }
        binding.tvReligion.setOnClickListener {
            val bundle=Bundle()
            bundle.putBoolean(CommonKeys.FOR_UPDATE,true)
            bundle.putString(CommonKeys.RELIGION,profileData?.religion)
            findNavController().navigate(R.id.action_settingFragment_to_nav_religion,bundle)
        }
        binding.tvShowMe.setOnClickListener {
            val action=SettingsFragmentDirections.actionSettingFragmentToShowMeFragment(
                profileData?.intGender.toString(),
            )
            findNavController().navigate(action)
        }

         binding.tvDistanceRange.setOnClickListener {
             val action=SettingsFragmentDirections.actionSettingFragmentToDistanceRange(
                 profileData?.minDistance.toString(),
                 profileData?.maxDistance.toString(),
             )
             findNavController().navigate(action)
         }

         binding.tvContactUs.setOnClickListener {
             findNavController().navigate(R.id.action_settingFragment_to_contact_us)
         }
         binding.tvOurPolicy.setOnClickListener {
             /*var action=SettingsFragmentDirections.actionSettingFragmentToNavPrivacyPolicy(
                 ""
             )*/
             findNavController().navigate(R.id.action_setting_fragment_to_nav_privacy_policy)
         }
        binding.tvDeleteAccount.setOnClickListener {
            val deleteDialog=DeleteDialog{
                if (it) viewModel.deleteAccount()
            }
            deleteDialog.show(parentFragmentManager, "")
         }



        binding.ivMenu.setOnClickListener {
            val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.CustomPopup)
            val popup = PopupMenu(wrapper,binding.ivMenu)
            popup.gravity=Gravity.END
            popup.menuInflater
                .inflate(R.menu.popup_menu, popup.menu)
            popup.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.delete->{
                        binding.tvDeleteAccount.performClick()
                    }
                    R.id.unblock->{
                        findNavController().navigate(R.id.action_settingFragment_to_nav_block_user)
                    }
                }
                true
            }
            popup.show()
        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.hideProfileLiveData.postValue(null)
    }
}