package com.tech.perfumos.ui.dashboad.fragment_folder.articles_folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.ADD_REVIEW_API
import com.tech.perfumos.data.api.Constants.ARTICLE_API
import com.tech.perfumos.data.api.Constants.BASE_URL_IMAGE
import com.tech.perfumos.data.api.Constants.GET_PERFUMER_ALL_REVIEWS
import com.tech.perfumos.data.api.Constants.GET_PROFILE_API
import com.tech.perfumos.data.api.Constants.GET_PROFILE_REVIEW_API
import com.tech.perfumos.data.api.Constants.PERFUME_RECOMMENDATIONS_API
import com.tech.perfumos.data.model.GetAllReview
import com.tech.perfumos.data.model.SimpleDummyModel
import com.tech.perfumos.databinding.FragmentArticlesBinding
import com.tech.perfumos.databinding.ItemArticlesBinding
import com.tech.perfumos.databinding.MightLikePerfumeItemViewBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseFragment
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.camera_perfume.model.SimilarPerfume
import com.tech.perfumos.ui.camera_perfume.model.SimilarPerfumeModel
import com.tech.perfumos.ui.core.SearchClickListener

import com.tech.perfumos.ui.dashboad.DashboardActivity
import com.tech.perfumos.ui.dashboad.fragment_folder.articles_folder.model.RecommendationModel
import com.tech.perfumos.ui.dashboad.fragment_folder.articles_folder.model.RecommendationPerfumeList

