package com.andranikas.videoroom.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.andranikas.videoroom.R
import com.andranikas.videoroom.helpers.showNoSpaceAlertDialog
import com.andranikas.videoroom.media.MediaViewProvider
import com.andranikas.videoroom.permission.PermissionsFragment
import kotlinx.android.synthetic.main.fragment_video.*

class VideoRoomFragment : Fragment(), PermissionsFragment.OnPermissionFragmentInteractionListener {

    private lateinit var viewProvider: MediaViewProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_video, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewProvider = MediaViewProvider(
            resources.displayMetrics.heightPixels, resources.displayMetrics.widthPixels,
            layoutInflater, childFragmentManager, viewLifecycleOwner, container
        )
        viewProvider.addSelfieView()
        viewProvider.addVideoViews()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.video, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.add_video -> {
                viewProvider.addVideoView {
                    showNoSpaceAlertDialog(requireContext())
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        viewProvider.clear()
    }

    override fun onPermissionsGranted() {
        viewProvider.onPermissionsGranted()
    }

    override fun onPermissionsDenied() {
        viewProvider.onPermissionsDenied()
    }

    companion object {
        fun newInstance() =
            VideoRoomFragment()
    }
}