package com.kasc.hall.home;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.kasc.hall.MainActivity;
import com.kasc.hall.R;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private List<BranchModel> branchList;
    private List<EventModel> eventsList;
    private int currentEventIndex = 0;
    private EventsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        branchList = new ArrayList<>();
        branchList.add(new BranchModel(R.drawable.cse_icon, "Bsc Computer Technology (Bsc CT)"));
        branchList.add(new BranchModel(R.drawable.cse_icon, "Bsc Information Technology (Bsc IT) "));
        branchList.add(new BranchModel(R.drawable.cse_icon, "Bsc Computer Science  (Bsc CS) "));
        branchList.add(new BranchModel(R.drawable.cse_icon, "Bachelors's in Computer Application.(BCA) "));



        // to be added

        eventsList = new ArrayList<>();


        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return branchList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view.equals(object);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.branch_item, container, false);

                ImageView branchIconImageView = itemView.findViewById(R.id.br_icon);
                TextView branchTitleTextView = itemView.findViewById(R.id.br_title);


                BranchModel branch = branchList.get(position);
                branchIconImageView.setImageResource(branch.getImg());
                branchTitleTextView.setText(branch.getTitle());

                container.addView(itemView, 0);
                return itemView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });

        SliderView sliderView = view.findViewById(R.id.sliderView);
        sliderView.setSliderAdapter(new SliderViewAdapter<SliderViewHolder>() {
            private final int[] images = {R.drawable.main, R.drawable.e2, R.drawable.stu,R.drawable.banner02,R.drawable.banner04,R.drawable.banner06_0,R.drawable.banner_new06,R.drawable.banner_new07};
            private final String[] text = {"Kongunadu Arts And Science, Coimbatore", "KASC Entrance", "Students at KASC", "","","","",""};

            @Override
            public SliderViewHolder onCreateViewHolder(ViewGroup parent) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliter_item_layout, parent, false);
                return new SliderViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(SliderViewHolder viewHolder, int position) {
                viewHolder.imageView.setImageResource(images[position]);
                viewHolder.textView.setText(text[position]);
            }

            @Override
            public int getCount() {
                return images.length;
            }
        });

        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
        sliderView.startAutoCycle();

        ImageView map = view.findViewById(R.id.map);
        map.setOnClickListener(v -> openMap());


        eventsRecyclerView = view.findViewById(R.id.eventsRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        eventsRecyclerView.setLayoutManager(layoutManager);
        adapter = new EventsAdapter(eventsList);
        eventsRecyclerView.setAdapter(adapter);

        new FetchEventsTask().execute();
        eventsRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentEventIndex < eventsList.size() - 1) {
                    currentEventIndex++;
                } else {
                    currentEventIndex = 0;
                }
                eventsRecyclerView.smoothScrollToPosition(currentEventIndex);
                eventsRecyclerView.postDelayed(this, 3000);
            }
        }, 3000);

        return view;
    }

    private void openMap() {
        Uri uri = Uri.parse("geo:0,0?q=kongunadu arts and science,Coimbatore");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setActionBarTitle("Home");
    }

    private static class BranchModel {
        private final int img;
        private final String title;

        private BranchModel(int img, String title) {
            this.img = img;
            this.title = title;
        }

        private int getImg() {
            return img;
        }

        private String getTitle() {
            return title;
        }
    }

    private static class EventModel {
        private final String eventName;

        public EventModel(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return eventName;
        }
    }

    private static class SliderViewHolder extends SliderViewAdapter.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        private SliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.textDescription);
        }
    }

    private static class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
        private final List<EventModel> eventsList;

        public EventsAdapter(List<EventModel> eventsList) {
            this.eventsList = eventsList;
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
            return new EventViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            EventModel event = eventsList.get(position);
            holder.eventNameTextView.setText(event.getEventName());
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }

        public static class EventViewHolder extends RecyclerView.ViewHolder {
            TextView eventNameTextView;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                eventNameTextView = itemView.findViewById(R.id.event_name_text_view);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchEventsTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> eventNames = new ArrayList<>();
            try {
                // Make HTTP request to fetch webpage content
                Document doc = Jsoup.connect("https://www.kongunaducollege.ac.in/").get();

                for (Element element : doc.select(".views-field-title a")) {
                    eventNames.add(element.text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return eventNames;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(List<String> eventNames) {

            eventsList.clear();
            int count = 0;
            for (String eventName : eventNames) {
                eventsList.add(new EventModel(eventName));
                count++;
                if (count >= 10)
                    break;
            }
            adapter.notifyDataSetChanged();
        }

    }
}
