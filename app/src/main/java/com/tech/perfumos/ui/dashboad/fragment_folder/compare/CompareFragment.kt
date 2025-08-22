package com.tech.perfumos.ui.dashboad.fragment_folder.compare

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.ADD_TO_FAVORITE_API
import com.tech.perfumos.data.api.Constants.LOGIN_API
import com.tech.perfumos.data.api.Constants.RECENT_TOP_PERFUME_API
import com.tech.perfumos.databinding.FragmentCompareBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseFragment
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.camera_perfume.PerfumeInfoActivity
import com.tech.perfumos.ui.camera_perfume.model.PerfumeInfoModel
import com.tech.perfumos.ui.core.SearchClickListener
import com.tech.perfumos.ui.dashboad.SearchBottomSheet
import com.tech.perfumos.ui.dashboad.fragment_folder.setting.SettingFragment
import com.tech.perfumos.ui.dashboad.model.SearchHistoryModel
import com.tech.perfumos.utils.EventsHandler
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import com.tech.perfumos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class CompareFragment : BaseFragment<FragmentCompareBinding>() {
    private val viewModel: CompareVm by viewModels()
    private var searchClickListener: SearchClickListener? = null
    private lateinit var compareAdapter: ComparePerfumeAdapter

    private var searchHistoryData: SearchHistoryModel.SearchHistoryData? = null
    private var isBottomSheetData = false
    private var favoritePos: Int? = null

    private var compareList: ArrayList<PerfumeInfoModel.PerfumeInfoData?> = ArrayList()

    companion object {
        var compareIndex = 2
    }

    override fun onCreateView(view: View) {
        initView()
        initObserver()
        initAdapter()
        clickListener()
    }


    override fun getLayoutResource(): Int {
        return R.layout.fragment_compare
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }


    private fun initView() {
        sharedPrefManager.getCompareList()?.forEach {
            compareList.add(it)
        }
        Log.d("compareList", "initView: $compareList")
        val perfumeInfo =
            arguments?.getSerializable("perfumeData") as? PerfumeInfoModel.PerfumeInfoData
        Log.d("perfumeInfo", "initView: $compareIndex")
        if (perfumeInfo != null) {

            /*compareList.forEachIndexed { index, data ->
                if(data == null){
                    compareList.add(index, perfumeInfo)
                    return@forEachIndexed
                }
                else if(index == compareIndex){
                    compareList.add(index, perfumeInfo)
                    return@forEachIndexed
                }
                }*/
            for (index in compareList.indices) {
                val data = compareList[index]
                if (data == null || index == compareIndex) {
                    compareList[index] = perfumeInfo // replace instead of add
                    compareIndex = index
                    break
                }
                if(compareList.size ==2){
                    compareList[1] = perfumeInfo
                }
            }

            sharedPrefManager.saveCompareList(compareList)

           /* if (compareList.isNullOrEmpty()) {
                compareList.add(perfumeInfo)
                compareList.add(null)
                sharedPrefManager.saveCompareList(compareList)
            } else {
                when (compareList.size) {
                    0 -> {
                        compareList.add(1, perfumeInfo)

                    }

                    1 -> {
                        compareList.add(0, perfumeInfo)
                    }
                }
            }*/
        }
    }

    private fun initAdapter() {

        val windowBackground = requireActivity().window.decorView.background
        compareAdapter = ComparePerfumeAdapter(
            requireContext(),
            compareList,
            object : ComparePerfumeAdapter.ItemClickListener {

                override fun onItemClickListener(perfumeId: String, position: Int) {
                    searchClickListener?.openPerfumeScreen(perfumeId)
                    compareIndex = position
                }

                override fun onItemFavorite(perfumeId: String, position: Int) {
                    favoritePos = position
                    val requestMap = hashMapOf<String, Any>(
                        "id" to perfumeId,
                        "type" to "perfume",
                    )
                    viewModel.addToFavoriteApi(ADD_TO_FAVORITE_API, requestMap)

                }

                override fun onEmptyItemClickListener(position: Int) {
                    searchClickListener?.onSearchClick()
                    compareIndex = position
                }

            })
        binding.rvCompare.adapter = compareAdapter
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

                        ADD_TO_FAVORITE_API -> {
                            try {
                                Log.d(
                                    "response", "ADD_TO_FAVORITE_API: ${Gson().toJson(it)}"
                                )
                                // val data: LoginModel? = Utils.parseJson(it.data.toString())

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: $jsonObject")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())
                                    if(favoritePos != null){
                                        compareList[favoritePos!!]?.isFavorite = !compareList[favoritePos!!]?.isFavorite!!
                                        compareAdapter.notifyItemChanged(favoritePos!!)
                                        favoritePos = null

                                        sharedPrefManager.saveCompareList(compareList)
                                    }
                                } else {
                                    showErrorToast(jsonObject.getString("message").toString())
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
                        Log.e("initObserverJSONObject", "initObserver: ${it}")
                        val jsonObject = JSONObject(it.data.toString())
                        val message = jsonObject.getString("message")
                        showErrorToast(message)
                        if (it.code == 401) {
                            showErrorToast("Your login section is expire, Please login again")
                            requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
                            requireActivity().finishAffinity()
                            sharedPrefManager.clear()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {

                }
            }
        }
    }

    private fun clickListener() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.settingIcon -> {
                    loadFragment(SettingFragment())
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
        transaction.replace(R.id.homeSectionNav, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchClickListener) {
            searchClickListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        searchClickListener = null
        compareIndex = 2
    }


}