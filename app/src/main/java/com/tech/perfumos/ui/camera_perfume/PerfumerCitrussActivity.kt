package com.tech.perfumos.ui.camera_perfume

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.data.api.Constants.ADD_COLLECTION_API
import com.tech.perfumos.data.api.Constants.ADD_TO_FAVORITE_API
import com.tech.perfumos.data.api.Constants.GET_FOLLOWERS_API
import com.tech.perfumos.data.api.Constants.GET_NOTE_API
import com.tech.perfumos.data.api.Constants.SEND_QUIZ_INVITE_API

import com.tech.perfumos.data.model.SimpleDummyModel
import com.tech.perfumos.databinding.ActivityPerfumerCitrussBinding
import com.tech.perfumos.databinding.SinglePerfumeItemViewBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.camera_perfume.model.NoteModel
import com.tech.perfumos.ui.camera_perfume.model.SimilarPerfume


import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import com.tech.perfumos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class PerfumerCitrussActivity : BaseActivity<ActivityPerfumerCitrussBinding>() {
    val viewmodel: CameraVm by viewModels()

    private lateinit var similarPerfumeAdapter: SimilarPerfumeAdapter
    private var similarPerfumeList: ArrayList<SimilarPerfume?>? = ArrayList()

    private lateinit var noteId: String
    private var isFavorite: Boolean = false


    override fun getLayoutResource(): Int {
        return R.layout.activity_perfumer_citruss
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }
    override fun onCreateView() {
        initView()
        clickListener()
        initObserver()
        initAdapter()

        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)

        noteId = intent.getStringExtra("noteId").toString()
        val hashMap = hashMapOf<String, Any>(
            "id" to noteId,
        )
        viewmodel.getNotesApi(GET_NOTE_API, hashMap)

    }

    private fun initView() {
        /*val windowBackground = window.decorView.background
        val blurView = binding.blurView
        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setupWith(rootView, RenderEffectBlur())
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(4f)
        }*/
    }

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.iv_back -> {
                    super.onBackPressedDispatcher.onBackPressed()
                }
                R.id.tvAddFavorite -> {

                    val requestMap = hashMapOf<String, Any>(
                        "id" to noteId,
                        "type" to "note",
                    )
                    viewmodel.addToFavoriteApi(ADD_TO_FAVORITE_API, requestMap)

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
                        GET_NOTE_API -> {
                            try {
                                Log.d("response", "NoteModel: ${Gson().toJson(it)}")
                                val response: NoteModel? = parseJson(it.data.toString())
                                Log.d("response", "NoteModel : ${response?.success}")

                                if (response?.success == true) {

                                    if (!response.data?.perfumes.isNullOrEmpty()) {

                                        similarPerfumeList = response.data?.perfumes
                                        similarPerfumeAdapter.list = similarPerfumeList
                                        similarPerfumeAdapter.notifyDataSetChanged()

                                        binding.rvSimilarPerfumes.visibility = View.VISIBLE
                                        binding.similarNoDataFound.visibility = View.GONE
                                    }else{
                                        binding.rvSimilarPerfumes.visibility = View.GONE
                                        binding.similarNoDataFound.visibility = View.VISIBLE
                                    }

                                    val note =response.data?.note

                                    val url  = note?.image
                                    if (url != null) {
                                        val imageUrl = if (url.contains("http")) {
                                            url
                                        } else {
                                            "${Constants.BASE_URL_IMAGE}$url"
                                        }
                                        Glide.with(this).load(imageUrl).into(binding.perfumeImg)
                                    } else {
                                        Glide.with(this).load(R.drawable.earn_badge_img).into(binding.perfumeImg)
                                    }

                                    binding.apply {
                                        tvName.text = note?.name
                                        tvType.text = note?.group

                                        isFavorite = note?.isFavorite ?: false

                                        if (isFavorite) {
                                            binding.tvAddFavorite.text = ContextCompat.getString(
                                                this@PerfumerCitrussActivity, R.string.addedInFavorite
                                            )
                                        } else {
                                            binding.tvAddFavorite.text = ContextCompat.getString(
                                                this@PerfumerCitrussActivity, R.string.add_to_favourite
                                            )
                                        }

                                        tvDesc.text = note?.odorProfile.toString()

                                    }

                                } else {

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
                                        binding.tvAddFavorite.text = ContextCompat.getString(
                                            this@PerfumerCitrussActivity, R.string.addedInFavorite
                                        )
                                    } else {
                                        binding.tvAddFavorite.text = ContextCompat.getString(
                                            this@PerfumerCitrussActivity, R.string.add_to_favourite
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

    private fun initAdapter() {



        similarPerfumeAdapter = SimilarPerfumeAdapter(
            this, similarPerfumeList, object : SimilarPerfumeAdapter.ClickListener {
                override fun onClick(
                    position: Int, model: SimilarPerfume
                ) {
                    val intent =
                        Intent(this@PerfumerCitrussActivity, PerfumeInfoActivity::class.java).apply {
                            putExtra("perfumeId", model.id)
                        }
                    startActivity(intent)
                }
            })
        binding.rvSimilarPerfumes.adapter = similarPerfumeAdapter

    }
}