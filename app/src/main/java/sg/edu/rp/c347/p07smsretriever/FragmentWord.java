package sg.edu.rp.c347.p07smsretriever;


import android.Manifest;
import android.content.ContentResolver;
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
public class FragmentWord extends Fragment {

    Button btnRetrieve;
    TextView tvDisplay;
    EditText etWord;

    public FragmentWord() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        tvDisplay = view.findViewById(R.id.tvDisplay);
        btnRetrieve = view.findViewById(R.id.btnRetrieve);
        etWord = view.findViewById(R.id.etWord);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = etWord.getText().toString();
                if (word.equalsIgnoreCase("")){
                    Toast.makeText(getContext(), "Please enter a word into the text field", Toast.LENGTH_LONG).show();
                }else{
                    Uri uri = Uri.parse("content://sms");
                    String[] reqCols = new String[]{"date", "address", "body", "type"};
                    String filter = "body LIKE ?";
                    String[] arg =  {"%" + word + "%"};
                    ContentResolver cr = getActivity().getContentResolver();
                    Cursor cursor = cr.query(uri, reqCols, filter, arg, null);
                    if (word.contains(" ")){
                        String[] words = word.split(" ");
                        String[] args = new String[words.length];
                        args[0] = "%" + words[0] + "%";
                        for (int i = 1; i<(words.length); i++){
                            filter += "and body LIKE ?";
                            String fil = "%" + words[i] + "%";
                            args[i] = fil;
                        }

                         cursor = cr.query(uri, reqCols, filter, args, null);
                    }

                    String smsBody = "";
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
                            }else{
                                type = "Sent:";
                            }
                            smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";

                        }while (cursor.moveToNext());
                    }
                    tvDisplay.setText(smsBody);
                }
            }
        });
        return view;
    }

}
