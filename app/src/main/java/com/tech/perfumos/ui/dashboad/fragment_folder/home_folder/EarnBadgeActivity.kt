package com.tech.perfumos.ui.dashboad.fragment_folder.home_folder

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.BADGES_API
import com.tech.perfumos.data.api.Constants.RANKS_API
import com.tech.perfumos.databinding.ActivityEarnBadgeBinding
import com.tech.perfumos.databinding.BadgeDialogBinding
import com.tech.perfumos.databinding.BadgeProgressDialogBinding
import com.tech.perfumos.databinding.EarnBadgeItemViewBinding
import com.tech.perfumos.databinding.EarnRankItemViewBinding
import com.tech.perfumos.databinding.RankPointDialogBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.model.EarnBadgesModel
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.model.RankList
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.model.RanksModel
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.BadgeId
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.BadgeModel
import com.tech.perfumos.utils.BaseCustomDialog
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderEffectBlur
import org.json.JSONObject

@AndroidEntryPoint
class EarnBadgeActivity : BaseActivity<ActivityEarnBadgeBinding>() {

    private val viewModel: HomeFragmentVm by viewModels()
    private lateinit var badgeAdapter: SimpleRecyclerViewAdapter<BadgeId, EarnBadgeItemViewBinding>
    private lateinit var rankAdapter: SimpleRecyclerViewAdapter<RankList, EarnRankItemViewBinding>
    private var userBadgeList: ArrayList<BadgeId?>? = ArrayList()
    private var ranksList: ArrayList<RankList?>? = ArrayList()

    private lateinit var badgeDialog: BaseCustomDialog<BadgeDialogBinding>
    private lateinit var badgeProgressDialog: BaseCustomDialog<BadgeProgressDialogBinding>
    private lateinit var rankPointDialog: BaseCustomDialog<RankPointDialogBinding>

    override fun getLayoutResource(): Int {
        return R.layout.activity_earn_badge
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        setAdapter()
        badgeDialog()
        clickListener()
        initObserver()
        viewModel.getRanksApi(RANKS_API)
        viewModel.getBadgesApi(BADGES_API)
        setData()
        Log.d("DASdasdasdasd", "onCreateView: ${Gson().toJson(sharedPrefManager.getCurrentUser())}")

        binding.selectTap = 1

    }

    private fun setData() {

       /* for (item in 0..1) {
            userBadgeList?.add(
                BadgeModel(
                    BadgeModel.BadgeId(
                        "",
                        "",
                        "http://13.51.224.142:3000/uploads/badge6.png",
                        "",
                        0
                    ), null, null, null, null
                )
            )
        }
        for (item in 0..6) {
            userBadgeList?.add(
                BadgeModel(
                    BadgeModel.BadgeId("", "", null, "", 0),
                    null,
                    null,
                    null,
                    null
                )
            )
        }*/
        badgeAdapter.list = userBadgeList
        // badgeAdapter.notifyDataSetChanged()

        rankAdapter.list = ranksList
        rankAdapter.notifyDataSetChanged()

    }

    private fun setAdapter() {
        badgeAdapter = SimpleRecyclerViewAdapter(
            R.layout.earn_badge_item_view, BR.bean
        ) { v, m, pos ->
            when (v.id) {

                R.id.llMain -> {
                    badgeDialog.binding.bean = m
                    badgeProgressDialog.binding.bean = m
                    if (pos < 2) {
                        badgeDialog.show()
                    } else {
                        badgeProgressDialog.show()
                    }
                }
            }
        }
        binding.rvBadges.adapter = badgeAdapter
        badgeAdapter.list = userBadgeList


        rankAdapter = SimpleRecyclerViewAdapter(
            R.layout.earn_rank_item_view, BR.bean
        ) { v, m, pos ->
            when (v.id) {

                R.id.llMain -> {
                    rankPointDialog.binding.apply {

                    }
                    rankPointDialog.binding.bean = m
                    rankPointDialog.show()
                }
            }
        }
        binding.rvRank.adapter = rankAdapter
        rankAdapter.list = ranksList

    }

    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.llRanks -> {
                    binding.selectTap = 1
                    binding.rvRank.visibility = View.VISIBLE
                    binding.rvBadges.visibility = View.GONE
                    /*badgeAdapter.list = userBadgeList
                     badgeAdapter.notifyDataSetChanged()*/
                }

