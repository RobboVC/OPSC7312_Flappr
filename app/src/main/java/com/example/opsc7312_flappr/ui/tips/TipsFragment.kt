package com.example.opsc7312_flappr.ui.tips

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.opsc7312_flappr.CustomizedExpandableListAdapter
import com.example.opsc7312_flappr.ExpandableListDataItems
import com.example.opsc7312_flappr.R
import java.util.*

class TipsFragment : Fragment() {

    private lateinit var expandableListViewExample: ExpandableListView
    private lateinit var expandableListAdapter: ExpandableListAdapter
    private lateinit var expandableTitleList: List<String>
    private lateinit var expandableDetailList: HashMap<String, List<String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_tips, container, false)

        // Find the ExpandableListView in your fragment's layout
        expandableListViewExample = root.findViewById(R.id.expandableListViewSample)

        // Initialize data for the ExpandableListView
        expandableDetailList = ExpandableListDataItems.getData()
        expandableTitleList = ArrayList(expandableDetailList.keys)

        // Set a custom indicator color (replace R.color.your_custom_color)
        val indicator = resources.getDrawable(android.R.drawable.ic_menu_more).mutate()
        indicator.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.text_purple),
            PorterDuff.Mode.SRC_IN
        )
        expandableListViewExample.setGroupIndicator(indicator)

        // Create and set the adapter
        expandableListAdapter = CustomizedExpandableListAdapter(
            requireContext(),
            expandableTitleList,
            expandableDetailList
        )
        expandableListViewExample.setAdapter(expandableListAdapter)


        return root
    }
}