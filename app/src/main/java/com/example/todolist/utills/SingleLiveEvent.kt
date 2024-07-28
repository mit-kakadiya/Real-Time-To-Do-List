package com.example.todolist.utills

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T>:MutableLiveData<T?>() {

    private var mPending:AtomicBoolean = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        super.observe(owner){
            if ( mPending.compareAndSet(true,false)){
                observer.onChanged(it)
            }
        }
    }

    override fun setValue(value: T?) {
        mPending.set(true)
        super.setValue(value)
    }

    @MainThread
    fun call(){
        value = null
    }
}