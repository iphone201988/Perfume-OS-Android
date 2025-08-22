package com.tech.perfumos.ui.dashboad.fragment_folder.setting

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.DELETE_ACCOUNT_API
import com.tech.perfumos.data.api.Constants.FORGOT_PASSWORD_API
import com.tech.perfumos.databinding.DeleteDialogBinding
import com.tech.perfumos.databinding.FragmentSettingBinding
import com.tech.perfumos.databinding.LogoutDialogBinding
import com.tech.perfumos.databinding.SettingRvItemBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseFragment
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.dashboad.fragment_folder.change_language.ChangeLanguage
import com.tech.perfumos.ui.dashboad.fragment_folder.change_password_folder.ChangePassword
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.HomeFragment
import com.tech.perfumos.ui.dashboad.fragment_folder.privacy_folder.PrivacyActivity
import com.tech.perfumos.ui.dashboad.fragment_folder.term_of_use.TermOfUseActivity
import com.tech.perfumos.utils.BaseCustomDialog
import com.tech.perfumos.utils.SocketManagerHelper
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.showErrorToast
import com.tech.perfumos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    private val viewModel: SettingFragmentVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_setting
    }


    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        adapterInit()
        initObserver()
        clickListener()
    }

    private fun clickListener() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.back -> {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        FORGOT_PASSWORD_API -> {
                            try {
                                Log.d(
                                    "FORGOT_PASSWORD_API",
                                    "FORGOT_PASSWORD_API: ${Gson().toJson(it)}"
                                )
                                // val data: LoginModel? = Utils.parseJson(it.data.toString())

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: ${jsonObject}")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())

                                    val intent = Intent(
                                        requireContext(),
                                        ChangePassword::class.java
                                    )
                                    intent.putExtra(
                                        "email",
                                        sharedPrefManager.getCurrentUser()?.email.toString()
                                    )
                                    intent.putExtra("type", 3)
                                    intent.putExtra("from", 1)
                                    changePasswordLauncher.launch(intent)
                                    //startActivity(intent)
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        DELETE_ACCOUNT_API -> {
                            try {
                                Log.d(
                                    "DELETE_ACCOUNT_API",
                                    "DELETE_ACCOUNT_API: ${Gson().toJson(it)}"
                                )
                                // val data: LoginModel? = Utils.parseJson(it.data.toString())

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: ${jsonObject}")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())
                                    deleteDialog.dismiss()
                                    SocketManagerHelper.disconnect()
                                    //Utils.route = 1
                                    sharedPrefManager.clear()
                                    startActivity(
                                        Intent(
                                            requireActivity(),
                                            LoginActivity::class.java
                                        )
                                    )
                                    requireActivity().finishAffinity()
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    try {
                        Log.d("ERROR", "initObserver: ${Gson().toJson(it)}")
                        val jsonObject = JSONObject(it.data.toString())
                        Log.d("ERROR", "initObserver: ${jsonObject}")

                        Log.d("ErrorMessage", jsonObject.getString("message").toString())
                        val message = jsonObject.getString("message").toString()
                        showErrorToast(message)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {

                }
            }
        }
    }

    private lateinit var logoutDialog: BaseCustomDialog<LogoutDialogBinding>
    private lateinit var deleteDialog: BaseCustomDialog<DeleteDialogBinding>
    private lateinit var settingAdapter: SimpleRecyclerViewAdapter<SettingModel, SettingRvItemBinding>
    private lateinit var settingAdapter2: SimpleRecyclerViewAdapter<SettingModel, SettingRvItemBinding>

    private fun logoutFun() {
        logoutDialog = BaseCustomDialog<LogoutDialogBinding>(
            requireContext(), R.layout.logout_dialog
        ) {
            when (it?.id) {
                R.id.logout -> {
                    logoutDialog.dismiss()
                    SocketManagerHelper.disconnect()
                    //Utils.route = 1
                    sharedPrefManager.clear()
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    requireActivity().finishAffinity()

                }

                R.id.cancel -> {
                    logoutDialog.dismiss()
                }
            }
        }
        logoutDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        logoutDialog.create()
        logoutDialog.show()
    }

    private fun deleteAccountFun() {
        deleteDialog = BaseCustomDialog<DeleteDialogBinding>(
            requireContext(), R.layout.delete_dialog
        ) {
            when (it?.id) {

                R.id.logout -> {

                    if (deleteDialog.binding.newPassword.text.isNullOrEmpty() || !deleteDialog.binding.newPassword.text.toString().equals("DELETE")) {
                        showErrorToast("Please type DELETE to delete your account.")
                    } else {
                        val requestMap = hashMapOf<String, Any>(
                            "password" to deleteDialog.binding.newPassword.text.toString(),
                        )
                        viewModel.deleteUserApi(DELETE_ACCOUNT_API, requestMap)
                    }

                   /* if (sharedPrefManager.getCurrentUser()?.socialLinkedAccounts == null) {

                    } else {
                        val requestMap = hashMapOf<String, Any>()
                        viewModel.deleteUserApi(DELETE_ACCOUNT_API, requestMap)

                    }*/


                }

                R.id.cancel -> {
                    deleteDialog.dismiss()
                }
            }
        }
        deleteDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        deleteDialog.create()

        /*if (sharedPrefManager.getCurrentUser()?.socialLinkedAccounts == null) {
            deleteDialog.binding.confirmPassword.visibility = View.VISIBLE
            deleteDialog.binding.newPassword.visibility = View.VISIBLE
        }else{
            deleteDialog.binding.confirmPassword.visibility = View.GONE
            deleteDialog.binding.newPassword.visibility = View.GONE
        }*/


        deleteDialog.show()
    }

    private fun adapterInit() {
        val itemListData = ArrayList<SettingModel>()
        itemListData.add(SettingModel(getString(R.string.share_app), R.drawable.share_app, false))
        itemListData.add(
            SettingModel(
                getString(R.string.change_language),
                R.drawable.language_translate,
                false
            )
        )
        itemListData.add(SettingModel(getString(R.string.contact_us), R.drawable.contact_us, false))
        itemListData.add(
            SettingModel(
                getString(R.string.change_password),
                R.drawable.change_password,
                false
            )
        )
        itemListData.add(
            SettingModel(
                getString(R.string.delete_account),
                R.drawable.delete_account,
                false
            )
        )

        val itemListData2 = ArrayList<SettingModel>()
        itemListData2.add(
            SettingModel(
                getString(R.string.privacy_policy),
                R.drawable.ic_description,
                false
            )
        )
        itemListData2.add(
            SettingModel(
                getString(R.string.term_of_use),
                R.drawable.ic_description,
                false
            )
        )
        itemListData2.add(SettingModel(getString(R.string.log_out), R.drawable.login, false))
        /////////////////////////////

        /*itemListData2.add(SettingModel(getString(R.string.log_out), R.drawable.login, false))
        itemListData2.add(SettingModel(getString(R.string.log_out), R.drawable.login, false))
        itemListData2.add(SettingModel(getString(R.string.log_out), R.drawable.login, false))
        itemListData2.add(SettingModel(getString(R.string.log_out), R.drawable.login, false))
        itemListData2.add(SettingModel(getString(R.string.log_out), R.drawable.login, false))
        itemListData2.add(SettingModel(getString(R.string.log_out), R.drawable.login, false))
        itemListData2.add(SettingModel(getString(R.string.log_out), R.drawable.login, false))*/

        settingAdapter = SimpleRecyclerViewAdapter(
            R.layout.setting_rv_item, BR.bean
        ) { v, m, pos ->
            when (pos) {
                0 -> {

                }

                1 -> startActivity(Intent(requireActivity(), ChangeLanguage::class.java))
                3 -> {
                    val requestMap = hashMapOf<String, Any>(
                        "email" to sharedPrefManager.getCurrentUser()?.email.toString(),
                        "type" to 3,
                    )

                    viewModel.restPasswordApi(FORGOT_PASSWORD_API, requestMap)

                }

                4 -> {


                    deleteAccountFun()
                }
            }
        }
        binding.settingRv.adapter = settingAdapter
        settingAdapter.list = itemListData



        settingAdapter2 = SimpleRecyclerViewAdapter(
            R.layout.setting_rv_item, BR.bean
        ) { v, m, pos ->
            when (pos) {
                0 -> startActivity(Intent(requireActivity(), PrivacyActivity::class.java))
                1 -> startActivity(Intent(requireActivity(), TermOfUseActivity::class.java))
                2 -> logoutFun()
            }
        }
        binding.settingRvII.adapter = settingAdapter2
        settingAdapter2.list = itemListData2
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        } else {
            // Permission denied
        }
    }


    private val changePasswordLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                // Handle the result here
                // requireActivity().onBackPressedDispatcher.onBackPressed()

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.setReorderingAllowed(true)
                transaction.replace(R.id.homeSectionNav, HomeFragment())

                transaction.addToBackStack(null)
                // Clear backstack when loading a main fragment
                /* requireActivity().supportFragmentManager.popBackStack(
                     null,
                     FragmentManager.POP_BACK_STACK_INCLUSIVE
                 )*/


                transaction.commit()
            }
        }


}