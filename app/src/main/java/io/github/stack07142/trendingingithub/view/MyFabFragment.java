package io.github.stack07142.trendingingithub.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.stack07142.trendingingithub.R;
import io.github.stack07142.trendingingithub.util.DebugLog;
import io.github.stack07142.trendingingithub.util.FilterPreference;

import static io.github.stack07142.trendingingithub.util.FilterPreference.getStringArrayPref;
import static io.github.stack07142.trendingingithub.util.FilterPreference.setStringArrayPref;

public class MyFabFragment extends AAH_FabulousFragment {

    private final String TAG = MyFabFragment.class.getSimpleName();

    private final String SELECTED = "selected";
    private final String UNSELECTED = "unselected";

    private final int LANG_PAGE = 0;
    private final int PERIOD_PAGE = 1;

    ArrayMap<String, ArrayList<String>> applied_filters = new ArrayMap<>();
    ArrayList<TextView> languageTVs = new ArrayList<>();
    ArrayList<TextView> periodTVs = new ArrayList<>();

    SectionsPagerAdapter mAdapter;
    ViewPager vp_types;
    TabLayout tabs_types;

    ImageButton imgbtn_refresh;

    public static MyFabFragment newInstance() {

        return new MyFabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applied_filters.put(FilterPreference.LANGUAGE, getStringArrayPref(getContext(), FilterPreference.LANGUAGE));
        applied_filters.put(FilterPreference.CREATED, getStringArrayPref(getContext(), FilterPreference.CREATED));

        for (Map.Entry<String, ArrayList<String>> entry : applied_filters.entrySet()) {

            DebugLog.logD(TAG, "from activity: " + entry.getKey());

            for (String s : entry.getValue()) {

                DebugLog.logD(TAG, "from activity val: " + s);
            }
        }

        // 최초 쿼리
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {

        DebugLog.logD(TAG, "setupDialog()");

        View contentView = View.inflate(getContext(), R.layout.filter_view, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        vp_types = (ViewPager) contentView.findViewById(R.id.vp_types);
        tabs_types = (TabLayout) contentView.findViewById(R.id.tabs_types);

        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        imgbtn_refresh = (ImageButton) contentView.findViewById(R.id.imgbtn_refresh);
        ImageButton imgbtn_apply = (ImageButton) contentView.findViewById(R.id.imgbtn_apply);

        // Apply 버튼 Click Listener
        imgbtn_apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                closeFilter(applied_filters);

                DebugLog.logD(TAG, "apply button clicked : ");

                // preference 저장
                setStringArrayPref(getContext(), FilterPreference.LANGUAGE, applied_filters.get(FilterPreference.LANGUAGE));
                setStringArrayPref(getContext(), FilterPreference.CREATED, applied_filters.get(FilterPreference.CREATED));
            }
        });

        // Refresh 버튼 Click Listener
        imgbtn_refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DebugLog.logD(TAG, "refresh button clicked : ");

                for (TextView tv : languageTVs) {

                    tv.setTag(UNSELECTED);
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                }

                if (applied_filters.get(FilterPreference.LANGUAGE) != null) {

                    applied_filters.get(FilterPreference.LANGUAGE).clear();
                }
            }
        });

        mAdapter = new SectionsPagerAdapter();

        vp_types.setOffscreenPageLimit(2);
        vp_types.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        tabs_types.setupWithViewPager(vp_types);

        // params to set
        setAnimationDuration(400); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
        setCallbacks((Callbacks) getActivity()); //optional; to get back result
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

    private class SectionsPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.view_filters_sorters, collection, false);
            FlexboxLayout fbl = (FlexboxLayout) layout.findViewById(R.id.fbl);

            switch (position) {

                case LANG_PAGE:
                    inflateLayoutWithFilters(FilterPreference.LANGUAGE, fbl);
                    break;

                case PERIOD_PAGE:
                    inflateLayoutWithFilters(FilterPreference.CREATED, fbl);
                    break;
            }

            collection.addView(layout);

            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {

                case 0:
                    return FilterPreference.LANGUAGE;

                case 1:
                    return FilterPreference.CREATED;

            }

            return "";
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private void inflateLayoutWithFilters(final String filter_category, FlexboxLayout fbl) {

        List<String> keys = new ArrayList<>();

        switch (filter_category) {

            case FilterPreference.LANGUAGE:

                keys = ((RepositoryListActivity) getActivity()).filterData.getLanguageList();
                break;

            case FilterPreference.CREATED:

                keys = ((RepositoryListActivity) getActivity()).filterData.getCreatedList();
                break;

        }

        for (int i = 0; i < keys.size(); i++) {

            View subchild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);

            final TextView tv = ((TextView) subchild.findViewById(R.id.txt_title));

            tv.setText(keys.get(i));

            final int finalI = i;
            final List<String> finalKeys = keys;

            tv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (tv.getTag() != null && tv.getTag().equals(SELECTED)) {

                        DebugLog.logD(TAG, "unselected " + tv.getText());

                        tv.setTag(UNSELECTED);
                        tv.setBackgroundResource(R.drawable.chip_unselected);
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));

                        removeFromSelectedMap(filter_category, finalKeys.get(finalI));
                    } else {

                        if (filter_category.equals(FilterPreference.CREATED)) {

                            clearPeriodSelected();
                        }

                        DebugLog.logD(TAG, "selected " + tv.getText());

                        tv.setTag(SELECTED);
                        tv.setBackgroundResource(R.drawable.chip_selected);
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));

                        addToSelectedMap(filter_category, finalKeys.get(finalI));
                    }
                }
            });

            if (applied_filters != null && applied_filters.get(filter_category) != null
                    && applied_filters.get(filter_category).contains(keys.get(finalI))) {

                tv.setTag(SELECTED);
                tv.setBackgroundResource(R.drawable.chip_selected);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
            } else {

                tv.setBackgroundResource(R.drawable.chip_unselected);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
            }

            if (filter_category.equals(FilterPreference.LANGUAGE)) {

                languageTVs.add(tv);
            } else {

                periodTVs.add(tv);
            }

            fbl.addView(subchild);
        } // ~for loop
    }

    private void clearPeriodSelected() {

        for (TextView tv : periodTVs) {

            tv.setTag(UNSELECTED);
            tv.setBackgroundResource(R.drawable.chip_unselected);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
        }

        if (applied_filters.get(FilterPreference.CREATED) != null) {

            applied_filters.get(FilterPreference.CREATED).clear();
        }
    }

    private void addToSelectedMap(String key, String value) {

        if (applied_filters.get(key) != null && !applied_filters.get(key).contains(value)) {

            applied_filters.get(key).add(value);
        } else {

            ArrayList<String> temp = new ArrayList<>();
            temp.add(value);
            applied_filters.put(key, temp);
        }
    }

    private void removeFromSelectedMap(String key, String value) {

        if (applied_filters.get(key).size() == 1) {

            applied_filters.remove(key);
        } else {

            applied_filters.get(key).remove(value);
        }
    }
}