import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.HomeFragmentVm
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileDataModel
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileViewModel
import com.tech.perfumos.ui.dashboad.fragment_folder.setting.SettingFragment
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils.formatReviewCount
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import com.tech.perfumos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ArticlesFragment : BaseFragment<FragmentArticlesBinding>() {
    private val viewModel: ArticlesFragmentVm by viewModels()

    private var articlesList : ArrayList<Article?>? = ArrayList()
    private var mightLikePerfumeList : ArrayList<RecommendationPerfumeList?>? = ArrayList()
    private var PAGE = 1
    private val LIMIT = 10
    private var isLoading = false
    private var isLastPage = false

    private var isMightLikeLastPage = false
    private var HORIZONTAL_PAGE = 1
    private val HORIZONTAL_LIMIT = 10

    private var searchClickListener: SearchClickListener? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_articles
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }


    override fun onCreateView(view: View) {
        binding.isSelected = 1
        setUpRecyclerview()
        clickListener()
        initObserver()
        getArticles()

    }

    private fun getArticles() {
        viewModel.getArticleWithPagination(
            ARTICLE_API,
            hashMapOf(
                "page" to PAGE,
                "limit" to LIMIT,
            )
        )

        val requestMap = hashMapOf<String, Any>(
            "page" to HORIZONTAL_PAGE,
            "limit" to HORIZONTAL_LIMIT
        )
        viewModel.postRecommendationApi(PERFUME_RECOMMENDATIONS_API, requestMap)
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

                        ARTICLE_API -> {
                            try {
                                Log.d("ProfileDataModel", "ARTICLE_API: ${Gson().toJson(it)}")
                                val data: ArticleModel? = parseJson(it.data.toString())
                                Log.d("ProfileDataModel", "ARTICLE_API : ${data?.success}")

                                if (data?.success == true) {
                                    if(!data.data?.articles.isNullOrEmpty()){
                                       /* articlesList = data.data?.articles
                                        articlesAdapter.list = articlesList
                                        articlesAdapter.notifyDataSetChanged()*/
                                        binding.noArticlesFound.visibility = View.GONE

                                        val newArticlesList = data.data?.articles ?: emptyList()

                                        // Pagination metadata
                                        val pagination = data.data?.pagination
                                        if (pagination != null) {
                                            isLastPage = (pagination.perPage?.let { it1 ->
                                                pagination.currentPage?.times(
                                                    it1
                                                )
                                            } ?: 0) >= (pagination.totalCount ?: 0)

//                                        isLastPage =if((pagination.totalCount?:0) == reviewAdapter.list.size) true else false
                                        }
                                        if (PAGE == 1) {
                                            articlesList?.clear()
                                        }

                                        articlesList?.addAll(newArticlesList)
                                        articlesAdapter.list = articlesList
                                        articlesAdapter.notifyDataSetChanged()
                                        Log.d("mightLikePerfumeList?", "initObserver: ${mightLikePerfumeList?.size}, adapter list size ${likePerfumeAdapter.list.size}")


                                    }
                                    else{
                                        binding.noArticlesFound.visibility = View.VISIBLE
                                       // showErrorToast("Something went wrong!!")

                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        PERFUME_RECOMMENDATIONS_API -> {
                            try {
                                Log.d("PERFUME_RECOMMENDATIONS_API", "RecommendationModel: ${Gson().toJson(it)}")
                                val response: RecommendationModel? = parseJson(it.data.toString())

                                if (response?.success == true) {
                                    val newPerfumeList = response.data ?: emptyList()

                                    // Pagination metadata
                                    val pagination = response.pagination
                                    if (pagination != null) {
                                        isMightLikeLastPage = (pagination.perPage?.let { it1 ->
                                            pagination.currentPage?.times(
                                                it1
                                            )
                                        } ?: 0) >= (pagination.totalCount ?: 0)

//                                        isLastPage =if((pagination.totalCount?:0) == reviewAdapter.list.size) true else false
                                    }
                                    if (HORIZONTAL_PAGE == 1) {
                                        mightLikePerfumeList?.clear()
                                    }

                                    mightLikePerfumeList?.addAll(newPerfumeList)
                                    likePerfumeAdapter.list = mightLikePerfumeList
                                    //likePerfumeAdapter.addToList(mightLikePerfumeList)
                                    likePerfumeAdapter.notifyDataSetChanged()
                                    Log.d("mightLikePerfumeList?", "initObserver: ${mightLikePerfumeList?.size}, adapter list size ${likePerfumeAdapter.list.size}")


                                } else {
                                    showToast(response?.message.toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isLoading = false
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
                            startActivity(Intent(requireActivity(), LoginActivity::class.java))
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
                R.id.toggleSwitch -> {
                    binding.isSelected = if (binding.isSelected == 1) 2 else 1
                    Log.d("isSelected", "clickListener: ${binding.isSelected}")
                    val dark = binding.isSelected == 1
                    //searchClickListener?.onThemeSwitch(dark)

                }

                R.id.userNameSignUp->{
                    showLoading("under development")
                }
                R.id.settingIcon->{
                    loadFragment(SettingFragment())
                }
            }
        }
    }
    private lateinit var articlesAdapter: SimpleRecyclerViewAdapter<Article, ItemArticlesBinding>
    private lateinit var likePerfumeAdapter: SimpleRecyclerViewAdapter<RecommendationPerfumeList, MightLikePerfumeItemViewBinding>
    private fun setUpRecyclerview() {

        likePerfumeAdapter = SimpleRecyclerViewAdapter(
            R.layout.might_like_perfume_item_view, BR.bean
        ) { v, m, pos ->
            when(v?.id){
                R.id.clMain ->{
                    searchClickListener?.openPerfumeScreen(m.id.toString())
                }
            }

        }
        binding.rvMightLike.adapter = likePerfumeAdapter
        likePerfumeAdapter.list = mightLikePerfumeList


        articlesAdapter = SimpleRecyclerViewAdapter(
            R.layout.item_articles, BR.bean
        ) { v, m, pos ->

            val intent = Intent(requireActivity(), ArticleContent::class.java).apply {
                putExtra("article", m)
            }
            startActivity(intent)


        }
        binding.rvArticles.adapter = articlesAdapter
        articlesAdapter.list = articlesList


        binding.nestedScrollview.setOnScrollChangeListener { v: NestedScrollView, _, _, scrollX, oldScrollX ->
            if (!v.canScrollVertically(1)) {
                // Reached the bottom
                if (!isLoading && !isLastPage) {

                    PAGE++
                    isLoading = true
                    getArticles()

                }
            }
        }

        binding.rvMightLike.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isMightLikeLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        HORIZONTAL_PAGE++
                        isLoading = true

                        val requestMap = hashMapOf<String, Any>(
                            "page" to HORIZONTAL_PAGE,
                            "limit" to HORIZONTAL_LIMIT
                        )
                        viewModel.postRecommendationApi(PERFUME_RECOMMENDATIONS_API, requestMap)

                    }
                }
            }
        })
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
    }

}