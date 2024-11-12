package br.com.cursoandroid.simplenote.utils;

import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import android.app.Activity;
import br.com.cursoandroid.simplenote.R;

public class ThemeUtils {
    public static void applyThemeFromPreferences(Activity activity) {
        if (activity != null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String themePreference = sharedPreferences.getString("theme", "DefaultTheme");


                switch (themePreference) {
                    case "BlueTheme":
                        activity.setTheme(R.style.Theme_Blue);
                        break;
                    case "GreenTheme":
                        activity.setTheme(R.style.Theme_Green);
                        break;
                    case "AmberTheme":
                        activity.setTheme(R.style.Theme_Amber);
                        break;
                    case "OrangeTheme":
                        activity.setTheme(R.style.Theme_Orange);
                        break;
                    case "PurpleTheme":
                        activity.setTheme(R.style.Theme_Purple);
                        break;
                    case "DeepPurpleTheme":
                        activity.setTheme(R.style.Theme_DeepPurple);
                        break;
                    case "IndigoTheme":
                        activity.setTheme(R.style.Theme_Indigo);
                        break;
                    case "DeepOrangeTheme":
                        activity.setTheme(R.style.Theme_DeepOrange);
                        break;
                    case "TealTheme":
                        activity.setTheme(R.style.Theme_Teal);
                        break;
                    default:
                        activity.setTheme(R.style.Theme_SimpleNote);
                }
        }
    }

}
