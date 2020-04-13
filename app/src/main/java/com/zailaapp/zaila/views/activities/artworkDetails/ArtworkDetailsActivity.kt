package com.zailaapp.zaila.views.activities.artworkDetails

import android.animation.ValueAnimator
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.extensions.*
import com.zailaapp.zaila.extensions.projectOnly.fillArtwork
import com.zailaapp.zaila.model.responses.Artwork
import com.zailaapp.zaila.model.responses.ArtworkDetails
import com.zailaapp.zaila.services.TextToSpeechService
import com.zailaapp.zaila.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_artwork_details.*
import kotlinx.android.synthetic.main.layout_progress.*
import kotlin.random.Random

class ArtworkDetailsActivity : BaseActivity(R.layout.activity_artwork_details) {

    companion object {

        fun newIntent(sensorId: String): Intent {
            val intent = Intent(ZailaApplication.instance, ArtworkDetailsActivity::class.java)

            intent.putExtra("sensorId", sensorId)

            return intent
        }
    }

    private val _sensorId: String by lazy { intent.getStringExtra("sensorId") }

    private lateinit var _viewModel: ArtworkViewModel

    private var isPlaying = false
    private var currentPhraseIndex = -1

    private var _progressAnimator: ValueAnimator? = null

    private var _restoredSpinnerPosition = -1

    override fun onResume() {
        super.onResume()

        volumeControlStream = AudioManager.STREAM_MUSIC

        enterFullScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewModel = ViewModelProvider(this).get()

        //Initializes the service to display the required notification straight away.
        TextToSpeechService.init()
        /*  */

        //region click listeners

        ic_close?.setOnClickListener { onBackPressed() }

        btn_load_artwork?.setOnClickListener {

            /**
             * Artwork ID can come from safe args(HomeFragment) or from deep link:
             * @see ArtworkDetailsFragment.navigateDeepLink
             */
            _viewModel.getArtwork(_sensorId)
        }

        btn_play?.setOnClickListener {
            clickPlay()
        }

        btn_zaila?.setOnClickListener {
            clickPlay()
        }

        btn_zaila?.setOnLongClickListener {
            resetSpeech()
            return@setOnLongClickListener true
        }

        //endregion click listeners

        //...

        setObservers()

        //region restoring state and load

        //Retrieving spinner position from state... Will load it after spinner is recreated.
        val spinnerPosition = savedInstanceState?.getInt("spinner")
        if (spinnerPosition != null) {
            _restoredSpinnerPosition = spinnerPosition
        } else {
            _viewModel.getArtwork(_sensorId)
        }

        //endregion restoring state and load

    }

