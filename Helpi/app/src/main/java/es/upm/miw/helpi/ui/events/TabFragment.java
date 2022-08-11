package es.upm.miw.helpi.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import es.upm.miw.helpi.R;
import es.upm.miw.helpi.databinding.FragmentTabBinding;
import es.upm.miw.helpi.ui.events.tabAttended.AttendedTabFragment;
import es.upm.miw.helpi.ui.events.tabEvents.EventsTabFragment;
import es.upm.miw.helpi.ui.events.tabRequests.RequestsTabFragment;

public class TabFragment extends Fragment {

    private FragmentTabBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTabBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MyPagerAdapter myPagerAdapter =
                new MyPagerAdapter(
                        requireActivity().getSupportFragmentManager());

        ViewPager viewPager = root.findViewById(R.id.pager);
        viewPager.setAdapter(myPagerAdapter);

        TabLayout tabLayout = root.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return root;
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            switch (i) {
                case 0:
                    fragment = new EventsTabFragment();
                    break;
                case 1:
                    fragment = new RequestsTabFragment();
                    break;
                case 2:
                    fragment = new AttendedTabFragment();
                    break;
                default:
                    fragment = null;
            }
            return Objects.requireNonNull(fragment);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_events);
                case 1:
                    return getString(R.string.tab_requests);
                case 2:
                    return getString(R.string.tab_attended);
            }
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
