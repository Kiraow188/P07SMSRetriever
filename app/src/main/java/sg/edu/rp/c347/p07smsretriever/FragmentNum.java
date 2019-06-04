package sg.edu.rp.c347.p07smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNum extends Fragment {

    Button btnRetrieve, btnEmail;
    TextView tvDisplay;
    EditText etNum;
    String smsBody;

    public FragmentNum() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_num, container, false);

        tvDisplay = view.findViewById(R.id.tvDisplay);
        btnRetrieve = view.findViewById(R.id.btnRetrieve);
        etNum = view.findViewById(R.id.etPhone);
        btnEmail = view.findViewById(R.id.btnEmail);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNum.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getContext(), "Please enter a number into the text field", Toast.LENGTH_LONG).show();
                }else{
                    Uri uri = Uri.parse("content://sms");
                    String[] reqCols = new String[]{"date", "address", "body", "type"};
                    String filter = "address LIKE ?";
                    String[] args = {"%"+etNum.getText().toString()+"%"};
                    ContentResolver cr = getActivity().getContentResolver();
                    Cursor cursor = cr.query(uri, reqCols, filter, args, null);
                    smsBody = "";
                    if (cursor.moveToFirst()){
                        do{
                            android.text.format.DateFormat df = new android.text.format.DateFormat();
                            long dateInMillis = cursor.getLong(0);
                            String date = (String)df.format("dd MMM yyyy h:mm:ss aa", dateInMillis);

                            String address = cursor.getString(1);
                            String body = cursor.getString(2);
                            String type = cursor.getString(3);
                            if (type.equalsIgnoreCase("1")){
                                type = "Inbox:";
                                smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";
                            }
                        }while (cursor.moveToNext());
                    }
                    tvDisplay.setText(smsBody);
                }
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (smsBody.equalsIgnoreCase("")){
                    Toast.makeText(getContext(), "Retrieve SMS first", Toast.LENGTH_LONG).show();
                }else{
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{"17010368@myrp.edu.sg"});
                    email.putExtra(Intent.EXTRA_SUBJECT, "SMS contents");
                    email.putExtra(Intent.EXTRA_TEXT, smsBody);

                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client: "));
                }
            }
        });

        return view;
    }
}
