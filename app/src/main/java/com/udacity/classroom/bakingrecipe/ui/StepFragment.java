package com.udacity.classroom.bakingrecipe.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.classroom.bakingrecipe.BR;
import com.udacity.classroom.bakingrecipe.R;
import com.udacity.classroom.bakingrecipe.database.AppDatabase;
import com.udacity.classroom.bakingrecipe.database.BakingRecipeEntry;
import com.udacity.classroom.bakingrecipe.database.Step;
import com.udacity.classroom.bakingrecipe.databinding.FragmentStepBinding;
import com.udacity.classroom.bakingrecipe.model.DetailViewModel;
import com.udacity.classroom.bakingrecipe.model.DetailViewModelFactory;

import java.util.List;

public class StepFragment extends Fragment implements View.OnClickListener{

    private static final String BUNDLE_RECIPE_ID = "bundleRecipeId";
    private static final String BUNDLE_POSITION = "bundlePosition";
    private static final String BUNDLE_SEEK_POSITION = "bundleSeekPosition";
    private static final String BUNDLE_PLAY_WHEN_READY = "bundlePlayWhenReady";

    private AppDatabase mDb;
    private int mRecipeId;
    private int mPosition;
    private int mStepSize;
    private boolean mPlayWhenReady;
    private boolean mTwoPane;
    private boolean mTablet;
    private long mSeekPosition;
    private String mVideoUrl;
    private SimpleExoPlayer mExoPlayer;
    private FragmentStepBinding mStepBinding;
    private OnButtonClickListener mCallback;

    public StepFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTablet = getResources().getBoolean(R.bool.isTablet);
        mDb = AppDatabase.getInstance(getContext());
        mVideoUrl = "";
        mPlayWhenReady = true;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(BUNDLE_RECIPE_ID)) {
                mRecipeId = savedInstanceState.getInt(BUNDLE_RECIPE_ID);
            }

            if (savedInstanceState.containsKey(BUNDLE_POSITION)) {
                mPosition = savedInstanceState.getInt(BUNDLE_POSITION);
            }

            if (savedInstanceState.containsKey(BUNDLE_SEEK_POSITION)) {
                mSeekPosition = savedInstanceState.getLong(BUNDLE_SEEK_POSITION);
            }

            if (savedInstanceState.containsKey(BUNDLE_PLAY_WHEN_READY)) {
                mPlayWhenReady = savedInstanceState.getBoolean(BUNDLE_PLAY_WHEN_READY);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        mStepBinding = DataBindingUtil.bind(rootView);

        if (mStepBinding != null) {
            mStepBinding.backButton.setOnClickListener(this);
            mStepBinding.forwardButton.setOnClickListener(this);
        }

        if (mTwoPane || !mTablet) setupViewModel();

        return mStepBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_RECIPE_ID, mRecipeId);
        outState.putInt(BUNDLE_POSITION, mPosition);
        outState.putLong(BUNDLE_SEEK_POSITION, mSeekPosition);
        outState.putBoolean(BUNDLE_PLAY_WHEN_READY, mPlayWhenReady);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (!mVideoUrl.isEmpty()) {
                initializePlayer(Uri.parse(mVideoUrl));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23) {
            if (!mVideoUrl.isEmpty()) {
                initializePlayer(Uri.parse(mVideoUrl));
            }
        }

        if (!mTwoPane && !mTablet) onConfigurationChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mSeekPosition = mExoPlayer.getCurrentPosition();
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();
        }

        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void setupViewModel() {
        DetailViewModelFactory factory = new DetailViewModelFactory(mDb, mRecipeId);
        final DetailViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
        viewModel.getRecipe().observe(this, new Observer<BakingRecipeEntry>() {
            @Override
            public void onChanged(@Nullable BakingRecipeEntry recipeEntry) {
                List<Step> steps = recipeEntry.getSteps();
                mStepSize = steps.size();
                mStepBinding.textDescription.setText(steps.get(mPosition).getDescription());

                mVideoUrl = steps.get(mPosition).getVideoURL();
                if (!mVideoUrl.isEmpty()) {
                    initializePlayer(Uri.parse(mVideoUrl));
                }

                String thumbnailUrl = steps.get(mPosition).getThumbnailURL();
                if (!thumbnailUrl.isEmpty()) {
                    mStepBinding.imageThumbnail.setVisibility(View.VISIBLE);
                    mStepBinding.setVariable(BR.thumbnail, thumbnailUrl);
                    mStepBinding.executePendingBindings();
                }
            }
        });
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            mStepBinding.playerView.setPlayer(mExoPlayer);

            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            DataSource.Factory factory = new DefaultDataSourceFactory(getContext(), userAgent);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(factory).createMediaSource(mediaUri);
            mExoPlayer.prepare(mediaSource);
            if (mSeekPosition != 0) mExoPlayer.seekTo(mSeekPosition);
            mExoPlayer.setPlayWhenReady(mPlayWhenReady);
            mStepBinding.playerView.setVisibility(View.VISIBLE);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
        mExoPlayer = null;
    }

    public void onConfigurationChanged() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mExoPlayer != null) {
                hideSystemUi();
                ViewGroup.LayoutParams params = mStepBinding.playerView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                mStepBinding.playerView.setLayoutParams(params);
            }
        } else {
            if (mPosition == 0 && mStepSize > 1) {
                mStepBinding.forwardButton.setVisibility(View.VISIBLE);
            } else if (mPosition == mStepSize - 1) {
                mStepBinding.backButton.setVisibility(View.VISIBLE);
            } else {
                mStepBinding.backButton.setVisibility(View.VISIBLE);
                mStepBinding.forwardButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideSystemUi() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    public void setRecipeId(int recipeId) {
        mRecipeId = recipeId;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public void isTwoPane(boolean twoPane) {
        mTwoPane = twoPane;
    }

    public void onBackButtonClick() {
        mCallback.onButtonClicked(mPosition - 1);
    }

    public void onForwardButtonClick() {
        mCallback.onButtonClicked(mPosition + 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                onBackButtonClick();
                break;
            case R.id.forward_button:
                onForwardButtonClick();
                break;
            default:
                break;
        }
    }

    public interface OnButtonClickListener {
        void onButtonClicked(int position);
    }
}
