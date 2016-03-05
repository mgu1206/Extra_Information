package yu_cse.graduation_project_edit.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * Created by gyeunguckmin on 11/24/15.
 */
public class EditTextFilter {

    public InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

}
