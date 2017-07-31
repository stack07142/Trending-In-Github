package io.github.stack07142.trendingingithub.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import io.github.stack07142.trendingingithub.R;
import io.github.stack07142.trendingingithub.model.FilterData;
import io.github.stack07142.trendingingithub.util.DebugLog;
import io.github.stack07142.trendingingithub.model.FilterPreferenceData;

import static io.github.stack07142.trendingingithub.R.string.filter_error_null_lang;
import static io.github.stack07142.trendingingithub.model.FilterPreferenceData.getStringArrayPref;
import static io.github.stack07142.trendingingithub.model.FilterPreferenceData.setStringArrayPref;

public class MyFabFragment extends AAH_FabulousFragment {

    private final String TAG = MyFabFragment.class.getSimpleName();

    ArrayMap<String, ArrayList<String>> applied_filters = new ArrayMap<>();
    ArrayList<String> edited_filters = new ArrayList<>();

    private final String SELECTED = "selected";
    private final String UNSELECTED = "unselected";

    private final int LANG_PAGE = 0;
    private final int PERIOD_PAGE = 1;

    ArrayList<TextView> languageTVs = new ArrayList<>();
    ArrayList<TextView> periodTVs = new ArrayList<>();

    SectionsPagerAdapter mAdapter;
    ViewPager vp_types;
    TabLayout tabs_types;

    public static MyFabFragment newInstance() {

        return new MyFabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DebugLog.logD(TAG, "onCreate()");

        // preference 불러오기
        refreshFilters();

        DebugLog.logD(TAG, "edited_filters = " + edited_filters.toString());
        DebugLog.logD(TAG, "applied_filters = " + applied_filters.get(FilterPreferenceData.LANGUAGE).toString());
    }

    private void refreshFilters() {

        // preference 불러오기
        applied_filters = new ArrayMap<>();

        applied_filters.put(FilterPreferenceData.LANGUAGE, getStringArrayPref(getContext(), FilterPreferenceData.LANGUAGE));
        applied_filters.put(FilterPreferenceData.CREATED, getStringArrayPref(getContext(), FilterPreferenceData.CREATED));

        edited_filters = FilterPreferenceData.getStringArrayPref(getContext(), FilterPreferenceData.EDITED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DebugLog.logD(TAG, "onDestory()");
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {

        View contentView = View.inflate(getContext(), R.layout.filter_view, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        vp_types = (ViewPager) contentView.findViewById(R.id.vp_types);
        tabs_types = (TabLayout) contentView.findViewById(R.id.tabs_types);

        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        ImageButton imgbtn_edit = (ImageButton) contentView.findViewById(R.id.imgbtn_edit);
        ImageButton imgbtn_apply = (ImageButton) contentView.findViewById(R.id.imgbtn_apply);

        // Apply 버튼 Click Listener
        imgbtn_apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Apply 조건 체크
                if (checkApplyCondition()) {

                    closeFilter(applied_filters);

                    // preference 저장
                    setStringArrayPref(getContext(), FilterPreferenceData.LANGUAGE, applied_filters.get(FilterPreferenceData.LANGUAGE));
                    setStringArrayPref(getContext(), FilterPreferenceData.CREATED, applied_filters.get(FilterPreferenceData.CREATED));
                }
            }
        });

        // Edit 버튼 Click Listener - Language Customize
        imgbtn_edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Language Data
                FilterData filterData = new FilterData();

                String[] items = new String[filterData.getLanguageListSize() - 1];
                List<String> editableLanguageList = filterData.getLanguageList();
                editableLanguageList.remove(0); // Remove "All", All은 Filter에서 제거되지 않도록 한다
                items = editableLanguageList.toArray(items);

                final String[] finalItems = items;

                // Already Checked Items
                final boolean[] checkedItems = new boolean[filterData.getLanguageListSize() - 1];

                for (String s : edited_filters) {

                    if (s.equals("All")) continue;

                    checkedItems[filterData.getLanguageIndex(s)] = true;
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(getString(R.string.dialog_title))
                        .setMultiChoiceItems(items, checkedItems,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                        if (isChecked) {

                                            edited_filters.add(finalItems[which]);
                                        } else {

                                            // edited_filter 제거
                                            edited_filters.remove(finalItems[which]);

                                            // applied_filter에도 해당되면 제거
                                            removeFromSelectedMap(FilterPreferenceData.LANGUAGE, finalItems[which]);
                                        }
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                refreshFilters();
                            }
                        })
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Preference 저장
                                FilterPreferenceData.setStringArrayPref(getContext(), FilterPreferenceData.EDITED, edited_filters);
                                FilterPreferenceData.setStringArrayPref(getContext(), FilterPreferenceData.LANGUAGE, applied_filters.get(FilterPreferenceData.LANGUAGE));

