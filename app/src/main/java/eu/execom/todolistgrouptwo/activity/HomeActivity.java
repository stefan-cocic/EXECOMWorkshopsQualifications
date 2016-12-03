package eu.execom.todolistgrouptwo.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.RestClientException;

import java.util.List;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.adapter.TaskAdapter;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.model.Task;
import eu.execom.todolistgrouptwo.preference.UserPreferences_;
import eu.execom.todolistgrouptwo.util.Params;

/**
 * Home {@link AppCompatActivity Activity} for navigation and listing all tasks.
 */

// @OptionMenu by Stefan Ćoćić
@OptionsMenu(R.menu.menu_home)
@EActivity(R.layout.activity_home)
public class HomeActivity extends AppCompatActivity {

    /**
     * Used for logging purposes.
     */
    private static final String TAG = HomeActivity.class.getSimpleName();

    /**
     * Used for identifying results from different activities.
     */
    protected static final int ADD_TASK_REQUEST_CODE = 42;
    protected static final int LOGIN_REQUEST_CODE = 420; // BLAZE IT

    /* By Stefan Ćoćić */
    protected static final int TASK_DETAILS_REQUEST_CODE = 24;

    /**
     * Tasks are kept in this list during a user session.
     */
    private List<Task> tasks;

    /**
     * {@link FloatingActionButton FloatingActionButton} for starting the
     * {@link AddTaskActivity AddTaskActivity}.
     */
    @ViewById
    FloatingActionButton addTask;

    /**
     * {@link ListView ListView} for displaying the tasks.
     */
    @ViewById
    ListView listView;

    /**
     * {@link TaskAdapter Adapter} for providing data to the {@link ListView listView}.
     */
    @Bean
    TaskAdapter adapter;

    @Pref
    UserPreferences_ userPreferences;

    @RestService
    RestApi restApi;

    @AfterViews
    @Background
    void checkUser() {
        if (!userPreferences.accessToken().exists()) {
            LoginActivity_.intent(this).startForResult(LOGIN_REQUEST_CODE);
            return;
        }

        try {
            tasks = restApi.getAllTasks();
        } catch (RestClientException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        initData();
    }

    /**
     * Loads tasks from the {@link android.content.SharedPreferences SharedPreferences}
     * and sets the adapter.
     */
    @UiThread
    void initData() {
        listView.setAdapter(adapter);
        adapter.setTasks(tasks);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    // button show
                    addTask.show();
                } else {
                    // button hide
                    addTask.hide();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * Called when the {@link FloatingActionButton FloatingActionButton} is clicked.
     */
    @Click
    void addTask() {
        Log.i(TAG, "Add task clicked!");
        AddTaskActivity_.intent(this).startForResult(ADD_TASK_REQUEST_CODE);
    }

    /**
     * Called when the {@link AddTaskActivity AddTaskActivity} finishes.
     *
     * @param resultCode Indicates whether the activity was successful.
     * @param task       The new task.
     */
    @OnActivityResult(ADD_TASK_REQUEST_CODE)
    @Background
    void onResult(int resultCode, @OnActivityResult.Extra String task) {
        if (resultCode == RESULT_OK) {
            final Gson gson = new Gson();
            final Task newTask = gson.fromJson(task, Task.class);

            try {
                final Task newNewTask = restApi.createTask(newTask);
                onTaskCreated(newNewTask);
            } catch (Exception e) {
                /* By Stefan Ćoćić */
                showAddTaskError();

                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    @UiThread
    void onTaskCreated(Task task) {
        tasks.add(task);
        adapter.addTask(task);
    }

    @OnActivityResult(LOGIN_REQUEST_CODE)
    void onLogin(int resultCode, @OnActivityResult.Extra("token") String token) {
        if (resultCode == RESULT_OK) {
            userPreferences.accessToken().put(token);
            checkUser();
        }
    }

    /* By Stefan Ćoćić */
    @OptionsItem
    @Background
    void logout() {
        try {
            restApi.logout();
            backToLogin();
        } catch (RestClientException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            showLogoutError();
        }
    }

    /* By Stefan Ćoćić */
    @UiThread
    void backToLogin() {
        userPreferences.clear();
        tasks.clear();
        adapter.clear();
        LoginActivity_.intent(this).startForResult(LOGIN_REQUEST_CODE);
    }

    /* By Stefan Ćoćić */
    @UiThread
    void showLogoutError() {
        Toast.makeText(this, "Can't log out. Try later.", Toast.LENGTH_SHORT).show();
    }

    /* By Stefan Ćoćić */
    @UiThread
    void showAddTaskError() {
        Toast.makeText(this, "Log in first.", Toast.LENGTH_SHORT).show();
    }

    /* By Stefan Ćoćić */
    @ItemClick(R.id.listView)
    public void onTaskClick(Task task) {
        final Gson gson = new Gson();
        final TaskDetailsActivity_.IntentBuilder_ intent = TaskDetailsActivity_.intent(this);

        intent.taskExtra(gson.toJson(task)).startForResult(TASK_DETAILS_REQUEST_CODE);
    }

    /* By Stefan Ćoćić */
    @OnActivityResult(TASK_DETAILS_REQUEST_CODE)
    void onTaskDetailsResult(int resultCode, @OnActivityResult.Extra(Params.TASK) String task) {
        if (resultCode == RESULT_OK) {
            final Gson gson = new Gson();
            final Task newTask = gson.fromJson(task, Task.class);

            adapter.updateTask(newTask);
        }
    }
}
