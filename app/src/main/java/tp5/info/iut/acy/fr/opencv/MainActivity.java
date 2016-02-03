package tp5.info.iut.acy.fr.opencv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import android.view.View.OnClickListener;


public class MainActivity extends Activity implements CvCameraViewListener2, OnClickListener {

    private CameraBridgeViewBase mOpenCvCameraView;

    // definition des variables statiques pour les valeurs de processingMode
    final static int FLAG_ORIGINAL = 1;
    final static int FLAG_FLOUTAGE = 2;
    final static int FLAG_SEUILLAGE = 3;
    final static int FLAG_DEBRUITAGE = 4;

    // initialisation de la processingMod et definition du bouton Original par default
    private int processingMode = FLAG_ORIGINAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCV);
        mOpenCvCameraView.setCvCameraViewListener(MainActivity.this);

        // recuperation des 4 boutons
        Button btnOriginal = (Button)findViewById(R.id.original);
        Button btnFloutage = (Button)findViewById(R.id.floutage);
        Button btnSeuillage = (Button)findViewById(R.id.seuillage);
        Button btnDebruitage = (Button)findViewById(R.id.debruitage);

        // bindage du onClick
        btnOriginal.setOnClickListener(this);
        btnFloutage.setOnClickListener(this);
        btnSeuillage.setOnClickListener(this);
        btnDebruitage.setOnClickListener(this);

    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    // Rendre OPEN CV utilisable
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCVManager setup", "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
                mLoaderCallback);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    // lancee des qu'une image a ete acquise
    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        // récupérer l'image acquise
        Mat input = inputFrame.rgba();

        // metre l'image en gris pour la passer sur 3 canaux
        Mat inputGray = inputFrame.gray();

        // modifie l image selon la valeur de processingMode
        switch (processingMode){

            case FLAG_ORIGINAL:
                Log.i("onCameraFrame en mode","original");
                break;
            case FLAG_SEUILLAGE:
                Log.i("onCameraFrame en mode","seuillage");

                // convertie l image en niveaux de gris
                Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2GRAY);

                // seuille l'image avec un valeur de tresh de 100, seuil maximum de 200 et types de seuillage THRESH_TOZERO
                Imgproc.threshold(input,input,100,200,Imgproc.THRESH_TOZERO);
                break;

            case FLAG_DEBRUITAGE:
                Log.i("onCameraFrame en mode", "debruitage");
                
                // debruite l image depuis l image en gris avec les parametres recommandees dans la documents officiels de OpenCV
                Photo.fastNlMeansDenoising(inputGray,input,3,7,21);
                break;
            case FLAG_FLOUTAGE:
                Log.i("onCameraFrame en mode","floutage");

                /* floute l'image avec la matiere de taille  45*45
                   input etant l'image qu'on modifie et celle ou affiche le resultat */
                Imgproc.GaussianBlur(input,input,new Size(45,45),0);
                break;
        }

        return input;
    }

    @Override
    public void onClick(View v) {

        Log.i("onClick","appelé");
        switch (v.getId()){
            case R.id.original:
                Log.i("bouton","original");
                processingMode = FLAG_ORIGINAL;
                break;
            case R.id.floutage:
                Log.i("bouton","floutage");
                processingMode = FLAG_FLOUTAGE;
                break;
            case R.id.seuillage:
                Log.i("bouton","seuillage");
                processingMode = FLAG_SEUILLAGE;
                break;
            case R.id.debruitage:
                Log.i("bouton","debruitage");
                processingMode = FLAG_DEBRUITAGE;
                break;
        }

    }
}
