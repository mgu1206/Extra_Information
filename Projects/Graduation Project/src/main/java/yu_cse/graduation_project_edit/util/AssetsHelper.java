package yu_cse.graduation_project_edit.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;import java.lang.Exception;import java.lang.String;import java.lang.System;

/**
 * Created by doom on 15/4/2.
 *
 * -------------------------------------------------------
 * Edited by Gyeung-Uk on 2015/10/6
 *
 * -- Before --> SVG file must be in Assets folder
 * -- After --> Anywhere, On internal storage of smart phone.
 * -- Assign the SVG file path(include file name ex>/xxx/yyy/sample.svg) to 'String filePath' as String value.
 * -- Contact : mgu1206@me.com / mgu1206@gmail.com
 *
 * -------------------------------------------------------
 *
 */
public class AssetsHelper
{
    public static String getContent(Context context, String filePath)
    {
        try
        {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufReader = new BufferedReader(inputStreamReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
            {
                Result += line;
            }

            System.out.print(Result);
            return Result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
