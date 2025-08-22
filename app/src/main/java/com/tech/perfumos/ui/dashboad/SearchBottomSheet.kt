package com.tech.perfumos.ui.dashboad

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.SEARCH_PERFUME_API
import com.tech.perfumos.data.model.SimpleDummyModel
import com.tech.perfumos.databinding.SheetSearchBinding
import com.tech.perfumos.databinding.TopSearchItemViewBinding
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.camera_perfume.PerfumeInfoActivity
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileDataModel
import com.tech.perfumos.ui.dashboad.model.HistoryPerfumeList
import com.tech.perfumos.ui.dashboad.model.SearchHistoryModel
import com.tech.perfumos.ui.dashboad.model.SearchModel
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderEffectBlur
import org.json.JSONObject

@AndroidEntryPoint
class SearchBottomSheet : BottomSheetDialogFragment() {

    private var searchList: SearchHistoryModel.SearchHistoryData? = null
    private var binding: SheetSearchBinding? = null
    private val viewmodel: DashboardVm by viewModels()

    private lateinit var recentSearchAdapter: SimpleRecyclerViewAdapter<HistoryPerfumeList, TopSearchItemViewBinding>
    private lateinit var topSearchAdapter: SimpleRecyclerViewAdapter<HistoryPerfumeList, TopSearchItemViewBinding>
    private lateinit var SearchAdapter: SimpleRecyclerViewAdapter<HistoryPerfumeList, TopSearchItemViewBinding>

    private val similarPerfumeList = ArrayList<SimpleDummyModel>()
    private var recentSearchList: ArrayList<HistoryPerfumeList?>? = ArrayList()
    private var topSearchList: ArrayList<HistoryPerfumeList?>? = ArrayList()
    private var filterSearchList: ArrayList<HistoryPerfumeList?>? = ArrayList()

    var onPerfumeClick: ((String) -> Unit)? = null

    companion object {
        private const val ARG_SEARCH_LIST = "searchList"

        fun newInstance(searchList: SearchHistoryModel.SearchHistoryData): SearchBottomSheet {
            val fragment = SearchBottomSheet()
            val args = Bundle()
            args.putString(ARG_SEARCH_LIST, Gson().toJson(searchList))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(ARG_SEARCH_LIST)?.let {
            searchList = Gson().fromJson(it, SearchHistoryModel.SearchHistoryData::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = SheetSearchBinding.inflate(inflater, container, false)
        initSimilarPerfumeList()
        initAdapter()
        initObserver()

        binding?.ivClose?.setOnClickListener { dismiss() }

        binding?.edSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    binding?.labelRecentSearch?.visibility = View.GONE
                    binding?.rvRecentSearch?.visibility = View.GONE
                    binding?.labelTopSearch2?.visibility = View.GONE
                    binding?.rvTopSearch?.visibility = View.GONE
                    binding?.rvSearch?.visibility = View.INVISIBLE

                    viewmodel.searchPerfumeApi(SEARCH_PERFUME_API, hashMapOf("search" to s.toString()))
                } else {
                    if (!recentSearchList.isNullOrEmpty()) {
                        recentSearchAdapter.list = recentSearchList
                        recentSearchAdapter.notifyDataSetChanged()
                        binding?.labelRecentSearch?.visibility = View.VISIBLE
                        binding?.rvRecentSearch?.visibility = View.VISIBLE
                    } else {
                        binding?.labelRecentSearch?.visibility = View.GONE
                        binding?.rvRecentSearch?.visibility = View.GONE
                    }

                    binding?.labelTopSearch2?.visibility = View.VISIBLE
                    binding?.rvTopSearch?.visibility = View.VISIBLE
                    binding?.noDataFound?.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val windowBackground = requireActivity().window.decorView.background
        val rootView = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val blurView = binding?.blurView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView?.setupWith(rootView, RenderEffectBlur())
                ?.setFrameClearDrawable(windowBackground)
                ?.setBlurRadius(16f)
        }

        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            peekHeight = 0
            skipCollapsed = true
        }
    }

    private fun initSimilarPerfumeList() {
        recentSearchList = searchList?.recentPerfumes
        topSearchList = searchList?.topPerfumes
    }

    private fun initAdapter() {
        recentSearchAdapter = SimpleRecyclerViewAdapter(
            R.layout.top_search_item_view, BR.bean
        ) { v, m, _ ->
            if (v?.id == R.id.clMain) {
                /*val intent = Intent(requireContext(), PerfumeInfoActivity::class.java)
                intent.putExtra("perfumeId", m.id.toString())
                startActivity(intent)*/
                onPerfumeClick?.invoke(m.id.toString())
            }
        }

        if (!recentSearchList.isNullOrEmpty()) {
            binding?.rvRecentSearch?.adapter = recentSearchAdapter
            recentSearchAdapter.list = recentSearchList
        } else {
            binding?.labelRecentSearch?.visibility = View.GONE
            binding?.rvRecentSearch?.visibility = View.GONE
        }

        topSearchAdapter = SimpleRecyclerViewAdapter(
            R.layout.top_search_item_view, BR.bean
        ) { v, m, _ ->
            if (v?.id == R.id.clMain) {
                /*val intent = Intent(requireContext(), PerfumeInfoActivity::class.java)
                intent.putExtra("perfumeId", m.id.toString())
                startActivity(intent)*/
                onPerfumeClick?.invoke(m.id.toString())
            }
        }

        binding?.rvTopSearch?.adapter = topSearchAdapter
        topSearchAdapter.list = topSearchList

        SearchAdapter = SimpleRecyclerViewAdapter(
            R.layout.top_search_item_view, BR.bean
        ) { v, m, _ ->
            if (v?.id == R.id.clMain) {
                /*val intent = Intent(requireContext(), PerfumeInfoActivity::class.java)
                intent.putExtra("perfumeId", m.id.toString())
                startActivity(intent)*/
                onPerfumeClick?.invoke(m.id.toString())
            }
        }

        binding?.rvSearch?.adapter = SearchAdapter
        SearchAdapter.list = filterSearchList
    }

    private fun initObserver() {
        viewmodel.commonObserver.observe(this) {
            when (it?.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    if (it.message == SEARCH_PERFUME_API) {
                        try {
                            val data: SearchModel? = parseJson(it.data.toString())
                            filterSearchList = data?.data?.perfumes

                            if (!filterSearchList.isNullOrEmpty()) {
                                SearchAdapter.list = filterSearchList
                                SearchAdapter.notifyDataSetChanged()
                                binding?.rvSearch?.visibility = View.VISIBLE
                                binding?.noDataFound?.visibility = View.GONE
                            } else {
                                binding?.rvSearch?.visibility = View.INVISIBLE
                                binding?.noDataFound?.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                Status.ERROR -> {
                    try {
                        val jsonObject = JSONObject(it.data.toString())
                        val message = jsonObject.getString("message")
                        showErrorToast(message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {}
            }
        }
    }
}
