package com.tech.perfumos.ui.camera_perfume

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.data.api.Constants.ADD_TO_FAVORITE_API
import com.tech.perfumos.data.api.Constants.GET_PERFUMER_API
import com.tech.perfumos.data.api.Constants.SIMILAR_PERFUME_API
import com.tech.perfumos.databinding.ActivityPerfumerProfileBinding
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.camera_perfume.model.PerfumerModel
import com.tech.perfumos.ui.camera_perfume.model.SimilarPerfume
import com.tech.perfumos.ui.camera_perfume.model.SimilarPerfumeModel
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class PerfumerProfileActivity : BaseActivity<ActivityPerfumerProfileBinding>() {
    val viewmodel: PerfumerVm by viewModels()

    private var similarPerfumeList: ArrayList<SimilarPerfume?>? = ArrayList()
    var layoutManager: LinearLayoutManager? = null
    private var perfumerInfo: PerfumerModel.Data? = null
    private var perfumerId: String? = null
    private var page: Int = 1

    private lateinit var similarPerfumeAdapter: SimilarPerfumeAdapter

    private var isFavorite: Boolean = false

    override fun getLayoutResource(): Int {
        return R.layout.activity_perfumer_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onCreateView() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perfumerInfo = intent.getSerializableExtra("PerfumerData", PerfumeInfoModel.PerfumeInfoData.Perfumer::class.java)
        } else {
            perfumerInfo = intent.getSerializableExtra("PerfumerData") as PerfumeInfoModel.PerfumeInfoData.Perfumer?
        }*/
        perfumerId = intent.getStringExtra("PerfumerId")
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // initView()
        clickListener()
        initAdapter()
        initObserver()
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)

        val requestMap = hashMapOf<String, Any>(
            "id" to (perfumerId ?: 0)
        )
        viewmodel.getPerfumerApi(GET_PERFUMER_API, requestMap)
    }

    private fun initView() {
        if (perfumerInfo != null) {

            binding.apply {
                tvName.text = perfumerInfo?.perfumer?.name.toString()
                tvDesc.setText(perfumerInfo?.perfumer?.description.toString())
                tvPerfumeCount.text = perfumerInfo?.totalCount.toString()

                val perfumerImage =
                    if ((perfumerInfo?.perfumer?.bigImage ?: "").contains("http")) {
                        perfumerInfo?.perfumer?.bigImage
                    } else {
                        "${Constants.BASE_URL_IMAGE}${perfumerInfo?.perfumer?.bigImage}"
                    }
                Glide.with(this@PerfumerProfileActivity).load(perfumerImage).into(perfumeImg)

                isFavorite = perfumerInfo?.perfumer?.isFavorite ?: false

                if (isFavorite) {
                    binding.tvAddCollection.text = ContextCompat.getString(
                        this@PerfumerProfileActivity, R.string.addedInFavorite
                    )
                } else {
                    binding.tvAddCollection.text = ContextCompat.getString(
                        this@PerfumerProfileActivity, R.string.add_to_favourite
                    )
                }
            }

        }
    }

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.iv_back -> {
                    super.onBackPressedDispatcher.onBackPressed()
                }

                R.id.tv_write_review -> {
                    //reviewDialog.show()
                }

                R.id.tvAddCollection->{
                    if (perfumerId!=null) {
                        val requestMap = hashMapOf<String, Any>(
                            "id" to perfumerId.toString(),
                            "type" to "perfumer",
                        )
                        viewmodel.addToFavoriteApi(ADD_TO_FAVORITE_API, requestMap)
                    }
                }
            }
        }
    }

    private fun initObserver() {
        viewmodel.commonObserver.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        GET_PERFUMER_API -> {
                            try {
                                Log.d("ProfileDataModel", "ProfileDataModel: ${Gson().toJson(it)}")
                                val data: PerfumerModel? = parseJson(it.data.toString())
                                Log.d(
                                    "ProfileDataModel",
                                    "GET_PERFUMER_API: ${Gson().toJson(data)}"
                                )
                                perfumerInfo = data?.data
                                if (data?.success == true) {
                                    if (!perfumerInfo?.perfumes.isNullOrEmpty()) {

                                        similarPerfumeList = perfumerInfo?.perfumes
                                        similarPerfumeAdapter.list = similarPerfumeList
                                        similarPerfumeAdapter.notifyDataSetChanged()
                                        binding.rvSimilarPerfumes.visibility = View.VISIBLE
                                        binding.noDataFound.visibility = View.GONE
                                    } else {
                                        binding.rvSimilarPerfumes.visibility = View.GONE
                                        binding.noDataFound.visibility = View.VISIBLE
                                    }
                                    initView()
                                } else {
                                    showErrorToast("Something went wrong")


                                }

                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        }

                        SIMILAR_PERFUME_API -> {
                            try {
                                Log.d(
                                    "ProfileDataModel",
                                    "SIMILAR_PERFUME_API: ${Gson().toJson(it)}"
                                )
                                val data: SimilarPerfumeModel? = parseJson(it.data.toString())
                                Log.d(
                                    "ProfileDataModel",
                                    "SIMILAR_PERFUME_API: ${Gson().toJson(data)}"
                                )
                                if (data?.success == true) {
                                    val list = data.data
                                    if (!list.isNullOrEmpty()) {
                                        similarPerfumeList?.addAll(list)
                                        similarPerfumeAdapter.addNewItem(data.data)
                                        similarPerfumeAdapter.notifyDataSetChanged()

                                        binding.rvSimilarPerfumes.smoothScrollToPosition(
                                            layoutManager?.findLastCompletelyVisibleItemPosition()!! + 1
                                        )

                                    }
                                } else {
                                    showErrorToast("Something went wrong")


                                }

                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        }

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
                                    isFavorite = !isFavorite
                                    if (isFavorite) {
                                        binding.tvAddCollection.text = ContextCompat.getString(
                                            this@PerfumerProfileActivity, R.string.addedInFavorite
                                        )
                                    } else {
                                        binding.tvAddCollection.text = ContextCompat.getString(
                                            this@PerfumerProfileActivity, R.string.add_to_favourite
                                        )
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
                        Log.d("ERROR", "initObserver: ${Gson().toJson(it)}")
                        val jsonObject = JSONObject(it.data.toString())
                        val message = jsonObject.getString("message")
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


    private fun initAdapter() {

        similarPerfumeAdapter = SimilarPerfumeAdapter(
            this,
            similarPerfumeList,
            object : SimilarPerfumeAdapter.ClickListener {
                override fun onClick(
                    position: Int,
                    model: SimilarPerfume
                ) {
                    val intent =
                        Intent(this@PerfumerProfileActivity, PerfumeInfoActivity::class.java)
                            .apply {
                                putExtra("perfumeId", model.id)
                            }
                    startActivity(intent)
                }

            })

        binding.rvSimilarPerfumes.layoutManager = layoutManager
        binding.rvSimilarPerfumes.adapter = similarPerfumeAdapter


        binding.rvSimilarPerfumes.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                val totalItemCount = layoutManager?.itemCount
                val lastVisibleItem = layoutManager?.findLastCompletelyVisibleItemPosition()
                Log.d("lastVisibleItem", "onScrolled: lastVisibleItem $lastVisibleItem")

                if (totalItemCount != null) {
                    if (lastVisibleItem == totalItemCount - 1) {

                        if (totalItemCount < (perfumerInfo?.totalCount ?: 0)) {
                            page += 1
                            val requestMap = hashMapOf<String, Any>(
                                "id" to (perfumerId ?: 0),
                                "limit" to (10),
                                "page" to (page),
                                "type" to ("perfumer")
                            )
                            viewmodel.getSimilarPerfumerApi(SIMILAR_PERFUME_API, requestMap)
                        }


                    }
                }
            }
        }
        )
    }
}