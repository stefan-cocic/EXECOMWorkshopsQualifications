package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.RestClientException;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.model.Task;
import eu.execom.todolistgrouptwo.util.Params;

/*  By Stefan Ćoćić */
@EActivity(R.layout.activity_task_details)
public class TaskDetailsActivity extends AppCompatActivity {

    @ViewById
    EditText title;

    @ViewById
    EditText description;

    @ViewById
    CheckBox finished;

    @Extra
    String taskExtra;

    @RestService
    RestApi restApi;

    private Task task;
    private Gson gson;

    @AfterInject
    void afterInject() {
        gson = new Gson();
        task = gson.fromJson(taskExtra, Task.class);
    }

    @AfterViews
    void afterViews() {
        title.setText(task.getTitle());
        description.setText(task.getDescription());
        finished.setChecked(task.isFinished());
    }

    @Background
    @Click(R.id.update)
    void onUpdate() {
        collectData();

        try {
            final Task newTask = restApi.updateTask(task.getId(), task);

            setFinished(newTask);
        } catch (RestClientException e) {
            showUpdateError();
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    @UiThread
    void setFinished(Task task) {
        final Intent intent = new Intent();
        intent.putExtra(Params.TASK, gson.toJson(task));

        setResult(RESULT_OK, intent);
        finish();
    }

    @UiThread
    void collectData() {
        task.setTitle(title.getText().toString());
        task.setDescription(description.getText().toString());
        task.setFinished(finished.isChecked());
    }

    @UiThread
    void showUpdateError() {
        Toast.makeText(this, "Can't update task now. Try later.", Toast.LENGTH_SHORT).show();
    }
}
