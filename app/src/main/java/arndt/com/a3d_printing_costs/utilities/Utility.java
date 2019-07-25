package arndt.com.a3d_printing_costs.utilities;

import android.content.Context;

import java.lang.reflect.Field;

public class Utility {
    /**
     * Returns Identifier of String into it's ID as defined in R.java file.
     * @param pContext
     * @param pString defnied in Strings.xml resource name e.g: action_item_help
     * @return
     */
    public static int getStringIdentifier(Context pContext, String pString){
        return pContext.getResources().getIdentifier(pString, "string", pContext.getPackageName());
    }
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
