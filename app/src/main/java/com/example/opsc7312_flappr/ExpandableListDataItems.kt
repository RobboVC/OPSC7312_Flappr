package com.example.opsc7312_flappr

import java.util.ArrayList
import java.util.HashMap

object ExpandableListDataItems {
    fun getData(): HashMap<String, List<String>> {
        val expandableDetailList: HashMap<String, List<String>> = HashMap()

        // Populating the items/expanded view body to appear in the expanded view
        val essentials: MutableList<String> = ArrayList()
        essentials.add("- Bringing a hat to shield eyes from the sun so that you can see birds flying in the sky")
        essentials.add("- Your preferred, dependable pair of binoculars")
        essentials.add("- Your preferred bird guide")
        essentials.add("- Walking/Hiking shoes")
        essentials.add("- Patience (for good measure!)")

        val guideBook: MutableList<String> = ArrayList()
        guideBook.add("A bird guide book is a book filled with the necessary bird information to 'ID' a specific bird. A bird guide book will normally consist of; a detailed picture of the bird, an in-depth description of the bird, where and how to find the bird, and how each bird differs from another of the same species.")

        val general: MutableList<String> = ArrayList()
        general.add("- Remember to look down (for safety purposes and for grounded birds")
        general.add("- The optimal birding hours are early morning and early evening as those are the hours of the most bird activity")
        general.add("- Pay close attention to the size of the bird you are trying to ID as this in some cases can be the key distinguisher between different birds, consult your bird guide book")
        general.add("- Once your bird is spotted, ensure that you do not rush towards the bird or make any sudden movements that could spook the bird away")
        general.add("- 'Scan' often as many birds are stationary birds")

        val scanning: MutableList<String> = ArrayList()
        scanning.add("Scanning is the act of steadying yourself in a position with your binoculars to your eyes (preferable but not necessary) and moving slowly from left to right scanning the vacinity so as to not miss obvious or unobvious birds in the area. This is neccesary as some birds do not move unless provoked.")

        val idTechniques: MutableList<String> = ArrayList()
        idTechniques.add("- Pay close attention to the bird's eye colour (the ring around and the actual colour), size and tail length")
        idTechniques.add("- Take note of the bird's flight pattern")
        idTechniques.add("- Does the bird seem to be singular, in pairs or in a group?")
        idTechniques.add("- Is the bird walking or hopping?")
        idTechniques.add("- Is the bird on the ground? In a tree?)")
        idTechniques.add("- Is the bird fast moving or slow moving? (pertaining to the bird's general activity not the speed of the bird flying)")
        idTechniques.add("- Take note of the bird's leg and beak colour")

        // Adding to list and adding headers
        expandableDetailList["Essentials for Birding"] = essentials
        expandableDetailList["What is a 'Bird Guide' book?"] = guideBook
        expandableDetailList["General Tips"] = general
        expandableDetailList["What is 'Scanning?'"] = scanning
        expandableDetailList["Different Iding Techniques"] = idTechniques
        return expandableDetailList
    }
}