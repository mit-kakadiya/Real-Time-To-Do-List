package com.example.todolist.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.model.SaveList
import com.example.todolist.model.ToDoList
import com.example.todolist.R
import com.example.todolist.adapter.ListViewAdapter
import com.example.todolist.common.constants.Constants
import com.example.todolist.common.utils.Utils
import com.example.todolist.databinding.FragmentHomePageBinding
import com.example.todolist.listeners.ClickOnTaskListener
import com.example.todolist.receiver.NotificationBroadCastReceiver
import com.example.todolist.ui.activity.AuthenticationActivity
import com.example.todolist.ui.activity.ListActivity
import com.example.todolist.ui.viewModel.ListViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomePageFragment : BaseFragment<FragmentHomePageBinding>(), ClickOnTaskListener {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val dbCourse = auth.currentUser?.uid?.let { db.collection(it) }
    private val mListViewModel: ListViewModel by viewModels()
    private var mListViewAdapter: ListViewAdapter? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var currentNightMode: Int? = null
    override fun inflateLayout(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomePageBinding {
        return FragmentHomePageBinding.inflate(layoutInflater)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {
        currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        binding.listVm = mListViewModel
        getListFromFireStore()
        setUpToolbar()
        updateData()
        setSwipeToDelete()
        Utils.homeFragmentId = findNavController().currentDestination?.id?: 0
        sharedPreferences = requireActivity().getSharedPreferences(
            Constants.PREFERENCE,
            Context.MODE_PRIVATE
        )
    }

    private fun setSwipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = mListViewModel.taskList.value?.get(viewHolder.adapterPosition)
                val restoreItem = SaveList(
                    deletedItem?.title,
                    deletedItem?.description,
                    deletedItem?.date,
                    deletedItem?.day,
                    deletedItem?.type,
                    deletedItem?.priority,
                    deletedItem?.dateWithDay,
                    deletedItem?.priorityInWords
                )
                deletedItem?.id?.let { mListViewModel.deleteList(it) }
                mListViewModel.taskList.value?.removeAt(viewHolder.adapterPosition)
                binding.rvData.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                deletedItem?.title?.let {
                    Snackbar.make(binding.rvData, "$it deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo") {
                            mListViewModel.createList(restoreItem)
                            getListFromFireStore()
                        }.show()
                }
                getListFromFireStore()
            }
        }).attachToRecyclerView(binding.rvData)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateData() {
        dbCourse?.addSnapshotListener { _, e ->
            if (e != null) {
                return@addSnapshotListener
            } else {
                mListViewModel.readData()
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarmManager(selectedTimeInMills: Long) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(requireContext(), NotificationBroadCastReceiver::class.java).let {
            PendingIntent.getBroadcast(requireContext(), 0, it, PendingIntent.FLAG_MUTABLE)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, selectedTimeInMills, alarmIntent)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpToolbar() {
        (activity as ListActivity).apply {
            this.getBindingFromActivity().apply {
                layoutToolbar.apply {
                    tvTitle.visibility = View.VISIBLE
                    tvTitle.text = getString(R.string.to_do_list)
                    ivBack.visibility = View.INVISIBLE
                    ivLogout.visibility = View.VISIBLE
                    ivLogout.setOnClickListener {
                        sharedPreferences.edit().putBoolean(Constants.IS_LOGIN, false).apply()
                        Intent(requireContext(), AuthenticationActivity::class.java).apply {
                            startActivity(this)
                            finishAffinity()
                        }
                    }
                }
            }
        }
        setUpTheme()
    }

    private fun setUpTheme() {
        (activity as ListActivity).apply {
            this.getBindingFromActivity().apply {
                layoutToolbar.apply {
                    if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                        ivSwitchTheme.setImageResource(R.drawable.ic_night)
                    } else {
                        ivSwitchTheme.setImageResource(R.drawable.ic_light)
                    }
                    ivSwitchTheme.visibility = View.VISIBLE
                    ivSwitchTheme.setOnClickListener {
                        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    }
                }
            }
        }
    }

    private fun emptyListViewShow() {
        binding.rvData.visibility = View.GONE
        (activity as ListActivity).getBindingFromActivity().apply {
            emptyBg.visibility = View.VISIBLE
            tvEmptyList.visibility = View.VISIBLE
        }
    }

    private fun emptyListViewHide() {
        binding.rvData.visibility = View.VISIBLE
        (activity as ListActivity).getBindingFromActivity().apply {
            tvEmptyList.visibility = View.GONE
            emptyBg.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpAdapter(list: ArrayList<ToDoList>) {
        emptyListViewHide()
        mListViewAdapter = ListViewAdapter(requireContext(), list, this)
        mListViewAdapter!!.notifyDataSetChanged()
        binding.rvData.adapter = mListViewAdapter
    }

    private fun getListFromFireStore() {
        mListViewModel.readData()
        mListViewModel.taskList.observe(this) {
            if (!it.isNullOrEmpty()) {
                setUpAdapter(it)
            } else {
                emptyListViewShow()
            }
        }
    }

//    private fun onBackPressedInFragment(){
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner ,object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                Log.d("XXX","called2")
//                AlertDialog.Builder(requireContext()).apply {
//                    setTitle("Exit App")
//                    setMessage("Are you sure you want to exit?")
//
//                    setPositiveButton("Yes"){_,_ ->
//                        requireActivity().finishAffinity()
//                    }
//                    setNegativeButton("No"){dialog,_->
//                        dialog.dismiss()
//                    }
//                    create()
//                    show()
//                }
//            }
//        })
//    }

    override fun setUpClickEvents() {
        mListViewModel.addNotes.observe(this) {
            findNavController().navigate(R.id.action_homePageFragment_to_updateListFragment)
        }
    }

    override fun onObservers() {

    }

    override fun onTaskSelected(list: ToDoList) {
        val bundle = Bundle().apply {
            putParcelable(Constants.TODOLIST, list)
        }
        findNavController().navigate(R.id.action_homePageFragment_to_updateListFragment, bundle)
    }
}