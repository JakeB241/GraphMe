package umich.jakebock.trackme.firebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

import umich.jakebock.trackme.R;
import umich.jakebock.trackme.activities.LoginActivity;
import umich.jakebock.trackme.classes.DataProject;

/**
 * Created by Jake on 1/24/2018.
 */

public class FirebaseHandler
{
    // Constants - Data Project
    private static final String DATA_PROJECTS               = "dataProjects";
    private static final String DATA_PROJECT_UPDATE_TIME    = "updatedTime";
    private DocumentReference userDocumentReference;

    private FirebaseFirestore firebaseFirestore;
    private RelativeLayout    loadingView;

    private DataLoadCompletedListener dataLoadCompletedListener;

    public FirebaseHandler(Activity activity)
    {
        // Get the FireBase Instance
        this.firebaseFirestore = FirebaseFirestore.getInstance();

        // Fetch the Loading View
        loadingView = activity.findViewById(R.id.loading_view);

        // Fetch the User Document Reference
        userDocumentReference = firebaseFirestore.collection("users").document(LoginActivity.currentUserId);
    }

    public void createProject(DataProject dataProject)
    {
        // Set the Loading Visibility
        loadingView.setVisibility(View.VISIBLE);

        // Set the Data Project Update Time to the Current Time
        dataProject.setUpdatedTime(new Date());

        // Add the Data Project
        userDocumentReference.collection(DATA_PROJECTS).document(dataProject.getProjectTitle()).set(dataProject).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    // Call Data Project Created Completed
                    dataLoadCompletedListener.dataProjectCreatedCompleted();

                    // Set the Loading Visibility
                    loadingView.setVisibility(View.GONE);
                }

                else
                {
                    // Set the Loading Visibility
                    loadingView.setVisibility(View.GONE);
                }
            }
        });
    }


    public void deleteProjects(final ArrayList<DataProject> dataProjects)
    {
        // Create the WriteBatch
        WriteBatch batch = firebaseFirestore.batch();

        // Loop through the Data Projects and Add them to be Deleted
        for (DataProject dataProject : dataProjects)
            batch.delete(userDocumentReference.collection(DATA_PROJECTS).document(dataProject.getProjectTitle()));

        // Commit the Batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    // Set the Data Projects Deleted Completed
                    dataLoadCompletedListener.dataProjectsDeletedCompleted(dataProjects);

                    // Set the Loading Visibility
                    loadingView.setVisibility(View.GONE);
                }

                else
                {
                    // Set the Loading Visibility
                    loadingView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void loadProjects()
    {
        // Set the Loading Visibility
        loadingView.setVisibility(View.VISIBLE);

        // Fetch all of the Data Projects from the FireBase FireStore
        userDocumentReference.collection(DATA_PROJECTS).orderBy(DATA_PROJECT_UPDATE_TIME, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    // Create the ArrayList of Data Projects
                    ArrayList<DataProject> dataProjectArrayList = new ArrayList<>();

                    // Add the Data Projects
                    dataProjectArrayList.addAll(task.getResult().toObjects(DataProject.class));

                    // Set the Data Load Completed
                    dataLoadCompletedListener.dataProjectLoadCompleted(dataProjectArrayList);

                    // Set the Loading Visibility
                    loadingView.setVisibility(View.GONE);
                }

                else
                {
                    // Set the Loading Visibility
                    loadingView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void projectExists(String projectTitle)
    {
        // Set the Loading Visibility
        loadingView.setVisibility(View.VISIBLE);

        // Query FireStore for a Data Project with the Title
        userDocumentReference.collection(DATA_PROJECTS).document(projectTitle).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    // Set the Data Project Exists Completed
                    dataLoadCompletedListener.dataProjectExistsCompleted(task.getResult().exists());

                    // Set the Loading Visibility
                    loadingView.setVisibility(View.GONE);
                }

                else
                {
                    // Set the Loading Visibility
                    loadingView.setVisibility(View.GONE);
                }
            }
        });
    }

    // Define the Listener
    public interface DataLoadCompletedListener
    {
        void dataProjectCreatedCompleted ();
        void dataProjectLoadCompleted    (ArrayList<DataProject> loadedDataProjects );
        void dataProjectsDeletedCompleted(ArrayList<DataProject> deletedDataProjects);
        void dataProjectExistsCompleted  (Boolean projectExists                     );
    }

    // Set the Listener for the Adapter
    public void setListener(DataLoadCompletedListener listener)
    {
        this.dataLoadCompletedListener = listener;
    }
}
