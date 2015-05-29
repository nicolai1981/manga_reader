package itocorp.mangaextractor.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import itocorp.mangaextractor.R;
import itocorp.mangaextractor.control.Configurator;
import itocorp.mangaextractor.control.MangaReader;
import itocorp.mangaextractor.control.event.BaseEvent;
import itocorp.mangaextractor.control.event.LoadMangaDetailEvent;
import itocorp.mangaextractor.control.event.LoadMangaListEvent;
import itocorp.mangaextractor.model.Manga;

public class LoadFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static LoadFragment newInstance(int sectionNumber) {
        LoadFragment fragment = new LoadFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private EventBus mEventBus = EventBus.getDefault();

    @InjectView(R.id.text_progress)
    public TextView mProgressText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    public void onPause() {
        mEventBus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.button_init)
    public void onConfigClick() {
        new Configurator().setup();
    }

    @OnClick(R.id.button_start)
    public void onLoadClick() {
        new MangaReader().getMangaList(this);
    }

    @OnClick(R.id.button_print)
    public void onPrintClick() {
        mMangaList = Manga.getList();
        mIndex = 0;
        loadDescription();
    }

    private List<Manga> mMangaList = new ArrayList<Manga>();
    private int mIndex = 0;

    public void onEventMainThread(LoadMangaListEvent event) {
        mProgressText.setText(event.mMessage);
        if (event.mResult == BaseEvent.RESULT.SUCCESS) {
            mMangaList = Manga.getList();
            mIndex = 0;
            loadDescription();
        }
    }

    private void loadDescription() {
        if (mMangaList.size() > mIndex) {
            mProgressText.setText("Loading details from: " + mMangaList.get(mIndex).mTitle);
            new MangaReader().getMangaDetails(mMangaList.get(mIndex), this);
        }
    }

    public void onEventMainThread(LoadMangaDetailEvent event) {
        mProgressText.setText(event.mMessage);
        if (event.mResult == BaseEvent.RESULT.SUCCESS) {
            mIndex++;
            loadDescription();
        }
    }
}
