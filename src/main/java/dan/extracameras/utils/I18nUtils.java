package dan.extracameras.utils;

import net.minecraft.client.resource.language.I18n;

import java.util.ArrayList;
import java.util.List;

public class I18nUtils {


    public static String getString(String translateWord, Object... args) {
        return I18n.translate(translateWord, args);
    }
}