    private fun clickPlay() {
        isPlaying = !isPlaying

        //Does bounce animation if playing and starts/pauses the service:
        if (isPlaying) {

            //Shrinks, changes the image icon, then does the bounce effect.
            btn_zaila?.animateClickBounce {

                btn_play?.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)

                //PAUSE ICON: We need this validation as the pause button might have been pressed again:
                btn_zaila?.setImageResource(if (isPlaying) R.drawable.img_zaila_headphones else R.drawable.ic_img_zaila)
            }


            //Play TTS:
            startSpeechService()
        } else {

            //Pause TTS:
            pauseSpeech()

            /* currentPhraseIndex is saved at the highlight extension function */
        }
    }

    //region FULL_SCREEN_IMMERSIVE

    private fun enterFullScreen() {

        window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window?.decorView?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun exitFullScreen() {

        window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    //endregion FULL_SCREEN_IMMERSIVE


    //region observers
    /**
     * Sets the observers to update the UI as soon as it changes on the ViewModel:
     */
    private fun setObservers() {


        _viewModel.isLoading.observe(this, Observer<Boolean> {
            progress?.visibility = if (it) View.VISIBLE else View.GONE
        })

        _viewModel.errorMessage.observe(this, Observer<String> {
            toast(it)
        })

        _viewModel.artwork.observe(this, Observer<Artwork> { artwork ->
            layout_artwork_details?.fillArtwork(artwork)

            //Player labels:
            tv_title_artwork_.text = artwork.title
            tv_artist_artwork_.text = artwork.artistName
        })

        _viewModel.artworkDetails.observe(this, Observer<List<ArtworkDetails>> { listDetails ->

            if (listDetails != null) {
                val languages = listDetails.map { it.languageCode }
                loadDescription(languages)

                btn_play?.isEnabled = languages.isNotEmpty()
            }

        })

    }

//endregion observers

//region loadDescription Spinner

    private fun loadDescription(languageCodes: List<String?>) {

        val fullDescription = _viewModel.artworkDetails.value?.get(0)?.description
        tv_description_artwork?.text = fullDescription


        val array = fullDescription!!.splitDots()

        resetSpeech()
    }

//endregion loadDescription Spinner

//...

//region Text-to-speech

    private var hasZoomedOnce = false

    private fun startSpeechService() {

        val localeString =
            _viewModel.artworkDetails.value?.get(0)
                ?.languageCode

        //region text to speech callbacks - animate
        //Setup listener to run every time a new phrase is spoken:
        TextToSpeechService.listener = {
            runOnUiThread {

                tv_description_artwork?.highlight(it)   //This updates the currentPhraseIndex
                tv_description_artwork?.displayProgressVisuals(it)

                //todo - Hardcoded - If its first phrase:
                if (currentPhraseIndex == 0) {

                    img_artwork?.animateZoomIn(getPivotForDetailX(it), getPivotForDetailY(it))

                } else {

                    if (!hasZoomedOnce) {
                        hasZoomedOnce = true
                        img_artwork?.animateZoomOut()
                    }

                } //end else

            }


        }

        TextToSpeechService.listenerFinish = {
            runOnUiThread {
                resetSpeech()
            }
        }

        //endregion text to speech callbacks - animate

        //Start the service to speak: (We pass the phrases as an ARRAY)
        TextToSpeechService.startSpeak(
            ArrayList(tv_description_artwork?.text.toString().splitDots()),
            "$localeString",
            currentPhraseIndex
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        TextToSpeechService.killService()

        exitFullScreen()
    }

    /**
     * Pause - Zooms out and stops just the TTS (does not kill the service).
     */
    private fun pauseSpeech() {

        isPlaying = false

        btn_play?.setImageResource(R.drawable.ic_play)
        btn_zaila?.setImageResource(R.drawable.ic_img_zaila)

        img_artwork?.animateZoomOut()
        TextToSpeechService.stopTTS()

        pauseProgress()
    }

    /**
     * Reset - clears the visuals AND the current index being spoken.
     */
    private fun resetSpeech() {

        pauseSpeech()

        tv_description_artwork?.text = _viewModel.artworkDetails.value?.get(0)?.description
        currentPhraseIndex = -1

        if (progress_bar != null) {
            if (progress_bar.progress < progress_bar.max) {

                animateProgressTillMax(200L)
            }
        }
    }


//endregion Text-to-speech

//region extensions - text highlight, zoom and progress

//region speech progress

    /**
     * Given a phrase, it will make the progress bar animate from its current position, until the end.
     * The value considered for the animation is from the PHRASE position, until the END of the TextView.
     *
     * 0% -> 100% (If there is only one phrase in the TextView)
     *
     * 25% -> 100% (If there are 4 phrases, and the current phrase is the second one)
     *
     * 90% -> 100% (If there are 10 phrases, and the current phrase is the last one)
     */
    private fun TextView.displayProgressVisuals(phrase: String) {

        //Gets the array of phrases:
        val fullText = text.toString()
        val arrayPhrases = fullText.splitDots()

        //Then get the current index of the phrase being highlighted:
        val indexOfStringToHighlight = arrayPhrases.indexOf(phrase)

        //region Remaining words to be spoken
        val remainingArrayPhrases =
            arrayPhrases.subList(indexOfStringToHighlight, arrayPhrases.size)

        var remainingWords = 0

        remainingArrayPhrases.forEach {

            remainingWords += it.split(" ").size
        }
        //endregion Remaining words to be spoken

        //Value from 0 to 100, representing each phrase in the whole text:
        val progressStepValue = ((1 / arrayPhrases.size.toFloat()) * 100).toInt()

        //0%, 10%, 25% and so on:
        val initialProgressPercent =
            if (indexOfStringToHighlight == 0) {
                0
            } else {
                ((indexOfStringToHighlight / arrayPhrases.size.toFloat()) * 100).toInt()
            }

        //Will finish current progress step at 25%, 30%, 100%...?
        val endCurrentProgressPercent = initialProgressPercent + progressStepValue

        val durationForProgressAnimation =
            if (endCurrentProgressPercent + progressStepValue > 100) remainingWords * 320L else (remainingWords * 400L)

        /////////// GUARD ////////////////////////
        if (progress_player == null) return
        //////////RETURN HERE/////////////////////

        if (initialProgressPercent == 0) {
            progress_player.progress = 0
        }

        animateProgressTillMax(durationForProgressAnimation)
    }

    /**
     * Animate progress bar from its current progress, until 100%.
     * Cancels any previous animation (Progress bar will remain with last updated value).
     *
     * Updates the _progressAnimator reference to the latest one being used.
     */
    private fun animateProgressTillMax(duration: Long) {

        _progressAnimator?.cancel()

        _progressAnimator =
            progress_player?.progress?.animateTo((progress_player.max), duration) {
                progress_player?.progress = it
            }
    }

    private fun pauseProgress() {
        _progressAnimator?.cancel()
    }
//endregion speech progress

    /**
     * This extension is updating the currentPhraseIndex.
     *
     * Note that the INDEXOF accounts the DOT plus the space at the end!
     */
    private fun TextView.highlight(s: String) {

        val fullText = text.toString()

        val array = fullText.splitDots()

        val indexOfStringToHighlight = array.indexOf(s)

        //Saves the current index for pause/resume:
        currentPhraseIndex = indexOfStringToHighlight
        /////////////////////////////////////////////

        setCustomFontAndColor(
            CustomSpanSetup().apply {
                colorMap = hashMapOf(
                    indexOfStringToHighlight to R.color.blue
                )
                boldPositions = arrayListOf(indexOfStringToHighlight)  //Bold positions
            },
            {
                //Text click it -> position
                //  if (it == 1) {} else if (it == 3) {}
            },
            *array.toTypedArray()
        )
    }

    private fun String.splitDots(): List<String> {

        val delimiter = if (contains("。")) "。" else ". "

        val a = split(delimiter)
        val array = a.mapIndexed { index, s ->

            var newValue = s
            if (index < a.size - 1) {
                newValue += delimiter
            }

            newValue
        }

        return array
    }

    private fun ImageView.animateZoomIn(
        pivotForX: Float,
        pivotForY: Float,
        endListener: () -> Unit = {}
    ) {

        var zoomValue = 0f

        pivotX = measuredWidth * pivotForX
        pivotY = measuredHeight * pivotForY

        val valueAnimator = android.animation.ValueAnimator.ofFloat(zoomValue, 1.5f)

        valueAnimator.addUpdateListener {
            zoomValue = it.animatedValue as Float

            scaleX = 1 + zoomValue
            scaleY = 1 + zoomValue
        }

        valueAnimator.doOnEnd { endListener() }

        //5 Set up the animator’s duration and interpolator:
        valueAnimator.interpolator = android.view.animation.DecelerateInterpolator()
        valueAnimator.duration = 1000L

        //6 Go!
        valueAnimator.start()
    }

    private fun ImageView.animateZoomOut(endListener: () -> Unit = {}) {

        //Skip if View is NOT ZOOMED...
        if (scaleX == 1f) {
            return
        }

        var zoomValue = scaleX

        val valueAnimator = android.animation.ValueAnimator.ofFloat(zoomValue, 1f)

        valueAnimator.addUpdateListener {
            zoomValue = it.animatedValue as Float

            scaleX = zoomValue
            scaleY = zoomValue

        }

        valueAnimator.doOnEnd { endListener() }

        //5 Set up the animator’s duration and interpolator:
        valueAnimator.interpolator = android.view.animation.DecelerateInterpolator()
        valueAnimator.duration = 1000L

        //6 Go!
        valueAnimator.start()
    }

//endregion extensions - text highlight, zoom and progress

    //region mock test get pivot
    private fun getPivotForDetail(stringPiece: String): Float {
        //todo - API implementaion...
        val pivot = Random.nextInt(1, 10)

        return if (pivot == 10) 1F else "0.$pivot".toFloat()
    }

    private fun getPivotForDetailX(stringPiece: String): Float {
        return 0.61F
    }

    private fun getPivotForDetailY(stringPiece: String): Float {
        return 0.0F
    }
//endregion mock test get pivot
}