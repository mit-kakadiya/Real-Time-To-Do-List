package com.example.todolist.ui.activity


import android.app.AlertDialog
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.common.utils.Utils
import com.example.todolist.databinding.ActivityListBinding


class ListActivity : BaseActivity<ActivityListBinding>() {

    private lateinit var mParentNavController: NavController

    override fun inflateLayout(layoutInflater: LayoutInflater): ActivityListBinding {
        return ActivityListBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        mParentNavController = navHostFragment.navController
    }

    override fun setUpClickEvents() {

    }

    override fun onObservers() {

    }

    private fun showExitDialog(message:String, onYesClick:()->Unit){
        AlertDialog.Builder(this).apply {
            setTitle("Exit app")
            setMessage(message)
            setPositiveButton("Yes"){_,_ ->
               onYesClick()
            }
            setNegativeButton("No"){dialog,_->
                dialog.dismiss()
            }
            create()
            show()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when(mParentNavController.currentDestination?.id ?:0){
            Utils.UpdateFragmentId ->{
                showExitDialog("Are you sure you want to exit with out saving TODO list?"){
                    super.onBackPressed()
                    mParentNavController.popBackStack()
                }
            }
            Utils.homeFragmentId->{
                showExitDialog("Are you sure you want to exit?"){
                    super.onBackPressed()
                    finishAffinity()
                }
            }
        }
    }
}