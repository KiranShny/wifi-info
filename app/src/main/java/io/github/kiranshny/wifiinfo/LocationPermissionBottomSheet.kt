package io.github.kiranshny.wifiinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.florent37.runtimepermission.PermissionResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_location_bottom_sheet.view.*

class LocationPermissionBottomSheet : BottomSheetDialogFragment() {
    private lateinit var contentView: View
    private var actionBlock: (PermissionResult) -> Unit = {}
    private var actionBlockForFirstTimeRequest: () -> Unit = {}
    private lateinit var permissionResult: PermissionResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        contentView = inflater.inflate(
            R.layout.layout_location_bottom_sheet, container, false
        )
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(
            title = resources.getString(R.string.location_permission_enable_title),
            desc = resources.getString(R.string.location_permission_enable_desc),
            action = resources.getString(R.string.enable)
        )
    }

    fun withAction(block: (PermissionResult) -> Unit) {
        actionBlock = block
    }

    fun withActionForFirstTimeRequest(block: () -> Unit) {
        actionBlockForFirstTimeRequest = block
    }

    fun setUp(
        title: String,
        desc: String,
        action: String
    ) {
        contentView.tvTitle.text = title
        contentView.tvSubTitle.text = desc
        contentView.buttonLocation.text = action
        contentView.buttonLocation.setOnClickListener {
            if (::permissionResult.isInitialized) {
                actionBlock(permissionResult)
            } else {
                actionBlockForFirstTimeRequest()
            }
        }
    }

    fun show(fragmentManager: FragmentManager, pr: PermissionResult) {
        permissionResult = pr
        show(fragmentManager, this.tag)
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, this.tag)
    }
}