                                // applied_filter가 0이면 -> "All" Setting
                                if (applied_filters.get(FilterPreferenceData.LANGUAGE) == null || applied_filters.get(FilterPreferenceData.LANGUAGE).size() == 0) {

                                    addToSelectedMap(FilterPreferenceData.LANGUAGE, "All");
                                }

                                FilterPreferenceData.editedFlag = true;

                                closeFilter(applied_filters);
                            }
                        }).create().show();

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
                    inflateLayoutWithFilters(FilterPreferenceData.LANGUAGE, fbl);
                    break;

                case PERIOD_PAGE:
                    inflateLayoutWithFilters(FilterPreferenceData.CREATED, fbl);
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
                    return FilterPreferenceData.LANGUAGE;

                case 1:
                    return FilterPreferenceData.CREATED;

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

            case FilterPreferenceData.LANGUAGE:

                // keys = ((RepositoryListActivity) getActivity()).filterData.getLanguageList();
                keys = edited_filters;
                break;

            case FilterPreferenceData.CREATED:

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

                    // 선택하여 비활성화 하는 경우
                    if (tv.getTag() != null && tv.getTag().equals(SELECTED)) {

                        tv.setTag(UNSELECTED);
                        tv.setBackgroundResource(R.drawable.chip_unselected);
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));

                        removeFromSelectedMap(filter_category, finalKeys.get(finalI));
                    }
                    // 선택하여 활성화 하는 경우
                    else {

                        // Created의 임의의 항목을 선택하는 경우
                        if (filter_category.equals(FilterPreferenceData.CREATED)) {

                            clearPeriodSelected();
                        }

                        // Language의 임의의 항목을 선택하는 경우
                        if (filter_category.equals(FilterPreferenceData.LANGUAGE)) {

                            if (finalKeys.get(finalI).equals("All")) {

                                clearLanguageSelected();
                            } else {

                                languageTVs.get(0).setTag(UNSELECTED);
                                languageTVs.get(0).setBackgroundResource(R.drawable.chip_unselected);
                                languageTVs.get(0).setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));

                                removeFromSelectedMap(filter_category, "All");
                            }
                        }

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

            if (filter_category.equals(FilterPreferenceData.LANGUAGE)) {

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

        if (applied_filters.get(FilterPreferenceData.CREATED) != null) {

            applied_filters.get(FilterPreferenceData.CREATED).clear();
        }
    }

    private void clearLanguageSelected() {

        for (TextView tv : languageTVs) {

            tv.setTag(UNSELECTED);
            tv.setBackgroundResource(R.drawable.chip_unselected);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
        }

        if (applied_filters.get(FilterPreferenceData.LANGUAGE) != null) {

            applied_filters.get(FilterPreferenceData.LANGUAGE).clear();
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

        if (applied_filters.get(key) != null) {

            if (applied_filters.get(key).size() == 1 && applied_filters.get(key).contains(value)) {

                applied_filters.remove(key);
            } else {

                applied_filters.get(key).remove(value);
            }
        }
    }

    private boolean checkApplyCondition() {

        boolean ret = true;

        if (applied_filters.get(FilterPreferenceData.LANGUAGE) == null) {

            Toast.makeText(getContext(), getString(filter_error_null_lang), Toast.LENGTH_SHORT).show();
            ret = false;
        } else {

            if (applied_filters.get(FilterPreferenceData.LANGUAGE).size() > 3) {

                Toast.makeText(getContext(), getString(R.string.filter_error_exceed), Toast.LENGTH_SHORT).show();
                ret = false;
            }
        }

        if (applied_filters.get(FilterPreferenceData.CREATED) == null) {

            Toast.makeText(getContext(), getString(R.string.filter_error_null_created), Toast.LENGTH_SHORT).show();
            ret = false;
        }

        return ret;
    }
}