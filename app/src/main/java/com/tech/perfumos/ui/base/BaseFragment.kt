package com.tech.perfumos.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.tech.perfumos.BR
import com.tech.perfumos.data.local.SharedPrefManager
import com.tech.perfumos.room_data.AppDb
import com.tech.perfumos.utils.hideKeyboard
import javax.inject.Inject


abstract class BaseFragment<Binding : ViewDataBinding> : Fragment() {
    lateinit var binding: Binding
    val parentActivity: BaseActivity<*>?

        get() = activity as? BaseActivity<*>
    var db: AppDb? = null


    @Inject
    lateinit var sharedPrefManager: SharedPrefManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateView(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.inflate(layoutInflater, layout, container, false)
        binding.setVariable(BR.vm, getViewModel())
        db = Room.databaseBuilder(
            requireActivity(), AppDb::class.java, requireActivity().applicationContext.packageName
        ).build()
        return binding.root
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView(view: View)
    override fun onPause() {
        super.onPause()
        activity?.hideKeyboard()
    }

    fun showLoading(s: String?) {
        parentActivity?.showLoading(s)
    }

    fun hideLoading() {
        parentActivity?.hideLoading()
    }

    fun showLoading(s: Int) {
        parentActivity?.showLoading(getString(s))
    }

    open fun onBackPressed() {
        findNavController().popBackStack()
    }

}