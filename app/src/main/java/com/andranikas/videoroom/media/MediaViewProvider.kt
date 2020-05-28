package com.andranikas.videoroom.media

import android.annotation.SuppressLint
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.andranikas.videoroom.R
import com.andranikas.videoroom.camera.SelfieView
import com.andranikas.videoroom.video.VideoView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.*
import kotlin.random.Random

class MediaViewProvider(
   heightPixels: Int,
   widthPixels: Int,
   private val layoutInflater: LayoutInflater,
   private val childFragmentManager: FragmentManager,
   private val viewLifecycleOwner: LifecycleOwner,
   private val container: ConstraintLayout
) {

   private var itemMaxHeight = heightPixels / ITEMS_IN_HEIGHT
   private var itemMaxWidth = widthPixels / ITEMS_IN_WIDTH
   private var itemHeight = itemMaxHeight / 2
   private val reservedCells = mutableSetOf<Pair<Int, Int>>()
   private val uiScope = CoroutineScope(Dispatchers.Main)
   private lateinit var selfieView: SelfieView
   private lateinit var selfieCard: MaterialCardView

   init {
      setup()
   }

   fun addSelfieView() {
      uiScope.launch {
         withContext(Dispatchers.Default) {
            selfieCard = layoutInflater.inflate(R.layout.item_selfie_view, container, false) as MaterialCardView
            selfieCard.layoutParams = FrameLayout.LayoutParams(itemHeight, itemHeight)
            selfieCard.radius = (itemHeight / 2).toFloat()
            selfieView = selfieCard.children.first() as SelfieView
            selfieView.setup(itemHeight / 2, childFragmentManager, viewLifecycleOwner)
            val position = generateCellPosition()
            withContext(Dispatchers.Main) {
               container.addView(selfieCard)
               putViewInRandomPosition(position, selfieCard.id)
            }
         }
      }
   }

   fun addVideoViews() {
      for (i in 1..ITEMS) {
         addVideoView()
      }
   }

   fun addVideoView(onNoSpace: (() -> Unit?)? = null) {
      uiScope.launch {
         withContext(Dispatchers.Default) {
            val position = generateCellPosition()
            position.takeIf {
               it.first == OUT_OF_SPACE_POSITION && it.second == OUT_OF_SPACE_POSITION
            }?.also {
               withContext(Dispatchers.Main) { onNoSpace?.invoke() }
            } ?: addVideoIfSpace(position)
         }
      }
   }

   fun clear() {
      uiScope.cancel()
   }

   fun onPermissionsGranted() {
      selfieView.onPermissionsGranted()
   }

   fun onPermissionsDenied() {
      selfieView.onPermissionsDenied()
   }

   @SuppressLint("ClickableViewAccessibility")
   private fun setup() {
      container.setOnTouchListener { _, event ->
         moveSelfieView(event.x, event.y)
         true
      }
   }

   private suspend fun addVideoIfSpace(position: Pair<Int, Int>) {
      val videoCard = layoutInflater.inflate(R.layout.item_video_view, container, false) as MaterialCardView
      val videoView = videoCard.children.first() as VideoView
      videoView.setup(viewLifecycleOwner)
      videoCard.layoutParams = FrameLayout.LayoutParams(itemHeight, itemHeight)
      videoCard.radius = (itemHeight / 2).toFloat()
      videoCard.id = View.generateViewId()
      withContext(Dispatchers.Main) {
         container.addView(videoCard)
         putViewInRandomPosition(position, videoCard.id)
      }
   }

   private fun putViewInRandomPosition(cellPosition: Pair<Int, Int>, childId: Int) {
      val constraintSet = ConstraintSet()
      constraintSet.clone(container)

      val lastRow = cellPosition.first == ITEMS_IN_HEIGHT - 1
      val topMargin = cellPosition.first * itemMaxHeight + randomTopMargin(lastRow)
      val startMargin = cellPosition.second * itemMaxWidth + randomStartMargin()
      constraintSet.connect(childId, ConstraintSet.TOP, container.id, ConstraintSet.TOP, topMargin)
      constraintSet.connect(childId, ConstraintSet.START, container.id, ConstraintSet.START, startMargin)

      TransitionManager.beginDelayedTransition(container)
      constraintSet.applyTo(container)
   }

   private fun randomTopMargin(lastRow: Boolean): Int {
      val random = Random.nextInt(0, itemHeight/4)
      return if (lastRow) {
         when (random) {
            in 0..LAST_ITEM_BOTTOM_MARGIN -> -random - LAST_ITEM_BOTTOM_MARGIN
            else -> -random
         }
      } else random
   }

   private fun randomStartMargin(): Int =
      Random.nextInt(0, container.resources.getDimension(R.dimen.card_start_margin).toInt())

   private fun generateCellPosition(): Pair<Int, Int> {
      if (reservedCells.size == ITEMS_IN_HEIGHT * ITEMS_IN_WIDTH) {
         return Pair(OUT_OF_SPACE_POSITION, OUT_OF_SPACE_POSITION)
      }

      val row = Random.nextInt(0, ITEMS_IN_HEIGHT)
      val column = Random.nextInt(0, ITEMS_IN_WIDTH)
      val pair = Pair(row, column)
      if (!reservedCells.contains(pair)) {
         reservedCells.add(pair)
         return pair
      }

      return generateCellPosition()
   }

   private fun moveSelfieView(toX: Float, toY: Float) {
      selfieCard.post {
         selfieCard.animate()
            .x(toX)
            .y(toY)
            .setDuration(700)
            .start()
      }
   }

   companion object {
      private const val ITEMS = 9
      private const val ITEMS_IN_HEIGHT = 6
      private const val ITEMS_IN_WIDTH = 5
      private const val OUT_OF_SPACE_POSITION = -1
      private const val LAST_ITEM_BOTTOM_MARGIN = 50
   }
}
