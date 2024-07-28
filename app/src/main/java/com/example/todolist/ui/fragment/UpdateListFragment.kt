package com.example.todolist.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todolist.model.SaveList
import com.example.todolist.model.ToDoList
import com.example.todolist.R
import com.example.todolist.common.constants.Constants
import com.example.todolist.common.utils.Utils
import com.example.todolist.databinding.FragmentUpdateListBinding
import com.example.todolist.ui.activity.ListActivity
import com.example.todolist.ui.viewModel.UpdateListViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class UpdateListFragment: BaseFragment<FragmentUpdateListBinding>() {

    private var mTitle: String? = null
    private var mDescription: String? = null
    private var mDate: String? = null
    private var mDay: String? = null
    private var mDateWithDay: String? = null
    private var mPriorityInWords: String? = null
    private var mType: String? = null
    private var mPriority: Int? = null
    private lateinit var mPriorityList: Array<String>
    private val dateWithDayFormat = SimpleDateFormat("dd/MM, EEE", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    private var parableList: ToDoList? = null
    private val mUpdateListViewModel: UpdateListViewModel by viewModels()
    private val c: Calendar = Calendar.getInstance()
    private val hour = c.get(Calendar.HOUR)
    private val minute = c.get(Calendar.MINUTE)
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)

    override fun inflateLayout(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpdateListBinding {
        return FragmentUpdateListBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpToolbar() {
        (activity as ListActivity).apply {
            this.getBindingFromActivity().apply {
                layoutToolbar.apply {
                    ivBack.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            AlertDialog.Builder(requireContext()).apply {
                                setTitle("Exit App")
                                setMessage("Are you sure you want to exit with out saving TODO list?")

                                setPositiveButton("Yes"){_,_ ->
                                    findNavController().popBackStack()
                                }
                                setNegativeButton("No"){dialog,_->
                                    dialog.dismiss()
                                }
                                create()
                                show()
                            }
                        }
                    }
                    ivSwitchTheme.visibility = View.GONE
                    tvEmptyList.visibility = View.GONE
                    emptyBg.visibility = View.GONE
                    ivLogout.visibility = View.INVISIBLE
                    tvTitle.visibility = View.VISIBLE
                    tvTitle.text = "To-Do List"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getListFromBundle() {
        parableList = arguments?.getParcelable(Constants.TODOLIST)
        if (parableList != null) {
            mUpdateListViewModel.apply {
                titleTxt.value = parableList!!.title
                descriptionTxt.value = parableList!!.description
                typeTxt.value = parableList!!.type
                mDate = parableList!!.date
                mDay = parableList!!.day
                mDateWithDay = parableList!!.dateWithDay
                mPriority = parableList!!.priority
                mPriorityInWords = parableList!!.priorityInWords
                binding.datePicker.text = parableList!!.dateWithDay
                binding.autoCompleteTextView.setText(parableList!!.priorityInWords, false)
                if (mDateWithDay.isNullOrEmpty()){
                    binding.datePicker.text = "Date"
                }
                if (mPriorityInWords.isNullOrEmpty()){
                    binding.autoCompleteTextView.setText("priority")
                }
            }
        }
    }

    override fun initView() {
        getListFromBundle()
        binding.updateListVm = mUpdateListViewModel
        mPriorityList = resources.getStringArray(R.array.priority)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, mPriorityList)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        setUpToolbar()
        Utils.UpdateFragmentId = findNavController().currentDestination?.id?: 0
        setUpPriorityHint()
    }

    private fun setUpPriorityHint() {
        if (binding.autoCompleteTextView.text != null) {
            binding.textInputLayout.hint = null
        } else {
            binding.textInputLayout.hint = "Priority"
        }
    }

    override fun setUpClickEvents() {
        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            mPriorityInWords = mPriorityList[position]
            mPriority = position
        }
        mUpdateListViewModel.apply {
            datePickerBtn.observe(viewLifecycleOwner) {
                handleDatePickerClick()
            }
            saveListBtn.observe(viewLifecycleOwner) {
                handleSaveButtonClick()
            }
            timePickerBtn.observe(viewLifecycleOwner){
                handleTimePickerClick()
            }
        }
    }

    private fun handleTimePickerClick(){
        val timePickerDialog = TimePickerDialog(requireContext(),{view,hourOfDay,minute ->
                                                                 Log.d("XXX","$hourOfDay $minute")
        },hour,minute,false)
        timePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun handleDatePickerClick() {


        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, nYear, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(nYear, monthOfYear, dayOfMonth)
                }
                mDateWithDay = dateWithDayFormat.format(selectedDate.time)
                mDay = dayFormat.format(selectedDate.time)
                mDate = dateFormat.format(selectedDate.time)
                binding.datePicker.text = mDateWithDay
            }, year, month, day)
        datePickerDialog.show()
    }

//    private fun onBackPressedInFragment(){
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner ,object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                Log.d("XXX","called2")
//                AlertDialog.Builder(requireContext()).apply {
//                    setTitle("Exit App")
//                    setMessage("Are you sure you want to exit with out saving TODO list?")
//
//                    setPositiveButton("Yes"){_,_ ->
//                       findNavController().popBackStack()
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

    private fun handleSaveButtonClick() {
        val task = SaveList(
            mTitle,
            mDescription,
            mDate,
            mDay,
            mType,
            mPriority,
            mDateWithDay,
            mPriorityInWords
        )
        if (parableList?.id != null) {
            mUpdateListViewModel.updateData(parableList?.id!!, task, requireContext())
        } else {
            mUpdateListViewModel.createList(task, requireContext())
        }
        findNavController().popBackStack()
    }

    override fun onObservers() {
        mUpdateListViewModel.apply {
            titleTxt.observe(viewLifecycleOwner) {
                mTitle = it
            }
            descriptionTxt.observe(viewLifecycleOwner) {
                mDescription = descriptionTxt.value
            }
            typeTxt.observe(viewLifecycleOwner) {
                mType = typeTxt.value
            }
        }
    }

}