package co.winds.utils

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.custom_toolbar.*


object Utils {

    //toobar
    fun setToolbar(context: AppCompatActivity, toolbar : Toolbar, i:String){
        context.setSupportActionBar(toolbar)
        context.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        context.supportActionBar!!.setHomeButtonEnabled(true)
        context.tv_title_center.text=i
    }

    //Fragment toobar
    fun setToolbarFragment(context: AppCompatActivity, toolbar : Toolbar, i:String){
        context.setSupportActionBar(toolbar)
        context.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        context.supportActionBar!!.setHomeButtonEnabled(true)
        context.tv_title_center.text=i
    }



}






