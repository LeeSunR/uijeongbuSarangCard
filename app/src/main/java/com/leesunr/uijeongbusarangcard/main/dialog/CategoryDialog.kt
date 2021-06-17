package com.leesunr.uijeongbusarangcard.main.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.uijeongbusarangcard.R
import com.leesunr.uijeongbusarangcard.main.dialog.adapter.Category
import com.leesunr.uijeongbusarangcard.main.dialog.adapter.CategoryAdapter
import kotlinx.android.synthetic.main.dialog_category.view.*
import org.json.JSONObject

class CategoryDialog(private val category:JSONObject) : DialogFragment() {

    private var categorySelectedListener:((String?,String?)->Unit)? = null
    private var mainCategoryAdapter:CategoryAdapter? = null
    private var subCategoryAdapter:CategoryAdapter? = null
    private var dialogView:View? = null

    fun setCategorySelectedListener(listener:((String?,String?)->Unit)?){
        categorySelectedListener = listener
    }

    private val mainCategory by lazy {
        val mainCategory = ArrayList<String>()
        val keys = category.keys()
        while (keys.hasNext()){
            mainCategory.add(keys.next())
        }
        return@lazy mainCategory
    }

    private val subCategory = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialogView = inflater.inflate(R.layout.dialog_category, container)

        dialogView!!.rcy_main_category.apply {
            layoutManager = LinearLayoutManager(context)
            mainCategoryAdapter = CategoryAdapter(mainCategory.map { Category(it) })
            mainCategoryAdapter!!.itemClickListener { position ->
                dialogView!!.rcy_sub_category.adapter?.apply {
                    subCategory.clear()
                    val sub = category.getJSONArray(mainCategory[position])
                    for (i in 0 until sub.length()){
                        subCategory.add(Category(sub[i].toString()))
                    }
                    notifyDataSetChanged()
                }
                categoryUpdate()
            }
            adapter = mainCategoryAdapter
            adapter?.notifyDataSetChanged()
        }

        dialogView!!.rcy_sub_category.apply {
            layoutManager = LinearLayoutManager(context)
            subCategoryAdapter = CategoryAdapter(subCategory)
            adapter = subCategoryAdapter
            subCategoryAdapter!!.itemClickListener {
                categoryUpdate()
            }
            adapter?.notifyDataSetChanged()
        }

        dialogView!!.btn_select_done.setOnClickListener {
            var main = mainCategoryAdapter!!.getSelectedItem()
            val sub = subCategoryAdapter!!.getSelectedItem()
            if(main=="전체") main = null

            categorySelectedListener?.invoke(main,sub)
            this.dismiss()
        }

        return dialogView
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDismiss(dialog: DialogInterface) {
        Log.e(tag,"onDismiss")
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        Log.e(tag,"onCancel")
        super.onCancel(dialog)
    }

    @SuppressLint("SetTextI18n")
    private fun categoryUpdate(){
        val main = mainCategoryAdapter!!.getSelectedItem()
        val sub = subCategoryAdapter!!.getSelectedItem()
        if(sub!=null) dialogView!!.text_selected.text = "$main - $sub"
        else dialogView!!.text_selected.text = "$main"
    }
}