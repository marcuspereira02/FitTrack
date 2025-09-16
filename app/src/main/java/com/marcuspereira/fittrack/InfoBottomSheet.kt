package com.marcuspereira.fittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoBottomSheet(val onClick: () -> Unit ) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.info_bottom_sheet, container, false)

        val btnCancel = view.findViewById<Button>(R.id.btn_cancel_info_bottom_sheet)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete_info_bottom_sheet)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnDelete.setOnClickListener {
            onClick.invoke()
            dismiss()
        }
        return view
    }
}