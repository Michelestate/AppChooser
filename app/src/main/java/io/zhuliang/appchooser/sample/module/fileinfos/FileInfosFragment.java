package io.zhuliang.appchooser.sample.module.fileinfos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.julian.appchooser.sample.BuildConfig;
import io.julian.appchooser.sample.R;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.sample.SampleInjection;
import io.zhuliang.appchooser.sample.data.FileInfo;

public class FileInfosFragment extends Fragment implements FileInfosContract.View {

    private static final String EXTRA_ABSOLUTE_PATH = BuildConfig.APPLICATION_ID + ".extra.ABSOLUTE_PATH";
    private FileInfosContract.Presenter mPresenter;
    private FileInfosAdapter mAdapter;

    public static FileInfosFragment newInstance(@NonNull String absolutePath) {
        Preconditions.checkNotNull(absolutePath);
        Bundle args = new Bundle();
        args.putString(EXTRA_ABSOLUTE_PATH, absolutePath);

        FileInfosFragment fragment = new FileInfosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String absolutePath = getArguments().getString(EXTRA_ABSOLUTE_PATH);

        if (TextUtils.isEmpty(absolutePath)) {
            throw new IllegalStateException("Absolute path is null");
        }
        // Create the presenter
        new FileInfosPresenter(new FileInfo(new File(absolutePath)),
                SampleInjection.provideSchedulerProvider(),
                this,
                SampleInjection.provideFileInfoRepository());
        mAdapter = new FileInfosAdapter(getContext(), new ArrayList<FileInfo>());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_file_infos, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view_file_infos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.hasFixedSize();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(FileInfosContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showFileInfos(List<FileInfo> fileInfos) {
        if (getView() == null) {
            return;
        }
        mAdapter.replaceDatas(fileInfos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoFileInfos() {
        mAdapter.replaceDatas(Collections.EMPTY_LIST);
        mAdapter.notifyDataSetChanged();
    }
}
