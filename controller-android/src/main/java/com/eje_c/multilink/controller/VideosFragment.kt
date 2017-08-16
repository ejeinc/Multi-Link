package com.eje_c.multilink.controller

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.eje_c.multilink.controller.db.VideoEntity
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.ViewById

@EFragment(R.layout.fragment_videos)
open class VideosFragment : Fragment() {

    @ViewById
    lateinit var recyclerView: RecyclerView
    @ViewById
    lateinit var videoCount: TextView

    @AfterViews
    fun init() {

        val adapter = VideoListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        App.db.videoDao.query().observe(this, Observer<List<VideoEntity>> { data ->

            if (data != null) {

                // Show videos
                adapter.list = data

                // Show count of videos
                videoCount.text = data.size.toString()
            }
        })
    }
}