                R.id.llBadges -> {
                    binding.selectTap = 2
                    binding.rvRank.visibility = View.GONE
                    binding.rvBadges.visibility = View.VISIBLE
                    /*rankAdapter.list = ranksList
                    rankAdapter.notifyDataSetChanged()*/
                }
            }
        }
    }

    private fun initObserver() {
        viewModel.commonObserver.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        RANKS_API -> {
                            try {
                                Log.d("response", "initObserver: ${it.data}")
                                val response: RanksModel? = parseJson(it.data.toString())

                                if (response?.success == true) {
                                    if (!response.data?.ranks.isNullOrEmpty()) {
                                        ranksList = response.data?.ranks
                                        rankAdapter.list = ranksList
                                        rankAdapter.notifyDataSetChanged()
                                    }
                                } else {

                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        BADGES_API -> {
                            try {
                                Log.d("response", "initObserver: ${it.data}")
                                val response: EarnBadgesModel? = parseJson(it.data.toString())

                                if (response?.success == true) {
                                    if (!response.data.isNullOrEmpty()) {
                                        userBadgeList = response.data
                                        badgeAdapter.list = userBadgeList
                                        badgeAdapter.notifyDataSetChanged()
                                    }
                                } else {

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
                            startActivity(Intent(this, LoginActivity::class.java))
                            finishAffinity()
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


    private fun badgeDialog() {
        badgeDialog = BaseCustomDialog<BadgeDialogBinding>(
            this, R.layout.badge_dialog
        ) {
            when (it?.id) {
                R.id.ivClose -> {
                    badgeDialog.dismiss()
                }


            }
        }
        badgeDialog.create()
        badgeDialog.setCancelable(true)

        badgeDialog.binding.tvBadgeSubTitle.visibility = View.VISIBLE

        val windowBackground = window.decorView.background
        val blurView = badgeDialog.binding.blurView

        val rootView =
            window.decorView.findViewById<ViewGroup>(android.R.id.content)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setupWith(rootView, RenderEffectBlur())
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(16f)
        }

        badgeProgressDialog = BaseCustomDialog<BadgeProgressDialogBinding>(
            this, R.layout.badge_progress_dialog
        ) {
            when (it?.id) {
                R.id.ivClose -> {
                    badgeDialog.dismiss()
                }


            }
        }
        badgeProgressDialog.create()
        badgeProgressDialog.setCancelable(true)

        val windowBackground1 = window.decorView.background
        val blurView1 = badgeProgressDialog.binding.blurView

        val rootView1 =
            window.decorView.findViewById<ViewGroup>(android.R.id.content)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView1.setupWith(rootView1, RenderEffectBlur())
                .setFrameClearDrawable(windowBackground1)
                .setBlurRadius(16f)
        }

        rankPointDialog = BaseCustomDialog<RankPointDialogBinding>(
            this, R.layout.rank_point_dialog
        ) {
            when (it?.id) {
                R.id.ivClose -> {
                    rankPointDialog.dismiss()
                }


            }
        }
        rankPointDialog.create()
        rankPointDialog.setCancelable(true)

        val windowBackground2 = window.decorView.background
        val blurView2 = rankPointDialog.binding.blurView

        val rootView2 =
            window.decorView.findViewById<ViewGroup>(android.R.id.content)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView2.setupWith(rootView2, RenderEffectBlur())
                .setFrameClearDrawable(windowBackground2)
                .setBlurRadius(16f)
        }
    }
}