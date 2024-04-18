package com.example.htechqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.htechqr.ConfiguracionFragment;
import com.example.htechqr.QrFragment;
import com.example.htechqr.R;
import com.example.htechqr.SensorFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class VistaFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vista, container, false);
        viewPager = rootView.findViewById(R.id.viewPager);
        tabLayout = rootView.findViewById(R.id.tabGrupo);

        ViewPager2Adapter adapter = new ViewPager2Adapter(this);
        adapter.addFragment(new QrFragment(), "Grupo");
        adapter.addFragment(new SensorFragment(), "Sensor");
        adapter.addFragment(new ConfiguracionFragment(), "Configuracion");
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(adapter.getTitle(position))
        ).attach();

        return rootView;
    }

    private static class ViewPager2Adapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> titles = new ArrayList<>();

        public ViewPager2Adapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        public String getTitle(int position) {
            return titles.get(position);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}
