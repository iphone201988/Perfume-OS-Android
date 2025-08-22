package com.tech.perfumos.ui.dashboad.fragment_folder.change_language

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.databinding.ActivityChangeLanguageBinding
import com.tech.perfumos.databinding.ActivityTermOfUseBinding
import com.tech.perfumos.databinding.ChangeLanguageDialogBinding
import com.tech.perfumos.databinding.LanguageRvItemBinding
import com.tech.perfumos.databinding.LogoutDialogBinding
import com.tech.perfumos.databinding.SettingRvItemBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.dashboad.fragment_folder.privacy_folder.PrivacyActivity
import com.tech.perfumos.ui.dashboad.fragment_folder.setting.SettingModel
import com.tech.perfumos.ui.dashboad.fragment_folder.term_of_use.TermOfUseActivity
import com.tech.perfumos.ui.dashboad.fragment_folder.term_of_use.TermOfUseActivityVm
import com.tech.perfumos.utils.BaseCustomDialog
import com.tech.perfumos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray


@AndroidEntryPoint
class ChangeLanguage : BaseActivity<ActivityChangeLanguageBinding>() {
    val viewmodel: ChangeLanguageVm by viewModels()
    private lateinit var changeLanguageDialog: BaseCustomDialog<ChangeLanguageDialogBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_change_language
    }
    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }
    override fun onCreateView() {
        Utils.screenFillView(this)
        adapterInit()
        clickListener()
        changeLanguage()
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)
    }
    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    finish()
                }
            }
        }
    }

    private lateinit var settingAdapter: SimpleRecyclerViewAdapter<CountryCode, LanguageRvItemBinding>
    private fun adapterInit() {
        val countryList = ArrayList<CountryCode>()
        val resourceId = this.resources?.getIdentifier("country_json", "raw", this.packageName)
        val inputStream = resources.openRawResource(resourceId!!)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val gson = Gson()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val country = gson.fromJson(jsonObject.toString(), CountryCode::class.java)
            countryList.add(country)
        }
        settingAdapter = SimpleRecyclerViewAdapter(
            R.layout.language_rv_item, BR.bean
        ) { v, m, pos ->
            when (v?.id) {
                R.id.mainLayout -> {
                    changeLanguageDialog.show()
                }
            }
        }
        binding.settingRv.adapter = settingAdapter
        settingAdapter.list = countryList
    }

    private fun changeLanguage() {
        changeLanguageDialog = BaseCustomDialog<ChangeLanguageDialogBinding>(
            this, R.layout.change_language_dialog
        ) {
            when (it?.id) {
                R.id.change -> {
                    changeLanguageDialog.dismiss()

                }
                R.id.cancel -> {
                    changeLanguageDialog.dismiss()
                }
            }
        }
        changeLanguageDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        changeLanguageDialog.create()

    }
}