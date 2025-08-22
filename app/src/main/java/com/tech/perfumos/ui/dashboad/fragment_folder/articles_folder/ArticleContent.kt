package com.tech.perfumos.ui.dashboad.fragment_folder.articles_folder


import android.content.Intent
import android.util.Log

import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson

import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.ARTICLE_API
import com.tech.perfumos.databinding.ActivityArticleContentBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.dashboad.DashboardActivity
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast

import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ArticleContent : BaseActivity<ActivityArticleContentBinding>() {

    val viewmodel: ArticleContentVm by viewModels()
    private lateinit var articleData: Article

    override fun getLayoutResource(): Int {
        return R.layout.activity_article_content
    }
    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }
    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initObserver()
       /* Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)*/
        if(intent.hasExtra("articleId")){
            articleData  = intent.getSerializableExtra("article") as Article
            setData()
        }else{

            //showErrorToast("Something went wrong!!")
        }

    }

    private fun setData() {
        binding.apply {
            headingTitle.text = articleData.title
            tvDescription.text = articleData.content
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

                        ARTICLE_API -> {
                            try {
                                Log.d("ProfileDataModel", "ARTICLE_API: ${Gson().toJson(it)}")
                                val data: ArticleModel? = parseJson(it.data.toString())
                                Log.d("ProfileDataModel", "ARTICLE_API : ${data?.success}")

                                if (data?.success == true) {
                                    if(!data.data?.articles.isNullOrEmpty()){
                                        val articlesList = data.data?.articles

                                    }
                                    else{
                                        showErrorToast("Something went wrong!!")

                                    }
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

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                 finish()
                }

                R.id.settingIcon -> {
                    Utils.routeToHomeDashboardActivity=3
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("open_fragment", "profile")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}