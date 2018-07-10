package app.insti.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import app.insti.Constants;
import app.insti.ItemClickListener;
import app.insti.MainActivity;
import app.insti.R;
import app.insti.adapter.NotificationsAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.api.model.AppNotification;
import app.insti.api.model.NotificationsResponse;
import app.insti.data.Notification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends BaseFragment {

    RecyclerView notificationsRecyclerView;

    List<Notification> notifications;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Notifications");

        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getNotifications(((MainActivity)getActivity()).getSessionIDHeader()).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    notifications = response.body();
                    showNotifications(notifications);
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {

            }
        });
    }

    private void showNotifications(final List<Notification> notifications) {
        NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notifications, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                /* Get the notification */
                Notification notification = notifications.get(position);

                /* Open event */
                if (notification.getNotificationActorType().contains("event")) {
                    String eventJson = new Gson().toJson(notification.getNotificationActor());
                    Bundle bundle = getArguments();
                    if (bundle == null) bundle = new Bundle();
                    bundle.putString(Constants.EVENT_JSON, eventJson);
                    EventFragment eventFragment = new EventFragment();
                    eventFragment.setArguments(bundle);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                    transaction.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                    transaction.addToBackStack(eventFragment.getTag()).commit();
                }
            }
        });
        notificationsRecyclerView = (RecyclerView) getActivity().findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setAdapter(notificationsAdapter);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
