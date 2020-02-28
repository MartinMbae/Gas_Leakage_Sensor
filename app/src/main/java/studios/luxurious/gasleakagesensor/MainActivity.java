package studios.luxurious.gasleakagesensor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {


    public static final String NOTIFICATION = "PushNotification";
    String token;
    DatabaseReference firebaseDatabase;
    DatabaseReference rootDatabase;

    TextView currentTxv;
    Boolean isShowingDialg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

 
       currentTxv =  findViewById(R.id.gasLevels);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("GasLeakage");
        rootDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Get Instance Failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        token = task.getResult().getToken();
                        FirebaseMessaging.getInstance().subscribeToTopic("All");
                        rootDatabase.child("token").setValue(token);

                    }
                });

        rootDatabase.child("CurrentLevel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String current = dataSnapshot.getValue().toString();

                   String value = "Current Gas Levels : "+ current;

                  currentTxv.setText(value);

                  if (Float.parseFloat(current ) > 300){
                      showDialog();
                  }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    
    
    private void showDialog(){

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Gas leakage detected");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isShowingDialg = false;
            }
        });

        if (!isShowingDialg) {
            alertDialogBuilder.show();
            isShowingDialg = true;
        }
        
    }

}
