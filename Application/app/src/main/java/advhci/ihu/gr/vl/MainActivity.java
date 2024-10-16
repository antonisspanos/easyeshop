package advhci.ihu.gr.vl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    public static FragmentManager fragmentManager;
    public static adhvci.ihu.gr.vl.db_stuff.LocalDb sqlite;
    public static FirebaseFirestore fireslow;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        fireslow = FirebaseFirestore.getInstance();
        sqlite = Room.databaseBuilder(getApplicationContext(),
                adhvci.ihu.gr.vl.db_stuff.LocalDb.class,
                "dbv").allowMainThreadQueries().build();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);

        createNotificationChannel();
        DrawerMenuInit();
    }

    private void DrawerMenuInit()
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.productz:
                        drawerLayout.closeDrawers();
                        MainActivity.fragmentManager.beginTransaction()
                                .replace(R.id.drawer_layout, new ProductsFragment())
                                .addToBackStack(null)
                                .commit();
                        return true;
                    case R.id.tranz:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        MainActivity.fragmentManager.beginTransaction()
                                .replace(R.id.drawer_layout, new TransactionsFragment())
                                .addToBackStack(null)
                                .commit();
                        return true;
                    case R.id.suppz:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        MainActivity.fragmentManager.beginTransaction()
                                .replace(R.id.drawer_layout, new SupplierFragment())
                                .addToBackStack(null)
                                .commit();
                        return true;
                    case R.id.suplsz:
                        menuItem.setChecked(true);
                        MainActivity.fragmentManager.beginTransaction()
                                .replace(R.id.drawer_layout, new SuppliesFragment())
                                .addToBackStack(null)
                                .commit();
                        drawerLayout.closeDrawers();
                        return true;
                    default:
                        drawerLayout.closeDrawers();
                        return false;
                }
            }
        });
    }
    public static void pushNotification(android.content.Context Self, String message) {
        if (ActivityCompat
        .checkSelfPermission(Self, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            return;

        Uri alarmSound = RingtoneManager
        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(Self, "notificationId")
                .setContentText("Application")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setContentText(message)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Self);
        managerCompat.notify(999, builder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notifications";
            String description = "notificationDesc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notificationId", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
