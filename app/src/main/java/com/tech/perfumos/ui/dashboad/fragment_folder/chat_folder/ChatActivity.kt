package com.tech.perfumos.ui.dashboad.fragment_folder.chat_folder
import android.content.Intent
import android.window.OnBackInvokedDispatcher
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tech.perfumos.R
import com.tech.perfumos.databinding.ActivityChatFragmentBinding
import com.tech.perfumos.databinding.ChatItemViewModelBinding
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.BR
import com.tech.perfumos.ui.dashboad.DashboardActivity
import com.tech.perfumos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatFragmentBinding>() {
    val viewmodel: ChatFragmentVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_chat_fragment
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        initAdapter()
        clickListener()
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
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private lateinit var chatAdapter: SimpleRecyclerViewAdapter<ChatModel, ChatItemViewModelBinding>
    private fun initAdapter() {
        val itemListData = ArrayList<ChatModel>()
        itemListData.add(ChatModel("Hello Whizzy,how are you today?","",1))
        itemListData.add(ChatModel("Hello,i’m fine,how can i help you?","",2))
        itemListData.add(ChatModel("Lorem ipsum dolor sit amet consectetur. Aenean morbi donec nunc nibh porttitor. Nulla arcu eget elit molestie scelerisque. Aliquet dui ornare facilisi vivamus sit mauris cras sed. Interdum lacinia fermentum arcu vel.","",1))
        itemListData.add(ChatModel("Lorem ipsum dolor sit amet consectetur. Aenean morbi donec nunc nibh porttitor. Nulla arcu eget elit molestie scelerisque. Aliquet dui ornare facilisi vivamus sit mauris cras sed. Interdum lacinia fermentum arcu vel.","",2))

        chatAdapter = SimpleRecyclerViewAdapter(
            R.layout.chat_item_view_model, BR.bean
        ) { v, m, pos ->
            when (v.id) {

            }
        }
        chatAdapter.list=itemListData

        binding.chatRv.adapter = chatAdapter


    }




    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
        super.onBackPressed()

    }

}