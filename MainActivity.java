package com.example.kvizuma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btSettings, btCloseSettings, btInfoMath, btInfoGeo;
    private ConstraintLayout constraintLayouts;
    private TextView informationText;
    private ConstraintLayout infoLayout;
    private ConstraintSet resetConstraintSet = new ConstraintSet();
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private Switch swEuro, swAsia, swAfrica, swNAmerica, swSAmericaAus;
    public static boolean[] continentList = new boolean[]{true, true, true, true, true};

    public void setBtSettings(Button btSettings) {
        this.btSettings = btSettings;
    }

    public void setBtCloseSettings(Button btCloseSettings) {
        this.btCloseSettings = btCloseSettings;
    }

    public void setSwEuro(Switch swEuro) {
        this.swEuro = swEuro;
    }

    public void setSwAsia(Switch swAsia) {
        this.swAsia = swAsia;
    }

    public void setSwAfrica(Switch swAfrica) {
        this.swAfrica = swAfrica;
    }

    public void setSwNAmerica(Switch swNAmerica) {
        this.swNAmerica = swNAmerica;
    }

    public void setSwSAmericaAus(Switch swSAmericaAus) {
        this.swSAmericaAus = swSAmericaAus;
    }

    public void setBtInfoMath(Button btInfoMath) {
        this.btInfoMath = btInfoMath;
    }

    public void setBtInfoGeo(Button btInfoGeo) {
        this.btInfoGeo = btInfoGeo;
    }

    public void setInformationText(TextView informationText) {
        this.informationText = informationText;
    }

    public void setInfoLayout(ConstraintLayout infoLayout) {
        this.infoLayout = infoLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setBtCloseSettings(findViewById(R.id.button_closeSettings));
        setBtSettings(findViewById(R.id.button_settingsGeo));
        setSwAfrica(findViewById(R.id.switch_africa));
        setSwAsia(findViewById(R.id.switch_asia));
        setSwEuro(findViewById(R.id.switch_europe));
        setSwNAmerica(findViewById(R.id.switch_nAmerica));
        setSwSAmericaAus(findViewById(R.id.switch_sAmericaAndAustralija));
        setBtInfoMath(findViewById(R.id.button_infoMath));
        setBtInfoGeo(findViewById(R.id.button_infoGeo));
        setInfoLayout(findViewById(R.id.infoLayout));
        setInformationText(findViewById(R.id.textInformation));

        constraintLayouts = findViewById(R.id.linearLayout);
        resetConstraintSet.clone(constraintLayouts);
        applyConstraintSet.clone(constraintLayouts);

        swEuro.setChecked(continentList[0]);
        swAsia.setChecked(continentList[1]);
        swAfrica.setChecked(continentList[2]);
        swNAmerica.setChecked(continentList[3]);
        swSAmericaAus.setChecked(continentList[4]);

        switchOnChange(swEuro, 0);
        switchOnChange(swAsia, 1);
        switchOnChange(swAfrica, 2);
        switchOnChange(swNAmerica, 3);
        switchOnChange(swSAmericaAus, 4);

        Button button = (Button) findViewById(R.id.button_matematika);
        button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), ActivityMatematika.class);
            startActivity(i);
        }
    });
        Button button_geo = (Button) findViewById(R.id.button_geografija);
        button_geo.setOnClickListener(view -> {
            if((!continentList[0]) && (!continentList[1]) && (!continentList[2]) && (!continentList[3]) && (!continentList[4])) Toast.makeText(getBaseContext(), "Barem jedan kontinent treba biti uključen!", Toast.LENGTH_SHORT).show();
            else {
                Intent i = new Intent(view.getContext(), ActivityGeography.class);
                startActivity(i);
            }
        });

        btSettings.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(constraintLayouts);
            applyConstraintSet.setMargin(R.id.settingFrame, ConstraintSet.BOTTOM, 700);
            applyConstraintSet.setVisibility(R.id.settingFrame, ConstraintSet.VISIBLE);
            applyConstraintSet.applyTo(constraintLayouts);
            ViewGroup.LayoutParams lp = findViewById(R.id.settingFrame).getLayoutParams();
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;

            infoLayout.setVisibility(View.INVISIBLE);
            btInfoGeo.setVisibility(View.INVISIBLE);
            btInfoMath.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
            button_geo.setVisibility(View.INVISIBLE);
            btSettings.setVisibility(View.INVISIBLE);
            findViewById(R.id.settingFrame).setLayoutParams(lp);
        });

        btInfoMath.setOnClickListener(view -> infoButtonDo("Matematika ima četiri operacije: Sabiranje, oduzimanje, množenje i deljenje. Svaka operacija ima" +
                " pet nivoa koja rastu po težini, za svaki nivo imate odećeno vreme do kojeg se mora dati odgovor. Što se ranije odgovori više će te dobiti poena." +
                " Kada obavite svih pet nivoa jedne operacije sledeća operacija će se odkjučati. Možete svaku operaciju da ponovite nakon završetka, ali poeni će" +
                " se računati samo ako dobijete više poena od vašeg najboljeg pokušaja. Konačni tes se satoji od svih operacija i težina koje će se pojaviti" +
                " nasumično. Možete dati dvadeset taćnih odgovora za redom, ukoliko pogrešite vaši oeni će se još uvek računati ali će se konačni test završiti." +
                " Kada završite konačni možete uneti vaše poene u rekorde, ali posle toga će se poeni resetovati na nulu i sve operacije će se zaključati osim prve." +
                " Svaka operacija ima svoj najbolji rekord kao i finalni nivo koje možete pogledati u rekordima."));

        btInfoGeo.setOnClickListener(view -> infoButtonDo("Geografija ima tri kviza kviz zastava, granica i glavnih gradova. U roku od nekoliko sekundi možete" +
                " izabrati jedno od šest ponućenih odgovora na pitanje i predati ga. Količina dobijenih poena zavisi od brzine predaje odgovora. Uspešan tzavršetak" +
                " kviza zastava odključava kvizgraknica. koji odljučava kviz glavnih gradova, koji pritom odkjučava konačni kviz. Konačni kviz se sastoji od svih" +
                " tri kvizova i nosi određeni broj poena po težini nezavisno od vremena. U finalnom kvizu imate minutipo da odgovorite na što više pitanja, netačni" +
                " odgovori oduzimaju pojene ali ne prekidaju kviz. Kraj konačnog kviza će zaključati prethodno odključane kvizove, dopustiti da unesete vaše poene" +
                " u rekorde nakon čega će biti resetovani. Možete skupiti više poena na ponavljanju pretkodno završenog kviza samo ako dobijete više poena od" +
                " najboljeg pokušaja. Upodešavanjima pored dugmeta geografija, možete podesiti težinu kvizova, odnosno izabrati koji kontinenti će biti u kvizu," +
                " što više kontinenata je uključeno to će te više dobijati poena. Kontinente možete podešavati u bilo koje vreme."));


        informationText.setMovementMethod(new ScrollingMovementMethod());

        btCloseSettings.setOnClickListener(view -> resetConstraintSet.applyTo(constraintLayouts));

    }

    private void switchOnChange(Switch sw, int listPosition){
        sw.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                continentList[listPosition] = true;
            }else{
                continentList[listPosition] = false;
            }
        });

    }

    private void infoButtonDo(String text){
        infoLayout.setVisibility(View.VISIBLE);
        informationText.setText(text);
    }

}