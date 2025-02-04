package com.ssafy.reper.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.local.HomeAnnouncementModel
import com.ssafy.reper.data.local.HomeBannerModel

class RVHomeAnnouncement (val List: MutableList<HomeAnnouncementModel>) : RecyclerView.Adapter<RVHomeAnnouncement.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RVHomeAnnouncement.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_home_accouncement_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RVHomeAnnouncement.ViewHolder, position: Int) {

        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: HomeAnnouncementModel) {

            val rv_title = itemView.findViewById<TextView>(R.id.rv_home_announcement_title)
            val rv_time = itemView.findViewById<TextView>(R.id.rv_home_announcement_time)

            rv_title.text = item.title
            rv_time.text = item.time

        }
    }

}
