package fraternityandroid.greeklife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class ConstitutionViewer extends AppCompatActivity {

    PDFView mConstitution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constitution_viewer);

        mConstitution = (PDFView)findViewById(R.id.pdfConst);
        mConstitution.fromAsset("Constitution.pdf").load();
    }
}
